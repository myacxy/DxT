package net.myacxy.squinch.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.myacxy.squinch.R;
import net.myacxy.squinch.SimpleViewModelLocator;
import net.myacxy.squinch.viewmodels.DebugViewModel;

import butterknife.BindView;

public class DebugLogFragment extends MvvmFragment {

    public static final String TAG = DebugLogFragment.class.getSimpleName();

    @BindView(R.id.rv_dl_entries)
    protected RecyclerView entries;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_debug_log;
    }

    @Override
    protected DebugViewModel getViewModel() {
        return SimpleViewModelLocator.getInstance().getDebugViewModel();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewModel().attach();

        entries.setAdapter(getViewModel().createAdapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getViewModel().deattach();
    }
}
