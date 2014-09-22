package de.htw_berlin.imi.s0527535.dashclocktwitch;

import org.json.JSONObject;

/**
 * Class representing a channel from Twitch.tv. Just basic data for now.
 *
 * TODO: include more data?
 */
public class TwitchChannel
{
    String status;
    String game;
    String displayName;
    int followers;
    int id;
    boolean online;

    /**
     * Constructor that initializes the class members automatically from given JSON data
     *
     * @param channelObject JSON Data of a Twitch Channel
     */
    public TwitchChannel(JSONObject channelObject)
    {
        init(channelObject);
    }

    /**
     * Initializes the members of this instance with the data provided from the JSONObject.
     *
     * @param channelObject JSON Data of a Twitch Channel
     */
    public void init(JSONObject channelObject)
    {
        JsonGetter jsonGetter = new JsonGetter(null);
        status = jsonGetter.getString(channelObject, "status");
        game = jsonGetter.getString(channelObject, "game");
        displayName = jsonGetter.getString(channelObject, "display_name");
        followers = jsonGetter.getInt(channelObject, "followers");
        id = jsonGetter.getInt(channelObject, "_id");
    }
}
