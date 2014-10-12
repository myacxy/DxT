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

package net.myacxy.dashclock.twitch.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import net.myacxy.dashclock.twitch.TwitchExtension;
import net.myacxy.dashclock.twitch.models.TwitchChannel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TwitchUserFollowsGetter extends JsonGetter {

    public TcocManager tcocManager;
    /**
     * The activity's context is necessary in order to display the progress dialog.
     *
     * @param context activity from which the class has been called
     */
    public TwitchUserFollowsGetter(Context context, ProgressDialog progressDialog) {
        super(context, progressDialog);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if(jsonObject == null) return;
        JSONArray jsonAllFollowedChannels = null;
        // status error
        if(jsonObject.has("status"))
        {
            try {
                Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                if(mProgressDialog != null) mProgressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        // no channels being followed
        try {
            jsonAllFollowedChannels = jsonObject.getJSONArray("follows");
            if (jsonAllFollowedChannels == null)
            {
                Toast.makeText(mContext, "No channels being followed.", Toast.LENGTH_LONG).show();
                if(mProgressDialog != null) mProgressDialog.dismiss();
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // analyse json data and parse it
        ArrayList<TwitchChannel> allFollowedChannels = parseJsonObject(jsonAllFollowedChannels);

        if (allFollowedChannels != null) {
            tcocManager = new TcocManager(allFollowedChannels, mContext, mProgressDialog, mListener);
            tcocManager.executeOnExecutor(THREAD_POOL_EXECUTOR, null);
        }
    }

    /**
     * TODO: javadoc
     *
     * @return List of all the user's followed channels
     */
    public void updateAllFollowedChannels()
    {
        // get user name from preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String userName = sharedPreferences.getString(TwitchExtension.PREF_USER_NAME, "test_user1");
        userName = userName.trim();
        // initialize url
        String url = "https://api.twitch.tv/kraken/users/" + userName + "/follows/channels";
        // start async task to retrieve json file
        executeOnExecutor(THREAD_POOL_EXECUTOR, url);
    }

    /**
     * Parses the JSON data from a user's followed Twitch.tv channels
     *
     * @param jsonAllFollowedChannels JSON data from Twitch representing the followed channels of a user
     * @return List of all followed channels
     */
    protected ArrayList<TwitchChannel> parseJsonObject(JSONArray jsonAllFollowedChannels)
    {
        // initialize
        ArrayList<TwitchChannel> followedTwitchChannels = new ArrayList<TwitchChannel>();

        for (int i = 0; i < jsonAllFollowedChannels.length(); i++)
        {
            JSONObject channelObject = null;
            // get channel from array
            try {
                channelObject = jsonAllFollowedChannels.getJSONObject(i).getJSONObject("channel");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // parse json to TwitchChannel
            TwitchChannel tc = new TwitchChannel(channelObject);
            // add channel to list
            followedTwitchChannels.add(tc);
        }

        return followedTwitchChannels;
    } // parseJsonObject
}