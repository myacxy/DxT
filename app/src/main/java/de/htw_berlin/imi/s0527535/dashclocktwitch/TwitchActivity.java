package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class TwitchActivity extends Activity {

    // initialize shared preference keys
    public static String PREF_USER_NAME = "pref_user_name";
    public static String PREF_CUSTOM_VISIBILITY = "pref_custom_visibility";
    public static String PREF_ALL_FOLLOWED_CHANNELS = "pref_all_followed_channels";
    public static String PREF_SELECTED_FOLLOWED_CHANNELS = "pref_selected_followed_channels";
    public static String PREF_UPDATE_INTERVAL = "pref_update_interval";
    public static String PREF_LAST_UPDATE = "pref_last_update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitch_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_twitch_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), TwitchSettingsActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.action_json)
        {
            updateTwitchChannels(this);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * TODO: javadoc
     *
     * @param context
     * @param callback
     */
    static void updateTwitchChannels(final Context context, final Callback callback) {
        // initialize JsonGetter
        final TwitchJsonGetter twitchJsonGetter = new TwitchJsonGetter(context);

        twitchJsonGetter.update(new Callback() {
            @Override
            public void run(Object object) {
                // retrieve followed channels
                JSONArray jsonAllFollowedChannels = null;
                try {
                    JSONObject jsonObject = (JSONObject) object;
                    if(jsonObject.has("status") && jsonObject.getString("status").equals("404"))
                    {
                        Toast.makeText(context, "User does not exist.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    jsonAllFollowedChannels = jsonObject.getJSONArray("follows");
                    if (jsonAllFollowedChannels == null)
                    {
                        Toast.makeText(context, "No channels found.", Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // analyse json data and parse it
                ArrayList<TwitchChannel> allFollowedChannels = twitchJsonGetter.parseJsonObject(jsonAllFollowedChannels);

                if (allFollowedChannels != null) {
                    // save all followed channels to shared preferences
                    twitchJsonGetter.saveTwitchChannelsToPreferences(allFollowedChannels, TwitchActivity.PREF_ALL_FOLLOWED_CHANNELS);
                    // save all followed channels to database
                    new TwitchDbHelper(context).saveChannels(allFollowedChannels);
                    // retrieve selected followed channels

                    // TODO: display selected channels
//                    ArrayList<TwitchChannel> selectedFollowedChannels = getSelectedFollowedChannels(allFollowedChannels);
//                    if (selectedFollowedChannels != null) {
//                        twitchJsonGetter.saveTwitchChannelsToPreferences(selectedFollowedChannels, TwitchActivity.PREF_SELECTED_FOLLOWED_CHANNELS);
//                    }
                    // save the time of this update
                    twitchJsonGetter.saveCurrentTime();
                }

                // allow injectable code by callback
                if (callback != null) {
                    callback.run(allFollowedChannels);
                }
            }
        });
    } // updateTwitchChannels

    public void updateTwitchChannels(Context context)
    {
        updateTwitchChannels(context, null);
    } // updateTwitchChannels


    /**
     * TODO: javadoc
     *
     * @param allFollowedChannels
     * @return
     */
    protected ArrayList<TwitchChannel> getSelectedFollowedChannels(ArrayList<TwitchChannel> allFollowedChannels)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean customVisibility = sp.getBoolean(TwitchActivity.PREF_CUSTOM_VISIBILITY, false);
        Set<String> selectedFollowedChannels = sp.getStringSet(TwitchActivity.PREF_SELECTED_FOLLOWED_CHANNELS, null);

        if(!customVisibility || selectedFollowedChannels == null)
        {
            selectedFollowedChannels = new HashSet<String>();

        }
        return null;
    }
} // TwitchActivity