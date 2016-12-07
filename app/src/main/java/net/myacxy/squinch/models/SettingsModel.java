package net.myacxy.squinch.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

import net.myacxy.retrotwitch.models.User;

import java.io.Serializable;

public class SettingsModel extends BaseObservable implements Serializable
{
    private User user;
    private boolean hideEmptyExtension;
    private int updateInterval;

    @Bindable
    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
        notifyPropertyChanged(BR.user);
    }

    @Bindable
    public boolean isHideEmptyExtension()
    {
        return hideEmptyExtension;
    }

    public void setHideEmptyExtension(boolean hideEmptyExtension)
    {
        this.hideEmptyExtension = hideEmptyExtension;
        notifyPropertyChanged(BR.hideEmptyExtension);
    }

    @Bindable
    public int getUpdateInterval()
    {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval)
    {
        this.updateInterval = updateInterval;
        notifyPropertyChanged(BR.updateInterval);
    }
}
