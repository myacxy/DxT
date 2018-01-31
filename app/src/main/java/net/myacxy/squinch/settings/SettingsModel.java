package net.myacxy.squinch.settings;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;

import net.myacxy.retrotwitch.v5.api.streams.Stream;
import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.squinch.BR;

import java.util.List;
import java.util.Locale;

public class SettingsModel extends BaseObservable {

    private SimpleUser user;
    private String tmpUser;
    private String userError;
    private boolean isLoadingUser;

    private ObservableArrayList<UserFollow> userFollows = new ObservableArrayList<>();
    private ObservableArrayList<Long> deselectedChannelIds = new ObservableArrayList<>();
    private ObservableArrayList<Stream> liveStreams = new ObservableArrayList<>();

    private int updateInterval;
    private boolean isEmptyExtensionHidden;

    @Bindable
    public SimpleUser getUser() {
        return user;
    }

    public void setUser(SimpleUser user) {
        this.user = user;
        setTmpUser(user != null ? user.getName() : tmpUser);
        notifyPropertyChanged(BR.user);
    }

    @Bindable
    public String getTmpUser() {
        return tmpUser;
    }

    public void setTmpUser(String tmpUser) {
        this.tmpUser = tmpUser;
        notifyPropertyChanged(BR.tmpUser);
    }

    @Bindable({"user"})
    public String getUserLogo() {
        return user != null ? user.getLogo() : null;
    }

    @Bindable
    public String getUserError() {
        return userError;
    }

    public void setUserError(String userError) {
        this.userError = userError;
        notifyPropertyChanged(BR.userError);
    }

    @Bindable
    public boolean isLoadingUser() {
        return isLoadingUser;
    }

    public void setLoadingUser(boolean loadingUser) {
        isLoadingUser = loadingUser;
        notifyPropertyChanged(BR.loadingUser);
    }

    @Bindable
    public List<UserFollow> getUserFollows() {
        return userFollows;
    }

    public void addUserFollows(List<UserFollow> userFollows) {
        this.userFollows.addAll(userFollows);
        notifyPropertyChanged(BR.userFollows);
    }

    public void clearUserFollows() {
        this.userFollows.clear();
        notifyPropertyChanged(BR.userFollows);
    }

    @Bindable
    public List<Long> getDeselectedChannelIds() {
        return deselectedChannelIds;
    }

    public void setDeselectedChannelIds(List<Long> deselectedChannelIds) {
        this.deselectedChannelIds.clear();
        this.deselectedChannelIds.addAll(deselectedChannelIds);
        notifyPropertyChanged(BR.deselectedChannelIds);
    }

    @Bindable
    public List<Stream> getLiveStreams() {
        return liveStreams;
    }

    public void setLiveStreams(List<Stream> liveStreams) {
        this.liveStreams.clear();
        this.liveStreams.addAll(liveStreams);
        notifyPropertyChanged(BR.liveStreams);
    }

    public void clearLiveStreams() {
        this.liveStreams.clear();
        notifyPropertyChanged(BR.liveStreams);
    }

    @Bindable({"userFollows", "deselectedChannelIds"})
    public String getSelectedChannelsText() {
        int deselected = 0;
        for (UserFollow userFollow : userFollows) {
            if (deselectedChannelIds.contains(userFollow.getChannel().getId())) {
                deselected += 1;
            }
        }
        return String.format(Locale.getDefault(), "%d\u2009/\u2009%d", userFollows.size() - deselected, userFollows.size());
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
    public boolean isEmptyExtensionHidden() {
        return isEmptyExtensionHidden;
    }

    public void setEmptyExtensionHidden(boolean emptyExtensionHidden) {
        isEmptyExtensionHidden = emptyExtensionHidden;
        notifyPropertyChanged(BR.emptyExtensionHidden);
    }
}
