package net.myacxy.ditch.viewmodels;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import net.myacxy.ditch.models.SettingsModel;
import net.myacxy.retrotwitch.RxCaller;
import net.myacxy.retrotwitch.helpers.RxErrorFactory;
import net.myacxy.retrotwitch.models.Error;
import net.myacxy.retrotwitch.models.User;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SettingsViewModel
{
    public ObservableField<User> user = new ObservableField<>();
    public ObservableBoolean loadingUser = new ObservableBoolean();
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
            Error error = RxErrorFactory.fromThrowable(t);
            userError.set(error.message);
        }

        @Override
        public void onNext(User user)
        {
            SettingsViewModel.this.user.set(user);
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

    public void onChangeUserName(String userName) {
        if (userName != null && (userName = userName.trim()).length() != 0)
        {
            if(mSubscription != null) {
                mSubscription.unsubscribe();
            }
            user.set(null);
            userError.set(null);
            loadingUser.set(true);

            mSubscription = RxCaller.getInstance()
                    .getUser(userName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mObserver);
        } else {
            userError.set("user name must not be empty");
        }
    }
}
