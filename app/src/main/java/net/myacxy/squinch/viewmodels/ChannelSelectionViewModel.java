package net.myacxy.squinch.viewmodels;

import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.views.adapters.SelectableUserFollowsAdapter;

public class ChannelSelectionViewModel implements ViewModel {

    private final DataHelper dataHelper;

    public ChannelSelectionViewModel(DataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    public SelectableUserFollowsAdapter createAdapter() {
        return new SelectableUserFollowsAdapter(dataHelper);
    }
}
