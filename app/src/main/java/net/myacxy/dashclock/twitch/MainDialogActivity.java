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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;

import net.myacxy.dashclock.twitch.database.ChannelQuery;
import net.myacxy.dashclock.twitch.database.TwitchContract;
import net.myacxy.dashclock.twitch.database.TwitchDbHelper;
import net.myacxy.dashclock.twitch.io.AsyncTaskListener;
import net.myacxy.dashclock.twitch.io.TwitchUserFollowsGetter;
import net.myacxy.dashclock.twitch.models.TwitchGame;

public class MainDialogActivity extends Activity {

    protected SharedPreferences mSharedPreferences;
    protected TwitchDbHelper mDbHelper;
    protected Cursor mCursor;
    private TwitchUserFollowsGetter mFollowsGetter;
    private boolean showOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showAsPopup(this);
        setContentView(R.layout.main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        showOffline = mSharedPreferences.getBoolean(
                TwitchExtension.PREF_DIALOG_SHOW_OFFLINE, false);

        initView();
    }

    public void showAsPopup(Activity activity) {
        //To show activity as dialog and dim the background, you need to declare android:theme="@style/PopupTheme" on for the chosen activity on the manifest
        activity.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        activity.getWindow().setAttributes(params);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Button button = (Button) findViewById(R.id.main_dismiss);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_show_offline).setChecked(showOffline);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_show_offline: {
                showOffline = !item.isChecked();
                mSharedPreferences.edit()
                        .putBoolean(TwitchExtension.PREF_DIALOG_SHOW_OFFLINE, showOffline)
                        .apply();
                item.setChecked(showOffline);
                initView();
                break;
            }
            case R.id.action_update: {
                mFollowsGetter = TwitchExtension.updateTwitchChannels(this,
                        new ProgressDialog(this),
                        new AsyncTaskListener() {
                            @Override
                            public void handleAsyncTaskFinished() {
                                Log.d("MainDialog", "Update finished.");
                                if (getApplicationContext() != null) {
                                    // reinit view and update data
                                    initView();
                                    new TwitchDbHelper(getApplicationContext())
                                            .updatePublishedData();
                                }
                            }
                        });
                break;
            }
            case R.id.action_settings: {
                startActivity(new Intent(this, TwitchSettingsActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializes the ListView inside the main dialog. A MergeAdapter is being used
     * to separate online and offline channels and display a corresponding header.
     */
    public void initView() {


        // adapter merging multiple views and adapters
        MergeAdapter mergeAdapter = new MergeAdapter();
        if(showOffline) {
            // add online header
            TextView header = new TextView(this);
            header.setText(R.string.dialog_header_online);
            header.setTextAppearance(this, android.R.style.TextAppearance_Holo_DialogWindowTitle);
            mergeAdapter.addView(header);
            // add divider
            View divider = View.inflate(this, R.layout.divider, null);
            mergeAdapter.addView(divider);
        }

        // initialize database
        boolean selected = mSharedPreferences.getBoolean(TwitchExtension.PREF_CUSTOM_VISIBILITY, false);
        String sortOrder = TwitchContract.ChannelEntry.COLUMN_NAME_NAME;
        mDbHelper = new TwitchDbHelper(this);
        // get cursor for the channels that are online
        mCursor = mDbHelper.getChannelsCursor(selected, TwitchDbHelper.State.ONLINE, sortOrder);
        // add online list adapter
        ListAdapter listAdapter = new ListAdapter(this);
        listAdapter.swapCursor(mCursor);
        mergeAdapter.addAdapter(listAdapter);

        // display offline channels?
        if(showOffline) {
            // add offline header
            TextView header = new TextView(this);
            header.setText(R.string.dialog_header_offline);
            header.setTextAppearance(this, android.R.style.TextAppearance_Holo_DialogWindowTitle);
            mergeAdapter.addView(header);
            // add divider
            View divider = View.inflate(this, R.layout.divider, null);
            mergeAdapter.addView(divider);
            // get cursor for the channels that are offline
            mCursor = mDbHelper.getChannelsCursor(selected, TwitchDbHelper.State.OFFLINE, sortOrder);
            // add offline list adapter
            listAdapter = new ListAdapter(this);
            listAdapter.swapCursor(mCursor);
            mergeAdapter.addAdapter(listAdapter);
        }

        // get list from the dialog view
        ListView listView = (ListView) findViewById(R.id.main_list);
        listView.setAdapter(mergeAdapter);
    }

    public class ListAdapter extends ResourceCursorAdapter
    {
        public ListAdapter(Context context)
        {
            // inflate row layout
            super(context, R.layout.list_item_main, null, false);
        }

        /** Set elements of each row */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // initialize view for display name
            String displayName = cursor.getString(ChannelQuery.displayName);
            TextView displayNameView = (TextView) view.findViewById(
                    R.id.main_list_item_display_name_text);
            displayNameView.setText(displayName);
            // initialize view for game
            TextView gameView = (TextView) view.findViewById(R.id.main_list_item_game_text);
            TwitchGame game = mDbHelper.getGame(cursor.getInt(ChannelQuery.gameId));
            gameView.setText(game.name);

            // initialize view for status
            TextView statusView = (TextView) view.findViewById(R.id.main_list_status_text);
            statusView.setText(cursor.getString(ChannelQuery.status));

            // initialize view for viewers
            TextView viewersView = (TextView) view.findViewById(R.id.main_list_viewers_text);
            viewersView.setText(cursor.getString(ChannelQuery.viewers));

            // initialize view for viewers
            TextView updatedAtView = (TextView) view.findViewById(R.id.main_list_updated_at_text);
            updatedAtView.setText(cursor.getString(ChannelQuery.updatedAt));

        } // bindView
    } // ListAdapter

    @Override
    public void onDestroy() {
        mDbHelper.close();
        mCursor.close();
        // cancel async tasks
        if(mFollowsGetter == null);
        else if(mFollowsGetter.getStatus() != AsyncTask.Status.FINISHED)
            mFollowsGetter.cancel(true);
        else if(mFollowsGetter.tcocManager != null)
            if(mFollowsGetter.tcocManager.getStatus() != AsyncTask.Status.FINISHED)
                mFollowsGetter.tcocManager.cancel(true);

        super.onDestroy();
    }
}