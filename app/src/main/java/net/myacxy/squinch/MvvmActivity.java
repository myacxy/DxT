package net.myacxy.squinch;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.myacxy.squinch.viewmodels.ViewModel;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerAppCompatActivity;

public abstract class MvvmActivity extends DaggerAppCompatActivity {

    private ViewDataBinding dataBinding;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        dataBinding.setVariable(BR.viewModel, getViewModel());
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        dataBinding.executePendingBindings();
        dataBinding.unbind();
        unbinder.unbind();
        super.onDestroy();
    }

    @NonNull
    protected abstract ViewModel getViewModel();

    @LayoutRes
    protected abstract int getLayoutId();
}
