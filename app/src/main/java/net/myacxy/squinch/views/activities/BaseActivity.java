package net.myacxy.squinch.views.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import net.myacxy.squinch.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    @BindView(R.id.et_main_name)
    protected EditText editNameView;
    @BindView(R.id.et_main_email)
    protected EditText editEmailView;
    @BindView(R.id.tv_main_name)
    protected TextView nameView;
    @BindView(R.id.tv_main_email)
    protected TextView emailView;

    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

    }

    @OnClick(R.id.btn_main_send)
    protected abstract void onSendClicked();
}
