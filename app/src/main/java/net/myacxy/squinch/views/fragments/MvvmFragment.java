package net.myacxy.squinch.views.fragments;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.myacxy.squinch.BR;
import net.myacxy.squinch.viewmodels.ViewModel;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class MvvmFragment extends Fragment {

    private ViewDataBinding dataBinding;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        dataBinding.setVariable(BR.viewModel, getViewModel());
        return dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        dataBinding.executePendingBindings();
        unbinder.unbind();
        super.onDestroyView();
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract ViewModel getViewModel();
}
