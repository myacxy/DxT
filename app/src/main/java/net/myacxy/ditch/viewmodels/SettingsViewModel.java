package net.myacxy.ditch.viewmodels;

import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;
import android.view.ViewGroup;

import net.myacxy.ditch.models.SettingsModel;
import net.myacxy.retrotwitch.RxCaller;
import net.myacxy.retrotwitch.helpers.RxErrorFactory;
import net.myacxy.retrotwitch.models.Error;
import net.myacxy.retrotwitch.models.User;
import net.myacxy.retrotwitch.utils.StringUtil;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SettingsViewModel
{
    public ObservableField<User> user = new ObservableField<>();
    public ObservableBoolean loadingUser = new ObservableBoolean();
    public ObservableBoolean isUserAvatarAvailable = new ObservableBoolean();
    public ObservableField<String> userError = new ObservableField<>();
    private final Observer<User> mObserver = new Observer<User>()
    {
        @Override
        public void onCompleted()
        {
            loadingUser.set(false);
        }

        @Override
        public void onError(Throwable t)
        {
            user.set(null);
            loadingUser.set(false);
            isUserAvatarAvailable.set(false);
            Error error = RxErrorFactory.fromThrowable(t);
            userError.set(error.message);
        }

        @Override
        public void onNext(User user)
        {
            SettingsViewModel.this.user.set(user);
            if(user != null) {
                isUserAvatarAvailable.set(!StringUtil.isBlank(user.logo));
            }
        }
    };
    public ObservableBoolean hideEmptyExtension = new ObservableBoolean();
    public ObservableInt updateInterval = new ObservableInt();
    private Subscription mSubscription;

    public SettingsViewModel(SettingsModel settings)
    {
        user.set(settings.user);
        hideEmptyExtension.set(settings.hideEmptyExtension);
        updateInterval.set(settings.updateInterval);
    }

    @BindingAdapter("android:layout_width")
    public static void setLayoutWidth(View view, float width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_height")
    public static void setLayoutHeight(View view, float height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) height;
        view.setLayoutParams(layoutParams);
    }

    public void onChangeUserName(String userName)
    {
        if (userName != null && (userName = userName.trim()).length() != 0)
        {
            if (mSubscription != null)
            {
                mSubscription.unsubscribe();
            }
            user.set(null);
            isUserAvatarAvailable.set(false);
            userError.set(null);
            loadingUser.set(true);

            mSubscription = RxCaller.getInstance()
                    .getUser(userName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mObserver);
        } else
        {
            user.set(null);
            isUserAvatarAvailable.set(false);
            userError.set("user name must not be empty");
        }
    }
}
