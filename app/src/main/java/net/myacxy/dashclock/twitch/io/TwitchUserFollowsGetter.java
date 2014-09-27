package net.myacxy.dashclock.twitch.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import net.myacxy.dashclock.twitch.models.TwitchChannel;
import net.myacxy.dashclock.twitch.TwitchExtension;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

public class TwitchUserFollowsGetter extends JsonGetter {
    /**
     * The activity's context is necessary in order to display the progress dialog.
     *
     * @param context activity from which the class has been called
     */
    public TwitchUserFollowsGetter(Context context) {
        super(context);
    }

    @Override
    protected void onPreExecute() {
//        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
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

        if(mProgressDialog != null) mProgressDialog.setMessage("Parsing data...");
        // analyse json data and parse it
        ArrayList<TwitchChannel> allFollowedChannels = parseJsonObject(jsonAllFollowedChannels);

        if (allFollowedChannels != null) {
            // save all followed channels to shared preferences
            if(mProgressDialog != null) mProgressDialog.setMessage("Saving data...");
            saveTwitchChannelsToPreferences(allFollowedChannels, TwitchExtension.PREF_ALL_FOLLOWED_CHANNELS);
            // save all followed channels to database
            new TwitchDbHelper(mContext).saveChannels(allFollowedChannels);
            // save the time of this update
            saveCurrentTime();
        }

        // check online status of each channel
        for(final TwitchChannel tc : allFollowedChannels)
        {
            TwitchChannelOnlineChecker onlineChecker =
                    new TwitchChannelOnlineChecker(mContext, mProgressDialog);
            if(allFollowedChannels.get(allFollowedChannels.size() - 1).equals(tc)) {
                onlineChecker.setAsyncTaskListener(mListener.get());
                onlineChecker.run(tc, true);
            } else {
                onlineChecker.run(tc, false);
            }
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
        // initialize url
        String url = "https://api.twitch.tv/kraken/users/" + userName + "/follows/channels";
        // start async task to retrieve json file
        executeOnExecutor(SERIAL_EXECUTOR, url);
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
            TwitchChannel tc = new TwitchChannel(channelObject, mContext);
            // add channel to list
            followedTwitchChannels.add(tc);
        }

        return followedTwitchChannels;
    } // parseJsonObject

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
        int updateInterval = sp.getInt(TwitchExtension.PREF_UPDATE_INTERVAL, 5);
        Log.d("Debug", "Difference=" + difference);
        return difference < updateInterval;
    }

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
        for(TwitchChannel tc : twitchChannels)
        {
            values.add(tc.displayName);
        }
        // save
        editor.putStringSet(key, values);
        editor.apply();
    } // saveTwitchChannelsToPreferences
}