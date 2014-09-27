package net.myacxy.dashclock.twitch.models;

import android.content.Context;

import net.myacxy.dashclock.twitch.io.TwitchUserFollowsGetter;

import org.json.JSONObject;

/**
 * Class representing a channel from Twitch.tv. Just basic data for now.
 *
 * TODO: include more data?
 */
public class TwitchChannel
{
    public String status;
    public String game;
    public String displayName;
    public int followers;
    public int entryId;
    public boolean online;

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
        final TwitchUserFollowsGetter twitchUserFollowsGetter = new TwitchUserFollowsGetter(context);

        status = twitchUserFollowsGetter.getString(channelObject, "status");
        game = twitchUserFollowsGetter.getString(channelObject, "game");
        displayName = twitchUserFollowsGetter.getString(channelObject, "display_name");
        followers = twitchUserFollowsGetter.getInt(channelObject, "followers");
        entryId = twitchUserFollowsGetter.getInt(channelObject, "_id");
    }
}
