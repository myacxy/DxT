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
import android.util.Log;

import net.myacxy.dashclock.twitch.models.TwitchChannel;

import org.json.JSONException;
import org.json.JSONObject;

public class TwitchChannelOnlineChecker extends JsonGetter
{
    protected TwitchChannel mTwitchChannel;
    protected boolean mDismissProgressDialog;

    public TwitchChannelOnlineChecker(Context context, ProgressDialog progressDialog) {
        super(context, progressDialog);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        // check if stream is online
        try {
            if (jsonObject.getString("stream").equals("null")) {
                mTwitchChannel.online = false;
            } else {
                mTwitchChannel.online = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // update database
        TwitchDbHelper twitchDbHelper = new TwitchDbHelper(mContext);
        twitchDbHelper.updateOnlineStatus(mTwitchChannel);
        // dismiss progress dialog
        if(mDismissProgressDialog && mProgressDialog != null) mProgressDialog.dismiss();

        asyncTaskFinished();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        // display current progress
        if(mProgressDialog != null) mProgressDialog.setMessage("Checking " + values[0] + "...");
        Log.d("TwitchChannelOnlineChecker", values[0]);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        publishProgress(mTwitchChannel.displayName);
        return super.doInBackground(params);
    }

    /**
     * Retrieve the JSON data of the stream in order to check if the streamer is currently online.
     *
     * @param twitchChannel Stream to be checked.
     */
    public void run(TwitchChannel twitchChannel, boolean dismiss)
    {
        mDismissProgressDialog = dismiss;
        mTwitchChannel = twitchChannel;
        String url = "https://api.twitch.tv/kraken/streams/" + twitchChannel.name;
        // execute tasks one after the other
        executeOnExecutor(SERIAL_EXECUTOR, url);
    }
}
