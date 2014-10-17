/**
 * Copyright (c) 2014, Johannes Hoffmann
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.myacxy.dashclock.twitch.models;

import android.database.Cursor;

import net.myacxy.dashclock.twitch.database.ChannelQuery;
import net.myacxy.dashclock.twitch.io.JsonGetter;
import net.myacxy.dashclock.twitch.io.TwitchChannelOnlineChecker;
import net.myacxy.dashclock.twitch.io.TwitchGameSearcher;

import org.json.JSONObject;

/**
 * Class representing a channel from Twitch.tv
 */
public class TwitchChannel
{
    /**
     * identifier inside own database
     */
    public int id;
    /**
     * twitch's id
     */
    public int entryId;
    /**
     * number of viewers of this specific channel.
     * NOTE: only available while checking the individual stream
     */
    public int viewers;
    /**
     * number of followers of this specific channel.
     */
    public int followers;
    /**
     * stream currently online?
     * NOTE: only available while checking the individual stream
     */
    public boolean online;
    /**
     * status message
     */
    public String status;
    /**
     * unique name (inside url)
     */
    public String name;
    /**
     * name displayed to the user. may contain whitespaces
     */
    public String displayName;
    /**
     * url to the online source of a picture. template includes adjustable {width} and {height} parameters.
     *
     * example: "http://static-cdn.jtvnw.net/ttv-logoart/League%20of%20Legends-{width}x{height}.jpg"
     */
    public String logo;

    /**
     * url to the online source of a preview picture. template includes adjustable {width} and {height} parameters.
     *
     * example: "http://static-cdn.jtvnw.net/previews-ttv/live_user_nl_kripp-{width}x{height}.jpg"
     *
     * NOTE: only available while checking the individual stream
     */
    public String preview;
    /**
     * ISO8601 date of the last update of the channel by the streamer
     *
     * example: "2014-10-15T04:36:32Z"
     */
    public String updatedAt;
    public TwitchGame game;

    public TwitchChannel()
    {
        // default constructor
    }

    public TwitchChannel(Cursor cursor, TwitchGame game) {
        id = cursor.getInt(ChannelQuery.id);
        entryId = cursor.getInt(ChannelQuery.entryId);
        viewers = cursor.getInt(ChannelQuery.viewers);
        followers = cursor.getInt(ChannelQuery.followers);
        online = cursor.getInt(ChannelQuery.online) == 1;
        status = cursor.getString(ChannelQuery.status);
        name = cursor.getString(ChannelQuery.name);
        displayName = cursor.getString(ChannelQuery.displayName);
        logo = cursor.getString(ChannelQuery.logo);
        preview = cursor.getString(ChannelQuery.preview);
        updatedAt = cursor.getString(ChannelQuery.updatedAt);
        this.game = game;
    }

    /**
     * Initializes the members of this instance with the data that is being provided by the
     * JSONObject of a channel. The JSONObject retrieved from user/follows does not provide
     * all data, though, and thus it needs to be run through {@link TwitchChannelOnlineChecker}
     * and {@link TwitchGameSearcher} as well.
     *
     * @param channelObject JSON Data of a Twitch Channel
     */
    public TwitchChannel(JSONObject channelObject)
    {
        // no database relation yet
        id = -1;
        entryId = JsonGetter.getInt(channelObject, "_id");
        // no individual check by tcoc yet
        viewers = 0;
        followers = JsonGetter.getInt(channelObject, "followers");
        // no individual check by tcoc yet
        online = false;
        status = JsonGetter.getString(channelObject, "status");
        name = JsonGetter.getString(channelObject, "name");
        displayName = JsonGetter.getString(channelObject, "display_name");
        // 300x300 logo for now, no individual check by tcoc yet
        logo = JsonGetter.getString(channelObject, "logo");
        updatedAt = JsonGetter.getString(channelObject, "updated_at");
        updatedAt = updatedAt.replace('T', ' ').replace('Z', ' ').trim();
        // simple initialization in order to pass it to tgs later
        game = new TwitchGame(JsonGetter.getString(channelObject, "game"), null);
    }
} // TwitchChannel
