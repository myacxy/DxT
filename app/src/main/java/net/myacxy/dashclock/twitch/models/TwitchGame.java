package net.myacxy.dashclock.twitch.models;

import net.myacxy.dashclock.twitch.io.JsonGetter;

import org.json.JSONObject;

public class TwitchGame {

    public String name;
    public String abbreviation;
    public int entryId;
    public int viewers;
    public int channels;

    public TwitchGame(JSONObject jsonObject, int channels, int viewers) {
        init(jsonObject);
        this.channels = channels;
        this.viewers = viewers;
    }

    private void init(JSONObject jsonObject) {
        name = JsonGetter.getString(jsonObject, "name");
        entryId = JsonGetter.getInt(jsonObject, "_id");
    }
}
