package net.myacxy.squinch.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.squinch.R;
import net.myacxy.squinch.SimpleViewModelLocator;
import net.myacxy.squinch.viewmodels.ChannelSelectionViewModel;

import butterknife.BindView;

public class ChannelSelectionFragment extends MvvmFragment {

    public static final String TAG = ChannelSelectionFragment.class.getSimpleName();

    @BindView(R.id.rv_cs_channels)
    protected RecyclerView channels;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_channel_selection;
    }

    @Override
    protected ChannelSelectionViewModel getViewModel() {
        SimpleUser user = SimpleViewModelLocator.getInstance().getSettingsViewModel().settings.user.get();
        return SimpleViewModelLocator.getInstance().getChannelSelectionViewModel(user.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        channels.setAdapter(getViewModel().createAdapter());
    }
}
