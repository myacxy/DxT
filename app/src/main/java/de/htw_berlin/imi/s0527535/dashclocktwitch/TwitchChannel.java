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
        status = getStringFromJSON(channelObject, "status");
        game = getStringFromJSON(channelObject, "game");
        displayName = getStringFromJSON(channelObject, "display_name");
        followers = getIntFromJSON(channelObject, "followers");
    }

    /**
     * Retrieves the value of a String from given JSON Data
     *
     * @param channelObject JSON Data of a Twitch Channel
     * @param name Name of the String that needs to be retrieved
     * @return Value of the sought Object
     */
    private String getStringFromJSON(JSONObject channelObject, String name)
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
    private int getIntFromJSON(JSONObject channelObject, String name)
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
