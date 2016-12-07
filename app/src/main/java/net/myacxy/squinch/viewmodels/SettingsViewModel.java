package net.myacxy.squinch.viewmodels;

import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;
import android.view.ViewGroup;

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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
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
    private CompositeDisposable mSubscriptions = new CompositeDisposable();
    private final Observer<User> mUserObserver = new Observer<User>()
    {
        @Override
        public void onSubscribe(Disposable disposable)
        {
            userSubscription = disposable;
            mSubscriptions.add(disposable);
        }

        @Override
        public void onNext(User user)
        {
            settings.user.set(user);
            if (user != null)
            {
                userLogo.set(user.logo);
            }
        }

        @Override
        public void onComplete()
        {
            User user = settings.user.get();
            if (user != null && !StringUtil.isBlank(user.name))
            {
                RxCaller.getInstance()
                        .getUserFollows(user.name, null, null, null, null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mUserFollowsObserver);
            } else
            {
                loadingUser.set(false);
            }

            mSubscriptions.remove(userSubscription);
            userSubscription = null;
        }

        @Override
        public void onError(Throwable t)
        {
            settings.user.set(null);
            loadingUser.set(false);
            userLogo.set(null);
            Error error = RxErrorFactory.fromThrowable(t);
            userError.set(error.message);

            mSubscriptions.remove(userSubscription);
            userSubscription = null;
        }
    };
    private UserFollowsContainer mUserFollowsContainer;
    private final Observer<UserFollowsContainer> mUserFollowsObserver = new Observer<UserFollowsContainer>()
    {
        @Override
        public void onSubscribe(Disposable disposable)
        {
            userFollowsSubscription = disposable;
            mSubscriptions.add(disposable);
        }

        @Override
        public void onComplete()
        {
            loadingUser.set(false);

            mSubscriptions.delete(userFollowsSubscription);
            userFollowsSubscription = null;

            updateSelectedChannelsText();
        }

        @Override
        public void onError(Throwable t)
        {
            loadingUser.set(false);
            Error error = RxErrorFactory.fromThrowable(t);
            userError.set(error.message);

            mSubscriptions.remove(userFollowsSubscription);
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

    @BindingAdapter("android:layout_width")
    public static void setLayoutWidth(View view, float width)
    {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_height")
    public static void setLayoutHeight(View view, float height)
    {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) height;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter({"enabled"})
    public static void setEnabled(ViewGroup viewGroup, boolean enabled)
    {
        for (int i = 0; i < viewGroup.getChildCount(); i++)
        {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof ViewGroup)
            {
                setEnabled((ViewGroup) child, enabled);
            }
        }
    }

    public void onChangeUserName(String userName)
    {
        if (userName != null && (userName = userName.trim()).length() != 0)
        {
            reset();

            loadingUser.set(true);

            RxCaller.getInstance()
                    .getUser(userName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mUserObserver);
        } else
        {
            settings.user.set(null);
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
            mSubscriptions.remove(userSubscription);
            userSubscription = null;
        }
        if (userFollowsSubscription != null)
        {
            userFollowsSubscription.dispose();
            mSubscriptions.remove(userFollowsSubscription);
            userFollowsSubscription = null;
        }

        settings.user.set(null);
        userLogo.set(null);
        userError.set(null);
    }
}
