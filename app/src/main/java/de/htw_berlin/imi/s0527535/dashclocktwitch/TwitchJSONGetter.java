package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TwitchJsonGetter extends JsonGetter {
    /**
     * The activity's context is necessary in order to display the progress dialog.
     *
     * @param context activity from which the class has been called
     */
    public TwitchJsonGetter(Context context) {
        super(context);
    }

    /**
     *
     * @return List of all the user's followed channels
     */
    private void updateAllFollowedChannels(Callback callback)
    {
        this.callback = callback;
        ArrayList<String> allFollowedChannels = new ArrayList<String>();
        // get user name from preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String userName = sharedPreferences.getString(TwitchActivity.PREF_USER_NAME, "test_user1");
        // initialize url
        String url = "https://api.twitch.tv/kraken/users/" + userName + "/follows/channels";
        // start async task to retrieve json file
        execute(url);
    }

    protected ArrayList<TwitchChannel> getSelectedFollowedChannels(ArrayList<TwitchChannel> allFollowedChannels)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean customVisibility = sp.getBoolean(TwitchActivity.PREF_CUSTOM_VISIBILITY, false);
        Set<String> selectedFollowedChannels = sp.getStringSet(TwitchActivity.PREF_SELECTED_FOLLOWED_CHANNELS, null);

        if(!customVisibility || selectedFollowedChannels == null)
        {
            selectedFollowedChannels = new HashSet<String>();

        }
        return null;
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

    public void update(Callback callback)
    {
        updateAllFollowedChannels(callback);
    } // update

    public void saveCurrentTime()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(TwitchActivity.PREF_LAST_UPDATE, System.currentTimeMillis());
        editor.commit();
    } // saveCurrentTime

    public void saveTwitchChannelsToPreferences(ArrayList<TwitchChannel> twitchChannels, String key)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();

        HashSet<String> values = new HashSet<String>();
        for(TwitchChannel tc : twitchChannels)
        {
            values.add(tc.displayName);
        }
        editor.putStringSet(key, values);
        editor.commit();
    } // saveTwitchChannels

    public void saveTwitchChannelsToDb(ArrayList<TwitchChannel> twitchChannels)
    {
        // Gets the data repository in write mode
        SQLiteDatabase db = new TwitchDbHelper(mContext).getWritableDatabase();
        db.delete(TwitchContract.ChannelEntry.TABLE_NAME, null, null);

        for(TwitchChannel tc : twitchChannels)
        {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_ENTRY_ID, tc.id);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME, tc.displayName);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_STATUS, tc.status);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_GAME, tc.game);

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    TwitchContract.ChannelEntry.TABLE_NAME,
                    null,
                    values);
        }
    }
}