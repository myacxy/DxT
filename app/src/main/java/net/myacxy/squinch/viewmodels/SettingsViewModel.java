package net.myacxy.squinch.viewmodels;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import net.myacxy.squinch.models.SettingsModel;
import net.myacxy.retrotwitch.RxCaller;
import net.myacxy.retrotwitch.helpers.RxErrorFactory;
import net.myacxy.retrotwitch.models.Error;
import net.myacxy.retrotwitch.models.User;
import net.myacxy.retrotwitch.models.UserFollowsContainer;
import net.myacxy.retrotwitch.utils.StringUtil;

import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SettingsViewModel
{
    public SettingsModel settings;

    public ObservableBoolean loadingUser = new ObservableBoolean();

    public ObservableField<String> userLogo = new ObservableField<>();
    public ObservableField<String> userError = new ObservableField<>();
    public ObservableField<String> selectedChannelsText = new ObservableField<>("0\u2009/\u20090");

    private Disposable userSubscription;
    private Disposable userFollowsSubscription;
    private final DisposableObserver<User> mUserObserver = new DisposableObserver<User>()
    {

        @Override
        public void onNext(User user)
        {
            settings.setUser(user);
            if (user != null)
            {
                userLogo.set(user.logo);
            }
        }

        @Override
        public void onComplete()
        {
            User user = settings.getUser();
            if (user != null && !StringUtil.isBlank(user.name))
            {
                userFollowsSubscription = RxCaller.getInstance()
                        .getUserFollows(user.name, null, null, null, null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(mUserFollowsObserver);
            } else
            {
                loadingUser.set(false);
            }

            userSubscription.dispose();
            userSubscription = null;
        }

        @Override
        public void onError(Throwable t)
        {
            settings.setUser(null);
            loadingUser.set(false);
            userLogo.set(null);
            Error error = RxErrorFactory.fromThrowable(t);
            userError.set(error.message);

            userSubscription.dispose();
            userSubscription = null;
        }
    };
    private UserFollowsContainer mUserFollowsContainer;
    private final DisposableObserver<UserFollowsContainer> mUserFollowsObserver = new DisposableObserver<UserFollowsContainer>()
    {
        @Override
        public void onComplete()
        {
            loadingUser.set(false);

            userFollowsSubscription.dispose();
            userFollowsSubscription = null;

            updateSelectedChannelsText();
        }

        @Override
        public void onError(Throwable t)
        {
            loadingUser.set(false);
            Error error = RxErrorFactory.fromThrowable(t);
            userError.set(error.message);

            userFollowsSubscription.dispose();
            userFollowsSubscription = null;

            updateSelectedChannelsText();
        }

        @Override
        public void onNext(UserFollowsContainer userFollowsContainer)
        {
            mUserFollowsContainer = userFollowsContainer;
        }
    };

    public SettingsViewModel(SettingsModel settings)
    {
        this.settings = settings;
    }

    public void onChangeUserName(String userName)
    {
        if (userName != null && (userName = userName.trim()).length() != 0)
        {
            reset();

            loadingUser.set(true);

            userSubscription = RxCaller.getInstance()
                    .getUser(userName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(mUserObserver);
        } else
        {
            settings.setUser(null);
            userLogo.set(null);
            userError.set("user name must not be empty");
        }
    }

    private void updateSelectedChannelsText()
    {
        if (mUserFollowsContainer != null)
        {
            int followsCount = mUserFollowsContainer.userFollows.size();
            String text = String.format(Locale.getDefault(), "%d\u2009/\u2009%d", followsCount, followsCount);
            selectedChannelsText.set(text);
            return;
        }
        selectedChannelsText.set("0\u2009/\u20090");
    }

    private void reset()
    {
        if (userSubscription != null)
        {
            userSubscription.dispose();
            userSubscription = null;
        }
        if (userFollowsSubscription != null)
        {
            userFollowsSubscription.dispose();
            userFollowsSubscription = null;
        }

        settings.setUser(null);
        userLogo.set(null);
        userError.set(null);
    }
}
