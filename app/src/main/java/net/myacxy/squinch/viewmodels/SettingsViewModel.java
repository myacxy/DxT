package net.myacxy.squinch.viewmodels;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import net.myacxy.retrotwitch.utils.StringUtil;
import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.retrotwitch.v5.api.common.Direction;
import net.myacxy.retrotwitch.v5.api.common.Error;
import net.myacxy.retrotwitch.v5.api.common.SortBy;
import net.myacxy.retrotwitch.v5.api.common.TwitchConstants;
import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.retrotwitch.v5.api.users.SimpleUsersResponse;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.retrotwitch.v5.helpers.RxErrorFactory;
import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.models.SettingsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SettingsViewModel implements ViewModel {

    private final DataHelper dataHelper;

    public SettingsModel settings;
    public ObservableBoolean loadingUser = new ObservableBoolean();
    public ObservableField<String> userLogo = new ObservableField<>();
    public ObservableField<String> userError = new ObservableField<>();
    public ObservableField<String> selectedChannelsText = new ObservableField<>("0\u2009/\u20090");

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SettingsViewModel(DataHelper dataHelper) {
        this.dataHelper = dataHelper;
        settings = dataHelper.recoverSettings();
        updateSelectedChannelsText();
    }

    public void onUserNameChanged(String userName) {
        compositeDisposable.clear();
        if (userName != null && (userName = userName.trim()).length() != 0) {
            loadingUser.set(true);

            Disposable disposable = RxRetroTwitch.getInstance()
                    .users()
                    .translateUserNameToUserId(userName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(getUserObserver());

            compositeDisposable.add(disposable);

        } else {
            settings.setUser(null);
            userLogo.set(null);
            userError.set("user name must not be empty");
        }
    }

    public void onHideExtensionChanged(boolean hide) {
        settings.setHideEmptyExtension(hide);
        dataHelper.setHideEmptyExtension(hide);
    }

    private void updateSelectedChannelsText() {
        List<UserFollow> userFollows = settings.getUserFollows();
        if (!userFollows.isEmpty()) {
            int followsCount = userFollows.size();
            int deselectedChannels = 0;
            for (UserFollow deselected : settings.getDeselectedFollows()) {
                if (userFollows.contains(deselected)) {
                    deselectedChannels += 1;
                }
            }
            String text = String.format(Locale.getDefault(), "%d\u2009/\u2009%d", followsCount - deselectedChannels, followsCount);
            selectedChannelsText.set(text);
            return;
        }
        selectedChannelsText.set("0\u2009/\u20090");
    }

    private void onUserError(Error error) {
        dataHelper.setUser(null);
        settings.setUser(null);
        loadingUser.set(false);
        userLogo.set(null);
        userError.set(error.getMessage());
    }

    private void onUserFollowsError(Error error) {
        loadingUser.set(false);
        userError.set(error.getMessage());
        updateSelectedChannelsText();
    }

    private DisposableObserver<Response<SimpleUsersResponse>> getUserObserver() {
        return new DisposableObserver<Response<SimpleUsersResponse>>() {

            @Override
            public void onNext(Response<SimpleUsersResponse> response) {
                Error error = RxErrorFactory.fromResponse(response);
                if (error != null) {
                    onUserError(error);
                    return;
                }

                SimpleUser user = response.body().getUsers().get(0);
                settings.setUser(user);
                dataHelper.setUser(user);
                if (user != null) {
                    userLogo.set(user.getLogo());
                }
            }

            @Override
            public void onComplete() {
                SimpleUser user = settings.getUser();
                if (user != null && !StringUtil.isEmpty(user.getName())) {
                    Observable.range(0, Integer.MAX_VALUE)
                            .concatMap(page ->
                                    RxRetroTwitch.getInstance()
                                            .users()
                                            .getUserFollows(
                                                    user.getId(),
                                                    TwitchConstants.MAX_LIMIT,
                                                    page * TwitchConstants.MAX_LIMIT,
                                                    Direction.DEFAULT,
                                                    SortBy.DEFAULT
                                            )
                            )
                            .takeUntil(response -> response.code() != 200 || response.body().getUserFollows().size() == 0)
                            .reduceWith(() -> new ArrayList<UserFollow>(), (userFollows, response) -> {
                                if (response.code() != 200) {
                                    throw new HttpException(response);
                                }
                                userFollows.addAll(response.body().getUserFollows());
                                return userFollows;
                            })
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(new SingleObserver<List<UserFollow>>() {
                                @Override
                                public void onSubscribe(Disposable disposable) {
                                    compositeDisposable.add(disposable);
                                }

                                @Override
                                public void onSuccess(List<UserFollow> userFollows) {
                                    loadingUser.set(false);
                                    settings.setUserFollows(userFollows);
                                    dataHelper.setUserFollows(userFollows);
                                    updateSelectedChannelsText();
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    onUserFollowsError(RxErrorFactory.fromThrowable(throwable));
                                }
                            });
                } else {
                    loadingUser.set(false);
                }
            }

            @Override
            public void onError(Throwable t) {
                onUserError(RxErrorFactory.fromThrowable(t));
            }
        };
    }
}
