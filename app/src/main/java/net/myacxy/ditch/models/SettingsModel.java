package net.myacxy.ditch.models;

import net.myacxy.retrotwitch.models.User;

import java.io.Serializable;

public class SettingsModel implements Serializable
{
    public User user;
    public boolean hideEmptyExtension;
    public int updateInterval;
}
