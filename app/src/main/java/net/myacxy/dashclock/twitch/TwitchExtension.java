package net.myacxy.dashclock.twitch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import net.myacxy.dashclock.twitch.io.AsyncTaskListener;
import net.myacxy.dashclock.twitch.io.TwitchDbHelper;
import net.myacxy.dashclock.twitch.io.TwitchUserFollowsGetter;
import net.myacxy.dashclock.twitch.models.TwitchChannel;

import java.util.ArrayList;


public class TwitchExtension extends DashClockExtension {

    // initialize shared preference keys
    public static String PREF_USER_NAME = "pref_user_name";
    public static String PREF_CUSTOM_VISIBILITY = "pref_custom_visibility";
    public static String PREF_ALL_FOLLOWED_CHANNELS = "pref_all_followed_channels";
    public static String PREF_SELECTED_FOLLOWED_CHANNELS = "pref_selected_followed_channels";
    public static String PREF_UPDATE_INTERVAL = "pref_update_interval";
    public static String PREF_LAST_UPDATE = "pref_last_update";

    protected Cursor mCursor;
    protected TwitchDbHelper mDbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    protected ArrayList<TwitchChannel> getAllChannels(boolean selected)
    {
        ArrayList<TwitchChannel> twitchChannels = new ArrayList<TwitchChannel>();
        mDbHelper = new TwitchDbHelper(this);

        mCursor = mDbHelper.getChannelsCursor(selected, false);

        while(mCursor.moveToNext())
        {
            TwitchChannel twitchChannel = new TwitchChannel();
            twitchChannel.displayName = mCursor.getString(TwitchDbHelper.ChannelQuery.displayName);
            twitchChannel.game = mCursor.getString((TwitchDbHelper.ChannelQuery.game));
            twitchChannel.status = mCursor.getString(TwitchDbHelper.ChannelQuery.status);
            twitchChannel.online = mCursor.getInt(TwitchDbHelper.ChannelQuery.online) == 1;
            twitchChannels.add(twitchChannel);
        }
        mCursor.close();
        mDbHelper.close();
        return twitchChannels;
    }

    protected ArrayList<TwitchChannel> filterOnlineChannels(ArrayList<TwitchChannel> allChannels)
    {
        ArrayList<TwitchChannel> onlineChannels = new ArrayList<TwitchChannel>();
        for (TwitchChannel tc : allChannels)
        {
            if(tc.online) onlineChannels.add(tc);
        }
        return onlineChannels;
    }

    @Override
    protected void onUpdateData(int reason) {
        Log.d("DEBUG", "onUpdateData");
        if(!TwitchUserFollowsGetter.checkRecentlyUpdated(this)) updateTwitchChannels(this, new AsyncTaskListener() {
            @Override
            public void handleAsyncTaskFinished() {
                Log.d("DEBUG", "handleAsyncTaskFinished");
                ArrayList<TwitchChannel> onlineChannels = filterOnlineChannels(getAllChannels(true));
                int onlineCount = onlineChannels.size();
                String status = String.format("%d Live", onlineCount);
                String expandedTitle = String.format("%s Channel%s", status, onlineCount > 1 ? "s" : "");
                String expandedBody = "";

                for (TwitchChannel tc : onlineChannels)
                {
                    expandedBody += String.format("%s playing %s: %s", tc.displayName, tc.game, tc.status);
                    if(onlineChannels.indexOf(tc) < onlineChannels.size()) expandedBody += "\n";
                }

                Intent intent = new Intent(TwitchExtension.this, MainDialogActivity.class);
                publishUpdate(new ExtensionData()
                        .visible(onlineCount > 0)
                        .icon(R.drawable.ic_launcher)
                        .status(status)
                        .expandedTitle(expandedTitle)
                        .expandedBody(expandedBody)
                        .clickIntent(intent));
            }
        });
    }

    public static void updateTwitchChannels(final Context context, final AsyncTaskListener listener) {
        Log.d("Debug", "updateTwitchChannels");
        // initialize JsonGetter
        final TwitchUserFollowsGetter twitchUserFollowsGetter = new TwitchUserFollowsGetter(context);
        twitchUserFollowsGetter.setAsyncTaskListener(listener);
        twitchUserFollowsGetter.updateAllFollowedChannels();
    } // updateTwitchChannels
} // TwitchActivity