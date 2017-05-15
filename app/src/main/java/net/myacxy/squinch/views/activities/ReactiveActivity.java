package net.myacxy.squinch.views.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ReactiveActivity extends BaseActivity {

    UserManager userManager = new ReactiveUserManager();
    CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Disposable disposable = userManager.getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    editNameView.setText(user.name);
                    editEmailView.setText(user.email);
                    nameView.setText(user.name);
                    emailView.setText(user.email);
                });
        disposables.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    @Override
    protected void onSendClicked() {
        Disposable disposable = userManager.setName(editNameView.getText().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(user -> nameView.setText(user.name))
                .concatWith(
                        userManager.setEmail(editEmailView.getText().toString())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSuccess(user -> emailView.setText(user.email))
                )
                .subscribeOn(Schedulers.io())
                .subscribe();

        disposables.add(disposable);
    }

    private interface UserManager {
        Single<User> getUser();

        Single<User> setName(String name);

        Single<User> setEmail(String email);
    }

    private static class ReactiveUserManager implements UserManager {
        User user = new User("foobar", "foo@bar.de");

        @Override
        public Single<User> getUser() {
            return Single.just(user).delay(3, TimeUnit.SECONDS);
        }

        @Override
        public Single<User> setName(String name) {
            return Single.fromCallable(() -> {
                user.name = name;
                return user;
            }).delay(3, TimeUnit.SECONDS);
        }

        @Override
        public Single<User> setEmail(String email) {
            return Single.fromCallable(() -> {
                user.email = email;
                return user;
            }).delay(3, TimeUnit.SECONDS);
        }
    }
}
