package net.myacxy.squinch.viewmodels;

import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.views.adapters.SelectableUserFollowsAdapter;

public class ChannelSelectionViewModel implements ViewModel {

    private final DataHelper dataHelper;
    private final SelectableUserFollowsAdapter mAdapter;

    public ChannelSelectionViewModel(DataHelper dataHelper) {
        this.dataHelper = dataHelper;
        mAdapter = new SelectableUserFollowsAdapter(dataHelper);
    }

    public SelectableUserFollowsAdapter getAdapter() {
        return mAdapter;
    }
}
