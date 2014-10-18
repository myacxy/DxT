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

package net.myacxy.dashclock.twitch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import net.myacxy.dashclock.twitch.io.AsyncTaskListener;
import net.myacxy.dashclock.twitch.io.TcocManager;
import net.myacxy.dashclock.twitch.database.TwitchDbHelper;
import net.myacxy.dashclock.twitch.io.TwitchUserFollowsGetter;

import java.util.ArrayList;
import java.util.HashSet;


public class TwitchExtension extends DashClockExtension {

    // initialize shared preference keys
    public static String PREF_USER_NAME = "pref_user_name";
    public static String PREF_CUSTOM_VISIBILITY = "pref_custom_visibility";
    public static String PREF_ALL_FOLLOWED_CHANNELS = "pref_all_followed_channels";
    public static String PREF_SELECTED_FOLLOWED_CHANNELS = "pref_selected_followed_channels";
    public static String PREF_UPDATE_INTERVAL = "pref_update_interval";
    public static String PREF_LAST_UPDATE = "pref_last_update";
    public static String PREF_ONLINE_COUNT = "pref_online_count";
    public static String PREF_STATUS = "pref_status";
    public static String PREF_EXPANDED_TITLE = "pref_expanded_title";
    public static String PREF_EXPANDED_BODY = "pref_expanded_body";
    public static String PREF_LONGEST_BODY = "pref_longest_body";
    public static String PREF_DIALOG_SHOW_OFFLINE = "pref_dialog_show_offline";
    public static String PREF_HIDE_EMPTY = "pref_hide_empty";
    public static String PREF_CHAR_LIMIT = "pref_char_limit";
    public static String PREF_ABBR_COUNT = "pref_abbr_count";
    public static String PREF_GAMES_COUNT = "pref_games_count";
    public static String PREF_MAIN_LIST_SHOW_NAME = "pref_main_list_show_name";
    public static String PREF_MAIN_LIST_SHOW_GAME = "pref_main_list_show_game";
    public static String PREF_MAIN_LIST_SHOW_STATUS = "pref_main_list_show_status";
    public static String PREF_MAIN_LIST_SHOW_VIEWERS = "pref_main_list_show_viewers";
    public static String PREF_MAIN_LIST_SHOW_FOLLOWERS = "pref_main_list_show_followers";
    public static String PREF_MAIN_LIST_SHOW_UPDATED = "pref_main_list_show_updated";

    private AsyncTask task;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onUpdateData(int reason) {
        Log.d("TwitchExtension", "onUpdateData");
        // update data if it is outdated
        if(task == null || task.getStatus() == AsyncTask.Status.FINISHED) {
            if (!TcocManager.checkRecentlyUpdated(this)) {
                task = updateTwitchChannels(this, null, new AsyncTaskListener() {
                    @Override
                    public void handleAsyncTaskFinished() {
                        Log.d("TwitchExtension", "handleAsyncTaskFinished");
                        new TwitchDbHelper(getApplicationContext()).updatePublishedData();
                    }
                });
            }
        }
        // retrieve data from SharedPreferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int onlineCount = sp.getInt(PREF_ONLINE_COUNT, 0);
        String status = sp.getString(PREF_STATUS, "Empty");
        String expandedTitle = sp.getString(PREF_EXPANDED_TITLE, "Empty");
        ArrayList<String> expandedBodyList = new ArrayList<String>(
                sp.getStringSet(PREF_EXPANDED_BODY, new HashSet<String>()));
        int charLimit = sp.getInt(TwitchExtension.PREF_CHAR_LIMIT, 200);
        String expandedBody = "";
        for (String string : expandedBodyList) {
            int index = expandedBodyList.indexOf(string);
            if(charLimit < string.length()) {
                string = string.substring(0, charLimit).trim();
                expandedBodyList.set(index, string);
            }
            expandedBody += string;
            if(index < expandedBodyList.size() - 1) expandedBody += "\n";
        }
        boolean hideEmpty = sp.getBoolean(PREF_HIDE_EMPTY, true);
        boolean visible = onlineCount > 0 || !hideEmpty;
        Intent intent = new Intent(TwitchExtension.this, MainDialogActivity.class);
        // publish data
        publishUpdate(new ExtensionData()
                .visible(visible)
                .icon(R.drawable.twitch_purple)
                .status(status)
                .expandedTitle(expandedTitle)
                .expandedBody(expandedBody)
                .clickIntent(intent));
    } // onUpdateData

    public static TwitchUserFollowsGetter updateTwitchChannels(final Context context, final ProgressDialog progressDialog, final AsyncTaskListener listener) {
        Log.d("TwitchExtension", "updateTwitchChannels");
        // initialize JsonGetter
        final TwitchUserFollowsGetter twitchUserFollowsGetter = new TwitchUserFollowsGetter(context, progressDialog);
        twitchUserFollowsGetter.setAsyncTaskListener(listener);
        twitchUserFollowsGetter.updateAllFollowedChannels();
        return twitchUserFollowsGetter;
    } // updateTwitchChannels
} // TwitchActivity