package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class TwitchActivity extends Activity {

    public static String PREF_USER_NAME = "pref_user_name";
    public static String PREF_CUSTOM_VISIBILITY = "pref_custom_visibility";
    public static String PREF_ALL_FOLLOWED_CHANNELS = "pref_all_followed_channels";
    public static String PREF_SELECTED_FOLLOWED_CHANNELS = "pref_selected_followed_channels";
    public static String PREF_UPDATE_INTERVAL = "pref_update_interval";
    public static String PREF_LAST_UPDATE = "pref_last_update";

    public ArrayList<TwitchChannel> allFollowedChannels;
    public ArrayList<TwitchChannel> selectedFollowedChannels;

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

    static void updateTwitchChannels(Context context, final Callback callback) {
        final TwitchJSONGetter twitchJSONGetter = new TwitchJSONGetter(context);

        twitchJSONGetter.update(new Callback() {
            @Override
            public void run(Object object) {
                JSONArray jsonAllFollowedChannels = null;
                try {
                    JSONObject jsonObject = (JSONObject) object;
                    jsonAllFollowedChannels = jsonObject.getJSONArray("follows");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayList<TwitchChannel> allFollowedChannels = twitchJSONGetter.parseJSONObject(jsonAllFollowedChannels);
                if (allFollowedChannels != null) {
                    twitchJSONGetter.saveTwitchChannels(allFollowedChannels, TwitchActivity.PREF_ALL_FOLLOWED_CHANNELS);
                    ArrayList<TwitchChannel> selectedFollowedChannels = twitchJSONGetter.getSelectedFollowedChannels(allFollowedChannels);
                    if (selectedFollowedChannels != null) {
                        twitchJSONGetter.saveTwitchChannels(selectedFollowedChannels, TwitchActivity.PREF_SELECTED_FOLLOWED_CHANNELS);
                    }
                    twitchJSONGetter.saveCurrentTime();
                }

                if (callback != null) {
                    callback.run(allFollowedChannels);
                }
            }
        });
    } // updateTwitchChannels

    public static void updateTwitchChannels(Context context)
    {
        updateTwitchChannels(context, null);
    } // updateTwitchChannels
} // TwitchActivity