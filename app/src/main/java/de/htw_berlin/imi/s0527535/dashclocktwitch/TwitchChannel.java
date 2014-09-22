package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.content.Context;

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
    int entryId;
    boolean online;

    public TwitchChannel()
    {
        // default constructor
    }
    /**
     * Constructor that initializes the class members automatically from given JSON data.
     *
     * @param channelObject JSON Data of a Twitch Channel
     * @param context
     */
    public TwitchChannel(JSONObject channelObject, Context context)
    {
        init(channelObject, context);
    }

    /**
     * Initializes the members of this instance with the data provided from
     * the JSONObject of a channel.
     *
     * @param channelObject JSON Data of a Twitch Channel
     */
    public void init(JSONObject channelObject, final Context context)
    {
        final TwitchChannelGetter twitchChannelGetter = new TwitchChannelGetter(context);

        status = twitchChannelGetter.getString(channelObject, "status");
        game = twitchChannelGetter.getString(channelObject, "game");
        displayName = twitchChannelGetter.getString(channelObject, "display_name");
        followers = twitchChannelGetter.getInt(channelObject, "followers");
        entryId = twitchChannelGetter.getInt(channelObject, "_id");
    }
}
