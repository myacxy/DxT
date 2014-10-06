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
    public String name;
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
        final TwitchUserFollowsGetter twitchUserFollowsGetter = new TwitchUserFollowsGetter(context, null);

        status = twitchUserFollowsGetter.getString(channelObject, "status");
        name = twitchUserFollowsGetter.getString(channelObject, "name");
        game = twitchUserFollowsGetter.getString(channelObject, "game");
        displayName = twitchUserFollowsGetter.getString(channelObject, "display_name");
        followers = twitchUserFollowsGetter.getInt(channelObject, "followers");
        entryId = twitchUserFollowsGetter.getInt(channelObject, "_id");
    }
}
