package net.myacxy.squinch.views.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class SynchronousActivity extends BaseActivity {

    UserManager userManager = new SynchronousUserManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = userManager.getUser();
        editNameView.setText(user.name);
        editEmailView.setText(user.email);
        nameView.setText(user.name);
        emailView.setText(user.email);
    }

    @Override
    protected void onSendClicked() {
        userManager.setName(editNameView.getText().toString());
        userManager.setEmail(editEmailView.getText().toString());

        User user = userManager.getUser();
        nameView.setText(user.name);
        emailView.setText(user.email);
    }

    private interface UserManager {
        User getUser();

        void setName(String name);

        void setEmail(String email);
    }

    private static class SynchronousUserManager implements UserManager {
        User user = new User("foobar", "foo@bar.de");

        @Override
        public User getUser() {
            return user;
        }

        @Override
        public void setName(String name) {
            user.name = name;
        }

        @Override
        public void setEmail(String email) {
            user.email = email;
        }
    }
} // SynchronousActivity
