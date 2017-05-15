package net.myacxy.squinch.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import net.myacxy.squinch.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_main_synchronous)
    protected void onSynchronousClicked() {
        Intent intent = new Intent(this, SynchronousActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_main_asynchronous)
    protected void onAsynchronousClicked() {
        Intent intent = new Intent(this, AsynchronousActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_main_reactive)
    protected void onReactiveClicked() {
        Intent intent = new Intent(this, ReactiveActivity.class);
        startActivity(intent);
    }
}
