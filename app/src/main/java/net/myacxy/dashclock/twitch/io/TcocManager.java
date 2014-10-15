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
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import net.myacxy.dashclock.twitch.TwitchExtension;
import net.myacxy.dashclock.twitch.database.TwitchDbHelper;
import net.myacxy.dashclock.twitch.models.TwitchChannel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

public class TcocManager extends AsyncTask<Void, Integer, ArrayList<TwitchChannel>>
{
    protected ArrayList<TwitchChannel> mAllChannels;
    protected ArrayList<TwitchChannelOnlineChecker> mTcocs;
    protected ArrayList<TwitchGameSearcher> mTgss;
    protected Context mContext;
    protected ProgressDialog mProgressDialog;
    protected AsyncTaskListener mListener;
    protected ArrayList<TwitchChannel> mOnlineChannels;

    public TcocManager(ArrayList<TwitchChannel> allChannels, Context context, ProgressDialog progressDialog, AsyncTaskListener listener)
    {
        mAllChannels = allChannels;
        mContext = context;
        mProgressDialog = progressDialog;
        mListener = listener;
        mOnlineChannels = new ArrayList<>();
        mTcocs = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        for(TwitchChannel tc : mAllChannels)
        {
            TwitchChannelOnlineChecker onlineChecker =
                    new TwitchChannelOnlineChecker(mContext, mProgressDialog);
            mTcocs.add(onlineChecker);
            TwitchGameSearcher tgs = new TwitchGameSearcher(mContext);
            tgs.run(tc.game.name);
            onlineChecker.run(tc);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(mProgressDialog != null) mProgressDialog.setMessage(values[0].toString());
    }

    @Override
    protected void onPostExecute(ArrayList<TwitchChannel> channels) {
        if(mProgressDialog != null) mProgressDialog.dismiss();
        // save all followed channels to shared preferences
        saveTwitchChannelsToPreferences(mAllChannels, TwitchExtension.PREF_ALL_FOLLOWED_CHANNELS);
        // save all followed channels to database
        new TwitchDbHelper(mContext).saveChannels(mAllChannels);
        // save the time of this update
        saveCurrentTime();
        if(mListener != null) mListener.handleAsyncTaskFinished();
    }

    @Override
    protected ArrayList<TwitchChannel> doInBackground(Void... params) {

        while(true) {
            for(TwitchChannelOnlineChecker tcoc : mTcocs) {
                if(tcoc.getStatus() == Status.FINISHED && !mOnlineChannels.contains(tcoc.mTwitchChannel))
                    mOnlineChannels.add(tcoc.mTwitchChannel);
            }
            if(mOnlineChannels.size() == mAllChannels.size()) break;
        }
        Log.d("OnlineCheckerManager", "doInBackground finished");
        return mOnlineChannels;
    }

    @Override
    protected void onCancelled() {
        for(AsyncTask task : mTcocs) task.cancel(true);
        super.onCancelled();
    }

    /**
     * Saves the display names of the provided TwitchChannels to the Shared Preferences as a Set
     * of Strings.
     *
     * @param twitchChannels List of TwitchChannels being saved
     * @param key The name of the preference to modify.
     */
    public void saveTwitchChannelsToPreferences(ArrayList<TwitchChannel> twitchChannels, String key)
    {
        // initialize
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        // retrieve display names
        HashSet<String> values = new HashSet<String>();
        for(TwitchChannel tc : twitchChannels) {
            values.add(tc.displayName);
        }
        // save
        editor.putStringSet(key, values);
        editor.apply();
    } // saveTwitchChannelsToPreferences

    /**
     * Saves the current system time in milliseconds to the shared preferences so that it can later
     * be used to limit the updates in a adjustable time interval.
     */
    public void saveCurrentTime()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        long currentMillis = Calendar.getInstance().getTimeInMillis();
        editor.putLong(TwitchExtension.PREF_LAST_UPDATE, currentMillis);
        editor.apply();
    } // saveCurrentTime

    /**
     * Checks the Shared Preferences for the time of the last update.
     *
     * @return  returns true if channels were updated within the update interval
     *          return false if channels were updated later than the update interval
     */
    public static boolean checkRecentlyUpdated(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long lastUpdate = sp.getLong(TwitchExtension.PREF_LAST_UPDATE, 0);
        // calculate difference in minutes
        double difference = (Calendar.getInstance().getTimeInMillis() - lastUpdate) / 60000f;
        int updateInterval = sp.getInt(TwitchExtension.PREF_UPDATE_INTERVAL, 15);
        Log.d("TwitchUserFollowsGetter", "Difference=" + difference);
        return difference < updateInterval;
    }
} // TcocManager