package net.myacxy.dashclock.twitch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import net.myacxy.dashclock.twitch.io.AsyncTaskListener;
import net.myacxy.dashclock.twitch.io.TwitchDbHelper;
import net.myacxy.dashclock.twitch.io.TwitchUserFollowsGetter;


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

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onUpdateData(int reason) {
        Log.d("TwitchExtension", "onUpdateData");
        // update data if it is outdated
        if(!TwitchUserFollowsGetter.checkRecentlyUpdated(this)) {
            updateTwitchChannels(this, null, new AsyncTaskListener() {
                @Override
                public void handleAsyncTaskFinished() {
                    Log.d("TwitchExtension", "handleAsyncTaskFinished");
                    new TwitchDbHelper(getApplicationContext()).updatePublishedData();
                }
            });
        }
        // retrieve data from SharedPreferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int onlineCount = sp.getInt(PREF_ONLINE_COUNT, 0);
        String status = sp.getString(PREF_STATUS, "Empty");
        String expandedTitle = sp.getString(PREF_EXPANDED_TITLE, "Empty");
        String expandedBody = sp.getString(PREF_EXPANDED_BODY, "Empty");

        Intent intent = new Intent(TwitchExtension.this, MainDialogActivity.class);
        // publish data
        publishUpdate(new ExtensionData()
                .visible(onlineCount > 0)
                .icon(R.drawable.ic_twitch_purple)
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