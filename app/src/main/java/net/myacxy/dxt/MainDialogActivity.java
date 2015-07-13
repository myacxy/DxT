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

package net.myacxy.dxt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import net.myacxy.dxt.database.ChannelQuery;
import net.myacxy.dxt.database.TwitchContract;
import net.myacxy.dxt.database.TwitchDbHelper;
import net.myacxy.dxt.io.AsyncTaskListener;
import net.myacxy.dxt.io.TwitchUserFollowsGetter;
import net.myacxy.dxt.models.TwitchChannelListViewEntry;
import net.myacxy.dxt.models.TwitchGame;

import java.util.ArrayList;

public class MainDialogActivity extends Activity {

    /**
     * online / offline tabs
     */
    protected TabHost mTabHost;
    protected SharedPreferences mSharedPreferences;
    protected TwitchDbHelper mDbHelper;
    protected Cursor mCursor;
    private TwitchUserFollowsGetter mFollowsGetter;
    private boolean showOffline;

    private ArrayList<TwitchChannelListViewEntry> listEntry = new ArrayList<TwitchChannelListViewEntry>()
    {
        // rowKey, rowId, queryId, textId
        {
            // display name
            add(new TwitchChannelListViewEntry(
                    TwitchExtension.PREF_MAIN_LIST_SHOW_NAME,
                    R.id.main_list_item_display_name_text,
                    ChannelQuery.displayName,
                    R.id.main_list_item_display_name_text));
            // game
            add(new TwitchChannelListViewEntry(
                    TwitchExtension.PREF_MAIN_LIST_SHOW_GAME,
                    R.id.main_list_item_row_game,
                    ChannelQuery.gameId,
                    R.id.main_list_item_game_text));
            // status
            add(new TwitchChannelListViewEntry(
                    TwitchExtension.PREF_MAIN_LIST_SHOW_STATUS,
                    R.id.main_list_item_row_status,
                    ChannelQuery.status,
                    R.id.main_list_item_status_text));
            // viewers
            add(new TwitchChannelListViewEntry(
                    TwitchExtension.PREF_MAIN_LIST_SHOW_VIEWERS,
                    R.id.main_list_item_row_viewers,
                    ChannelQuery.viewers,
                    R.id.main_list_item_viewers_text));
            // followers
            add(new TwitchChannelListViewEntry(
                    TwitchExtension.PREF_MAIN_LIST_SHOW_FOLLOWERS,
                    R.id.main_list_item_row_followers,
                    ChannelQuery.followers,
                    R.id.main_list_item_followers_text));
            // last updated
            add(new TwitchChannelListViewEntry(
                    TwitchExtension.PREF_MAIN_LIST_SHOW_UPDATED,
                    R.id.main_list_item_row_updated,
                    ChannelQuery.updatedAt,
                    R.id.main_list_item_updated_text));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        showAsPopup(this);
        setContentView(R.layout.main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("online")
                .setIndicator("Online")
                .setContent(R.id.main_tab_online));
        mTabHost.addTab(mTabHost.newTabSpec("offline")
                .setIndicator("Offline")
                .setContent(R.id.main_tab_offline));

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            @Override
            public void onTabChanged(String tabId)
            {
                switch (tabId)
                {
                    case "online":
                        initView((ListView) findViewById(R.id.main_tab_online_list),
                                TwitchDbHelper.State.ONLINE);
                        break;
                    case "offline":
                        initView((ListView) findViewById(R.id.main_tab_offline_list),
                                TwitchDbHelper.State.OFFLINE);
                        break;
                }
            }
        });
        // refresh online tab view
        mTabHost.setCurrentTabByTag("offline");
        mTabHost.setCurrentTabByTag("online");
    } // onCreate

    public void showAsPopup(Activity activity) {
        // To show activity as dialog and dim the background,
        // you need to declare android:theme="@style/PopupTheme"
        // on for the chosen activity on the manifest
        activity.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        activity.getWindow().setAttributes(params);
    } // showAsPopup

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        Button button = (Button) findViewById(R.id.main_dismiss);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    } // onPostCreate

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
            case R.id.action_update: {
                mFollowsGetter = TwitchExtension.getInstance().updateTwitchChannels(this, true,
                        new AsyncTaskListener() {
                            @Override
                            public void handleAsyncTaskFinished() {
                                Log.d("MainDialog", "Update finished.");
                                if (getApplicationContext() != null) {
                                    // update data
                                    new TwitchDbHelper(getApplicationContext())
                                            .updatePublishedData();
                                    // refresh view
                                    mTabHost.setCurrentTabByTag("offline");
                                    mTabHost.setCurrentTabByTag("online");
                                }
                            }
                        });
                break;
            }
            case R.id.action_show_offline: {
                showOffline = !item.isChecked();
                mSharedPreferences.edit()
                                .putBoolean(TwitchExtension.PREF_DIALOG_SHOW_OFFLINE, showOffline)
                                .apply();
                item.setChecked(showOffline);
                mTabHost.setCurrentTabByTag("offline");
                mTabHost.setCurrentTabByTag("online");
                break;
            }
            case R.id.action_settings: {
                startActivity(new Intent(this, TwitchSettingsActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void initView(ListView listView, TwitchDbHelper.State state)
    {
        // text if no channel is online / offline
        TextView empty;
        if(state == TwitchDbHelper.State.ONLINE)
        {
            empty = (TextView) findViewById(R.id.main_tab_online_empty);
            empty.setText(getString(R.string.main_list_empty_online));
        }
        else
        {
            empty = (TextView) findViewById(R.id.main_tab_offline_empty);
            empty.setText(getString(R.string.main_list_empty_offline));
        }
        // display tab host?
        showOffline = mSharedPreferences.getBoolean(
                                TwitchExtension.PREF_DIALOG_SHOW_OFFLINE, false);

        if(showOffline)
        {
            mTabHost.getTabWidget().setVisibility(View.VISIBLE);
        }
        else
        {
            mTabHost.getTabWidget().setVisibility(View.GONE);
        }

        // initialize database
        boolean selected = mSharedPreferences.getBoolean(
                TwitchExtension.PREF_CUSTOM_VISIBILITY, false);
        String sortOrder = TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME;
        mDbHelper = new TwitchDbHelper(this);
        // get cursor for the channels that are online
        mCursor = mDbHelper.getChannelsCursor(selected, state, sortOrder);

        // no channel online / offline -> display empty text
        if(mCursor.getCount() == -1 || mCursor.getCount() == 0)
        {
            listView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
        else
        {
            listView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            // populate list view
            ListAdapter listAdapter = new ListAdapter(this);
            listAdapter.swapCursor(mCursor);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    // try to open selected channel in Twitch app
                    String url = "twitch://stream/" + view.getTag().toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });
        }
    } // initView

    /***
     * Populates a list with information on all Twitch channels that are online or offline
     */
    public class ListAdapter extends ResourceCursorAdapter
    {
        public ListAdapter(Context context)
        {
            // inflate row layout
            super(context, R.layout.list_item_main, null, false);
        }

        /** Set elements of each row */
        @Override
        public void bindView(final View view, Context context, final Cursor cursor)
        {
            for(TwitchChannelListViewEntry entry : listEntry)
            {
                boolean isRowVisible;

                // always hide viewer count in offline tab
                if(mTabHost.getCurrentTabTag().equals("offline")
                        && entry.rowKey.equals(TwitchExtension.PREF_MAIN_LIST_SHOW_VIEWERS))
                {
                    isRowVisible = false;
                }
                // check user preference
                else
                {
                    isRowVisible = mSharedPreferences.getBoolean(entry.rowKey, true);
                }
                // hide row?
                if(!isRowVisible)
                {
                    view.findViewById(entry.rowId).setVisibility(View.GONE);
                }

                // game? -> retrieve game from database
                if(entry.queryId == ChannelQuery.gameId)
                {
                    TextView gameView = (TextView) view.findViewById(R.id.main_list_item_game_text);
                    TwitchGame game = mDbHelper.getGame(cursor.getInt(ChannelQuery.gameId));
                    gameView.setText(game.name);
                }
                // display name? -> also set tag of the view to identify it on being clicked
                else if (entry.queryId == ChannelQuery.displayName)
                {
                    view.setTag(cursor.getString(ChannelQuery.name));

                    TextView textView = (TextView) view.findViewById(entry.textId);
                    textView.setText(cursor.getString(entry.queryId));
                }
                else
                {
                    TextView textView = (TextView) view.findViewById(entry.textId);
                    textView.setText(cursor.getString(entry.queryId));
                }
            }

        } // bindView
    } // ListAdapter

    @Override
    public void onDestroy() {
        mDbHelper.close();
        mCursor.close();

        cancelAsyncTasks();

        super.onDestroy();
    }

    private void cancelAsyncTasks()
    {
        if(mFollowsGetter != null)
        {
            // still getting followed channels?
            if(mFollowsGetter.getStatus() != AsyncTask.Status.FINISHED)
            {
                mFollowsGetter.cancel(true);
            }
            // still checking each channel?
            else if(mFollowsGetter.tcocManager != null)
            {
                if(mFollowsGetter.tcocManager.getStatus() != AsyncTask.Status.FINISHED)
                {
                    mFollowsGetter.tcocManager.cancel(true);
                }
            }
        }
    } // cancelAsyncTasks
} // MainDialogActivity