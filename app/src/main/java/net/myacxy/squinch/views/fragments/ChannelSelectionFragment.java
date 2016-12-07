package net.myacxy.squinch.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.myacxy.squinch.SimpleViewModelLocator;
import net.myacxy.squinch.databinding.ChannelSelectionFragmentBinding;
import net.myacxy.squinch.viewmodels.ChannelSelectionViewModel;
import net.myacxy.retrotwitch.models.User;

public class ChannelSelectionFragment extends Fragment
{
    public static final String TAG = ChannelSelectionFragment.class.getSimpleName();

    private ChannelSelectionFragmentBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mBinding = ChannelSelectionFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        User user = SimpleViewModelLocator.getInstance().getSettingsViewModel().settings.user.get();
        ChannelSelectionViewModel viewModel = SimpleViewModelLocator.getInstance().getChannelSelectionViewModel(user.name);
        mBinding.setViewModel(viewModel);
        mBinding.rvCsChannels.setAdapter(viewModel.getAdapter());
    }

    @Override
    public void onDestroy()
    {
        mBinding.unbind();
        super.onDestroy();
    }
}
