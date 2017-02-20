package net.myacxy.squinch.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;

import java.util.List;

public class SettingsModel extends BaseObservable {

    private SimpleUser user;
    private List<UserFollow> userFollows;
    private int updateInterval;
    private boolean hideEmptyExtension;

    @Bindable
    public SimpleUser getUser() {
        return user;
    }

    public void setUser(SimpleUser user) {
        this.user = user;
        notifyPropertyChanged(BR.user);
    }

    @Bindable
    public boolean isHideEmptyExtension() {
        return hideEmptyExtension;
    }

    public void setHideEmptyExtension(boolean hideEmptyExtension) {
        this.hideEmptyExtension = hideEmptyExtension;
        notifyPropertyChanged(BR.hideEmptyExtension);
    }

    @Bindable
    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        notifyPropertyChanged(BR.updateInterval);
    }

    @Bindable
    public List<UserFollow> getUserFollows() {
        return userFollows;
    }

    public void setUserFollows(List<UserFollow> userFollows) {
        this.userFollows = userFollows;
        notifyPropertyChanged(BR.userFollows);
    }
}
