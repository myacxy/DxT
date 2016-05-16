package net.myacxy.ditch.viewmodels;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import net.myacxy.ditch.models.SettingsModel;
import net.myacxy.retrotwitch.RxCaller;
import net.myacxy.retrotwitch.api.Direction;
import net.myacxy.retrotwitch.api.SortBy;
import net.myacxy.retrotwitch.api.TwitchV3Service;
import net.myacxy.retrotwitch.helpers.RxErrorFactory;
import net.myacxy.retrotwitch.models.Error;
import net.myacxy.retrotwitch.models.UserFollow;
import net.myacxy.retrotwitch.models.UserFollowsContainer;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SettingsViewModel
{
    public final ObservableArrayList<UserFollow> userFollows = new ObservableArrayList<>();
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableBoolean loadingUserName = new ObservableBoolean();
    public ObservableField<String> userNameError = new ObservableField<>();
    public ObservableField<String> userAvatarUrl = new ObservableField<>();
    public ObservableBoolean hideEmptyExtension = new ObservableBoolean();
    public ObservableInt updateInterval = new ObservableInt();
    private UserFollowsContainer mUserFollowsContainer;
    private final Observer<UserFollowsContainer> mObserver = new Observer<UserFollowsContainer>()
    {
        @Override
        public void onCompleted()
        {
            loadingUserName.set(false);
            userNameError.set(String.valueOf(userFollows.size()));
            if(mUserFollowsContainer != null && mUserFollowsContainer.userFollows.size() > 0) {
                userAvatarUrl.set(mUserFollowsContainer.userFollows.get(0).channel.logo);
            }
        }

        @Override
        public void onError(Throwable t)
        {
            mUserFollowsContainer = null;
            userAvatarUrl.set(null);
            loadingUserName.set(false);
            Error error = RxErrorFactory.fromThrowable(t);
            userNameError.set(error.message);
        }

        @Override
        public void onNext(UserFollowsContainer userFollowsContainer)
        {
            userFollows.addAll(userFollowsContainer.userFollows);
            mUserFollowsContainer = userFollowsContainer;
        }
    };
    private Subscription mSubscription;

    public SettingsViewModel(SettingsModel settings)
    {
        userName.set(settings.userName);
        hideEmptyExtension.set(settings.hideEmptyExtension);
        updateInterval.set(settings.updateInterval);
    }

    public void onChangeUserName(String userName) {
        if (userName != null && (userName = userName.trim()).length() != 0)
        {
            if(mSubscription != null) {
                mSubscription.unsubscribe();
            }
            mUserFollowsContainer = null;
            userFollows.clear();

            userAvatarUrl.set(null);
            loadingUserName.set(true);

            mSubscription = RxCaller.getInstance()
                    .getUserFollows(userName, TwitchV3Service.MAX_LIMIT, 0, Direction.DEFAULT, SortBy.CREATED_AT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mObserver);
        } else {
            userNameError.set("user name must not be empty");
        }
    }
}
