package net.myacxy.dashclock.twitch.models;

import android.database.Cursor;

import net.myacxy.dashclock.twitch.io.JsonGetter;
import net.myacxy.dashclock.twitch.io.TwitchDbHelper;

import org.json.JSONObject;

public class TwitchGame {

    public String name;
    public String abbreviation;
    public int entryId;
    public int viewers;
    public int channels;

    public TwitchGame(Cursor cursor) {
        name = cursor.getString(TwitchDbHelper.GameQuery.name);
        abbreviation = cursor.getString(TwitchDbHelper.GameQuery.abbreviation);
    }

    public TwitchGame(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

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
