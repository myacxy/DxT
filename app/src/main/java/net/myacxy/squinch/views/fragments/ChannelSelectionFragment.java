package net.myacxy.squinch.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.squinch.R;
import net.myacxy.squinch.SimpleViewModelLocator;
import net.myacxy.squinch.viewmodels.ChannelSelectionViewModel;
import net.myacxy.squinch.views.adapters.SelectableUserFollowsAdapter;

import butterknife.BindView;

public class ChannelSelectionFragment extends MvvmFragment implements SelectableUserFollowsAdapter.UserFollowsViewListener {

    public static final String TAG = ChannelSelectionFragment.class.getSimpleName();

    @BindView(R.id.rv_cs_channels)
    protected RecyclerView channels;

    private SelectableUserFollowsAdapter adapter;
    private ItemTouchHelper itemTouchHelper;

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

        SelectableUserFollowsAdapter.TouchCallback callback = new SelectableUserFollowsAdapter.TouchCallback(getViewModel(), this);
        itemTouchHelper = new ItemTouchHelper(callback);
        adapter = new SelectableUserFollowsAdapter(
                getViewModel().getUserFollows(),
                getViewModel().getDeselectedChannelIds(),
                getViewModel(),
                this
        );
        itemTouchHelper.attachToRecyclerView(channels);
        channels.setAdapter(adapter);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
    }

    @Override
    public void onChannelMoved(int from, int to) {
        adapter.notifyItemMoved(from, to);
    }
}
