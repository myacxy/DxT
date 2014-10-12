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

import net.myacxy.dashclock.twitch.io.JsonGetter;

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
     */
    public TwitchChannel(JSONObject channelObject)
    {
        init(channelObject);
    }

    /**
     * Initializes the members of this instance with the data provided from
     * the JSONObject of a channel.
     *
     * @param channelObject JSON Data of a Twitch Channel
     */
    private void init(JSONObject channelObject)
    {
        status = JsonGetter.getString(channelObject, "status");
        name = JsonGetter.getString(channelObject, "name");
        game = JsonGetter.getString(channelObject, "game");
        displayName = JsonGetter.getString(channelObject, "display_name");
        followers = JsonGetter.getInt(channelObject, "followers");
        entryId = JsonGetter.getInt(channelObject, "_id");
    }
}
