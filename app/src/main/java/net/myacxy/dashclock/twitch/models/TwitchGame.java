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
    }

    public TwitchGame(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public TwitchGame(JSONObject jsonObject, int channels, int viewers) {

        name = JsonGetter.getString(jsonObject, "name");
        entryId = JsonGetter.getInt(jsonObject, "_id");
        JSONObject logoJson = null;
        try {
            logoJson = jsonObject.getJSONObject("logo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        logo = JsonGetter.getString(logoJson, "template");

        this.channels = channels;
        this.viewers = viewers;
    }

    @Override
    public String toString() {
        return name;
    }
}
