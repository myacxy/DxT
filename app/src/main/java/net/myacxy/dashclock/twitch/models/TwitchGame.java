package net.myacxy.dashclock.twitch.models;

import android.database.Cursor;

import net.myacxy.dashclock.twitch.database.GameQuery;
import net.myacxy.dashclock.twitch.io.JsonGetter;

import org.json.JSONException;
import org.json.JSONObject;

public class TwitchGame {

    /**
     * identifier inside own database
     */
    public int id;
    /**
     * twitch's id
     */
    public int entryId;
    /**
     * number of channels that are online streaming this game. only available via top games
     */
    public int channels;
    /**
     * total number of viewers watching the game. only available via top games
     */
    public int viewers;
    /**
     * name of the game
     */
    public String name;
    /**
     * custom abbreviation for the name created by the user
     */
    public String abbreviation;
    /**
     * url to the online source of a picture.
     * template includes adjustable {width} and {height} parameters.
     *
     * example: "http://static-cdn.jtvnw.net/ttv-logoart/League%20of%20Legends-{width}x{height}.jpg"
     */
    public String logo;

    public TwitchGame(Cursor cursor) {
        id = cursor.getInt(GameQuery.id);
        entryId = cursor.getInt(GameQuery.entryId);
        channels = cursor.getInt(GameQuery.channels);
        viewers = cursor.getInt(GameQuery.viewers);
        name = cursor.getString(GameQuery.name);
        abbreviation = cursor.getString(GameQuery.abbreviation);
        logo = cursor.getString(GameQuery.logo);

        cursor.close();
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
        JSONObject logoJson = null;
        try {
            logoJson = jsonObject.getJSONObject("logo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        logo = JsonGetter.getString(logoJson, "template");
    }
}
