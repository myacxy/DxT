package net.myacxy.squinch.viewmodels;

import net.myacxy.retrotwitch.v5.api.channels.SimpleChannel;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.views.adapters.SelectableUserFollowsAdapter;

import java.util.Collections;
import java.util.List;

public class ChannelSelectionViewModel implements ViewModel, SelectableUserFollowsAdapter.UserFollowsDataListener {

    private final DataHelper dataHelper;
    private final List<UserFollow> userFollows;
    private final List<Long> deselectedChannelIds;

    public ChannelSelectionViewModel(DataHelper dataHelper) {
        this.dataHelper = dataHelper;
        userFollows = dataHelper.getUserFollows();
        deselectedChannelIds = dataHelper.getDeselectedChannelIds();
    }

    public List<UserFollow> getUserFollows() {
        return userFollows;
    }

    public List<Long> getDeselectedChannelIds() {
        return deselectedChannelIds;
    }

    @Override
    public void onChannelSelectionChanged(SimpleChannel channel, boolean selected) {
        long id = channel.getId();
        if (selected) {
            if (deselectedChannelIds.contains(id)) {
                deselectedChannelIds.remove(id);
                dataHelper.setDeselectedChannelIds(deselectedChannelIds);
            }
        } else {
            if (!deselectedChannelIds.contains(id)) {
                deselectedChannelIds.add(id);
                dataHelper.setDeselectedChannelIds(deselectedChannelIds);
            }
        }
    }

    @Override
    public void onChannelMoved(int from, int to) {
        Collections.swap(userFollows, from, to);
        // TODO: 17.03.2017 save order persistently
    }
}
