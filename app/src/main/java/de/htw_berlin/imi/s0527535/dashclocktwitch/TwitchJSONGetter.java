package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TwitchJSONGetter extends JSONGetter {
    /**
     * The activity's context is necessary in order to display the progress dialog.
     *
     * @param context activity from which the class has been called
     */
    public TwitchJSONGetter(Context context) {
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sharedPreferences.getString(TwitchActivity.PREF_USER_NAME, "test_user1");
        // initialize url
        String url = "https://api.twitch.tv/kraken/users/" + userName + "/follows/channels";
        // start async task to retrieve json file
        execute(url);
    }

    protected ArrayList<TwitchChannel> getSelectedFollowedChannels(ArrayList<TwitchChannel> allFollowedChannels)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
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
    protected ArrayList<TwitchChannel> parseJSONObject(JSONArray jsonAllFollowedChannels)
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
    } // parseJSONObject

    public void update(Callback callback)
    {
        updateAllFollowedChannels(callback);
    } // update

    public void saveCurrentTime()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(TwitchActivity.PREF_LAST_UPDATE, System.currentTimeMillis());
        editor.commit();
    } // saveCurrentTime

    public void saveTwitchChannels(ArrayList<TwitchChannel> twitchChannels, String key)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        HashSet<String> values = new HashSet<String>();
        for(TwitchChannel tc : twitchChannels)
        {
            values.add(tc.displayName);
        }
        editor.putStringSet(key, values);
        editor.commit();
    }
}