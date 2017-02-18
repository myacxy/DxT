package net.myacxy.squinch.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

import net.myacxy.retrotwitch.v5.api.users.SimpleUser;

import java.io.Serializable;

public class SettingsModel extends BaseObservable implements Serializable {

    private SimpleUser user;
    private boolean hideEmptyExtension;
    private int updateInterval;

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
}
