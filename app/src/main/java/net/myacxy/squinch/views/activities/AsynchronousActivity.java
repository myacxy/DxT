package net.myacxy.squinch.views.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsynchronousActivity extends BaseActivity {

    AsynchronousUserManager userManager = new AsynchronousUserManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userManager.getUser(new RequestListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (isDestroyed()) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editNameView.setText(user.name);
                        editEmailView.setText(user.email);
                        nameView.setText(user.name);
                        emailView.setText(user.email);
                    }
                });
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    @Override
    protected void onSendClicked() {

        userManager.setName(editNameView.getText().toString(), new RequestListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (isDestroyed()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nameView.setText(user.name);

                        userManager.setEmail(editEmailView.getText().toString(), new RequestListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                                if (isDestroyed()) {
                                    return;
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        emailView.setText(user.email);
                                    }
                                });
                            }

                            @Override
                            public void onError(Throwable t) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private interface UserManager {
        void getUser(RequestListener<User> listener);

        void setName(String email, RequestListener<User> listener);

        void setEmail(String email, RequestListener<User> listener);
    }

    private interface RequestListener<T> {
        @WorkerThread
        void onSuccess(T t);

        @WorkerThread
        void onError(Throwable t);
    }

    private static class AsynchronousUserManager implements UserManager {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        User user = new User("foobar", "foo@bar.de");

        @Override
        public void getUser(RequestListener<User> listener) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                    }
                    listener.onSuccess(user);
                }
            });
        }

        @Override
        public void setName(String name, RequestListener<User> listener) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                    }
                    user.name = name;
                    listener.onSuccess(user);
                }
            });
        }

        @Override
        public void setEmail(String email, RequestListener<User> listener) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                    }
                    user.email = email;
                    listener.onSuccess(user);
                }
            });
        }
    }
} // SynchronousActivity
