package net.myacxy.dashclock.twitch.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TwitchFollows {

    public ArrayList<TwitchChannel> follows;

    @SerializedName("_total")
    public int total;
}
