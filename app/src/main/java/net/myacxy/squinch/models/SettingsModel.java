package net.myacxy.squinch.models;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import net.myacxy.retrotwitch.models.User;

import java.io.Serializable;

public class SettingsModel extends BaseObservable implements Serializable
{
    public ObservableField<User> user = new ObservableField<>();
    public ObservableBoolean hideEmptyExtension = new ObservableBoolean();
    public ObservableInt updateInterval = new ObservableInt();
}
