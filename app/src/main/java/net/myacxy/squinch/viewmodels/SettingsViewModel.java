package net.myacxy.squinch.viewmodels;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import net.myacxy.retrotwitch.utils.StringUtil;
import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.retrotwitch.v5.api.common.Error;
import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.retrotwitch.v5.api.users.SimpleUsersResponse;
import net.myacxy.retrotwitch.v5.api.users.UserFollowsResponse;
import net.myacxy.retrotwitch.v5.helpers.RxErrorFactory;
import net.myacxy.squinch.helpers.SharedPreferencesHelper;
import net.myacxy.squinch.models.SettingsModel;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SettingsViewModel {
    public SettingsModel settings;
    public ObservableBoolean loadingUser = new ObservableBoolean();
    public ObservableField<String> userLogo = new ObservableField<>();
    public ObservableField<String> userError = new ObservableField<>();
    public ObservableField<String> selectedChannelsText = new ObservableField<>("0\u2009/\u20090");
    private SharedPreferencesHelper sharedPreferencesHelper;
    private UserFollowsResponse mUserFollowsResponse;

    private DisposableObserver disposale;

    public SettingsViewModel(SharedPreferencesHelper sharedPreferencesHelper) {
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        settings = sharedPreferencesHelper.createDefaultSettings();
    }

    public void onUserNameChanged(String userName) {
        if (userName != null && (userName = userName.trim()).length() != 0) {
            reset();

            loadingUser.set(true);

            RxRetroTwitch.getInstance()
                    .users()
                    .translateUserNameToUserId(userName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(disposale = getUserObserver());
        } else {
            settings.setUser(null);
            userLogo.set(null);
            userError.set("user name must not be empty");
        }
    }

    public void onHideExtensionChanged(boolean hide) {
        settings.setHideEmptyExtension(hide);
        sharedPreferencesHelper.setHideEmptyExtension(hide);
    }

    private void updateSelectedChannelsText() {
        if (mUserFollowsResponse != null) {
            int followsCount = mUserFollowsResponse.getUserFollows().size();
            String text = String.format(Locale.getDefault(), "%d\u2009/\u2009%d", followsCount, followsCount);
            selectedChannelsText.set(text);
            return;
        }
        selectedChannelsText.set("0\u2009/\u20090");
    }

    private void reset() {
        if (disposale != null) {
            disposale.dispose();
        }

        sharedPreferencesHelper.setUser(null);
        settings.setUser(null);
        userLogo.set(null);
        userError.set(null);
    }

    private DisposableObserver<Response<SimpleUsersResponse>> getUserObserver() {
        return new DisposableObserver<Response<SimpleUsersResponse>>() {

            @Override
            public void onNext(Response<SimpleUsersResponse> response) {
                SimpleUser user = response.body().getUsers().get(0);
                settings.setUser(user);
                sharedPreferencesHelper.setUser(user);
                if (user != null) {
                    userLogo.set(user.getLogo());
                }
            }

            @Override
            public void onComplete() {
                SimpleUser user = settings.getUser();
                if (user != null && !StringUtil.isEmpty(user.getName())) {
                    RxRetroTwitch.getInstance()
                            .users()
                            .getUserFollows(user.getId(), null, null, null, null)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(disposale = getUserFollowsObserver());
                } else {
                    loadingUser.set(false);
                }
            }

            @Override
            public void onError(Throwable t) {
                Error error = RxErrorFactory.fromThrowable(t);

                sharedPreferencesHelper.setUser(null);
                settings.setUser(null);
                loadingUser.set(false);
                userLogo.set(null);
                userError.set(error.getMessage());
            }
        };
    }

    private DisposableObserver<Response<UserFollowsResponse>> getUserFollowsObserver() {
        return new DisposableObserver<Response<UserFollowsResponse>>() {

            @Override
            public void onNext(Response<UserFollowsResponse> response) {
                mUserFollowsResponse = response.body();
            }

            @Override
            public void onComplete() {
                loadingUser.set(false);
                updateSelectedChannelsText();
            }

            @Override
            public void onError(Throwable t) {
                Error error = RxErrorFactory.fromThrowable(t);

                loadingUser.set(false);
                userError.set(error.getMessage());
                updateSelectedChannelsText();
            }
        };
    }
}
