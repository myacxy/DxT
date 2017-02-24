package net.myacxy.squinch.viewmodels;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.crashlytics.android.Crashlytics;
import com.google.android.apps.dashclock.api.DashClockExtension;

import net.myacxy.retrotwitch.utils.StringUtil;
import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.retrotwitch.v5.api.common.Error;
import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.retrotwitch.v5.api.users.SimpleUsersResponse;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.retrotwitch.v5.helpers.RxErrorFactory;
import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.models.SettingsModel;
import net.myacxy.squinch.models.events.DashclockUpdateEvent;
import net.myacxy.squinch.utils.RetroTwitchUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SettingsViewModel implements ViewModel {

    private final DataHelper dataHelper;

    public SettingsModel settings;

    public ObservableBoolean isLoadingUser = new ObservableBoolean();
    public ObservableField<String> userLogo = new ObservableField<>();
    public ObservableField<String> userError = new ObservableField<>();
    public ObservableField<String> selectedChannelsText = new ObservableField<>("0\u2009/\u20090");

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private SimpleUser tmpUser;
    private List<Long> deselectedChannelIds;

    public SettingsViewModel(DataHelper dataHelper) {
        this.dataHelper = dataHelper;
        settings = dataHelper.recoverSettings();
        refresh();
    }

    public void refresh() {
        userError.set(null);
        userLogo.set(settings.getUser() != null ? settings.getUser().getLogo() : null);
        deselectedChannelIds = dataHelper.getDeselectedChannelIds();
        updateSelectedChannelsText(dataHelper.getUserFollows(), deselectedChannelIds);
    }

    public Consumer<String> getUser() {
        return userName -> {
            compositeDisposable.clear();
            userError.set(null);
            tmpUser = null;

            if (userName != null && (userName = userName.trim()).length() != 0) {
                isLoadingUser.set(true);

                Disposable disposable = RxRetroTwitch.getInstance()
                        .users()
                        .translateUserNameToUserId(userName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(getUserObserver());

                compositeDisposable.add(disposable);
            }
        };
    }

    public void onHideExtensionChanged(boolean hide) {
        settings.setHideEmptyExtension(hide);
        dataHelper.setHideEmptyExtension(hide);
    }

    private void updateSelectedChannelsText(final List<UserFollow> userFollows, List<Long> deselectedChannelIds) {
        if (!userFollows.isEmpty()) {
            Observable.fromIterable(userFollows)
                    .filter(userFollow -> deselectedChannelIds.contains(userFollow.getChannel().getId()))
                    .count()
                    .subscribeOn(Schedulers.computation())
                    .subscribe((deselected, t) -> {
                        String text = String.format(Locale.getDefault(), "%d\u2009/\u2009%d", userFollows.size() - deselected, userFollows.size());
                        selectedChannelsText.set(text);
                    });
            return;
        }
        selectedChannelsText.set("0\u2009/\u20090");
    }

    private void onUserError(Error error) {
        settings.setUser(null);
        settings.setUserFollows(null);
        dataHelper.setUser(null);
        dataHelper.setUserFollows(null);

        userLogo.set(null);
        isLoadingUser.set(false);
        userError.set(error.getMessage());
        updateSelectedChannelsText(Collections.emptyList(), deselectedChannelIds);
    }

    private void onUserFollowsError(Error error) {
        onUserError(error);
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

                tmpUser = response.body().getUsers().get(0);
            }

            @Override
            public void onComplete() {
                if (tmpUser != null && !StringUtil.isEmpty(tmpUser.getName())) {
                    RetroTwitchUtil.getAllUserFollows(tmpUser.getId(), progress -> updateSelectedChannelsText(progress, deselectedChannelIds))
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(new SingleObserver<List<UserFollow>>() {
                                @Override
                                public void onSubscribe(Disposable disposable) {
                                    compositeDisposable.add(disposable);
                                }

                                @Override
                                public void onSuccess(List<UserFollow> userFollows) {
                                    SimpleUser user = tmpUser;
                                    settings.setUser(user);
                                    settings.setUserFollows(userFollows);
                                    dataHelper.setUser(user);
                                    dataHelper.setUserFollows(userFollows);

                                    updateSelectedChannelsText(userFollows, deselectedChannelIds);
                                    userLogo.set(user.getLogo());
                                    isLoadingUser.set(false);

                                    RetroTwitchUtil.getAllLiveStreams(userFollows, progress -> {
                                    })
                                            .doOnError(Crashlytics::logException)
                                            .subscribeOn(Schedulers.io())
                                            .subscribe(streams -> {
                                                dataHelper.setLiveStreams(streams);
                                                EventBus.getDefault().post(new DashclockUpdateEvent(DashClockExtension.UPDATE_REASON_SETTINGS_CHANGED));
                                            });
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    onUserFollowsError(RxErrorFactory.fromThrowable(throwable));
                                }
                            });
                } else {
                    isLoadingUser.set(false);
                }
            }

            @Override
            public void onError(Throwable t) {
                onUserError(RxErrorFactory.fromThrowable(t));
            }
        };
    }
}
