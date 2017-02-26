package net.myacxy.squinch.models;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import net.myacxy.retrotwitch.v5.api.streams.Stream;
import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;

import java.util.ArrayList;
import java.util.List;

public class SettingsModel {

    public ObservableField<SimpleUser> user = new ObservableField<>();
    public ObservableField<SimpleUser> tmpUser = new ObservableField<>();

    public ObservableField<List<UserFollow>> userFollows = new ObservableField<>(new ArrayList<>());
    public ObservableField<List<Stream>> liveStreams = new ObservableField<>(new ArrayList<>());
    public ObservableInt updateInterval = new ObservableInt();
    public ObservableBoolean isEmptyExtensionHidden = new ObservableBoolean();
    public ObservableBoolean isLoadingUser = new ObservableBoolean();

    public ObservableField<String> userLogo = new ObservableField<>();
    public ObservableField<String> userError = new ObservableField<>();
    public ObservableField<String> selectedChannelsText = new ObservableField<>("0\u2009/\u20090");

    public ObservableField<List<Long>> deselectedChannelIds = new ObservableField<>(new ArrayList<>());
}
