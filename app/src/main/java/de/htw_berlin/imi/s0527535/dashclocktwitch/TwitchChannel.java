package de.htw_berlin.imi.s0527535.dashclocktwitch;

import org.json.JSONException;
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

    public TwitchChannel()
    {
        // default constructor
    }

    /**
     * Constructor that initializes the class members automatically from given JSON data
     *
     * @param channelObject JSON Data of a Twitch Channel
     */
    public TwitchChannel(JSONObject channelObject)
    {
        status = getStringFromJson(channelObject, "status");
        game = getStringFromJson(channelObject, "game");
        displayName = getStringFromJson(channelObject, "display_name");
        followers = getIntFromJson(channelObject, "followers");
        id = getIntFromJson(channelObject, "_id");
    }

    /**
     * Retrieves the value of a String from given JSON Data
     *
     * @param channelObject JSON Data of a Twitch Channel
     * @param name Name of the String that needs to be retrieved
     * @return Value of the sought Object
     */
    private String getStringFromJson(JSONObject channelObject, String name)
    {
        try {
            if(channelObject.get(name) != null)
            {
                return channelObject.getString(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Retrieves the value of an Integer from given JSON Data
     *
     * @param channelObject JSON Data of a Twitch Channel
     * @param name Name of the Integer that needs to be retrieved
     * @return Value of the sought Object
     */
    private int getIntFromJson(JSONObject channelObject, String name)
    {
        try {
            if(channelObject.get(name) != null)
            {
                return channelObject.getInt(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
