package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;


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

        initView();
    }

    @Override
    protected void onResume() {
        initView();
        super.onResume();
    }

    /**
     * TODO: javadoc / comments
     */
    public void initView()
    {
        ListView listView = (ListView) TwitchActivity.this.findViewById(R.id.main_list);
        ListAdapter mAdapter = new ListAdapter(this);
        SQLiteDatabase mDb = new TwitchDbHelper(this).getReadableDatabase();

        String sortOrder = TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME;

        String selection = TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE + " LIKE ?" +
                " AND " + TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED + " LIKE ?";
        String[] selectionArgs = new String[] { "1", "1" };
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sp.getBoolean(PREF_CUSTOM_VISIBILITY, false)){
            selection = TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE + " LIKE ?";
            selectionArgs = new String[] { "1" };
        }
        // get all entries of the table from the database
        Cursor cursor = mDb.query(
                TwitchContract.ChannelEntry.TABLE_NAME,         // the table to query
                TwitchDbHelper.ChannelQuery.projection,         // the columns to return
                selection, // the columns for the WHERE clause
                selectionArgs,                                  // the values for the WHERE clause
                null,                                           // don't group the rows
                null,                                           // don't filter by row groups
                sortOrder                                       // the sort order
        );
        // reassign the cursor
        mAdapter.swapCursor(cursor);
        listView.setAdapter(mAdapter);
    }
    public class ListAdapter extends ResourceCursorAdapter
    {

        public ListAdapter(Context context)
        {
            // inflate row layout
            super(context, R.layout.list_item_following_short, null, false);
        }


        /**
         * Set elements of each row
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // initialize view for display name
            final String displayName = cursor.getString(TwitchDbHelper.ChannelQuery.displayName);
            TextView selectionDisplayName = (TextView) view.findViewById(
                    R.id.dialog_following_selection_display_name);
            selectionDisplayName.setText(displayName);
            // initialize view for game
            TextView selectionGame = (TextView) view.findViewById(
                    R.id.dialog_following_selection_game);
            selectionGame.setText(context.getResources().getString(R.string.dialog_following_selection_game)
                    + ": " + cursor.getString(TwitchDbHelper.ChannelQuery.game));
            // initialize view for status
            TextView selectionStatus = (TextView) view.findViewById(
                    R.id.dialog_following_selection_status);
            selectionStatus.setText(cursor.getString(TwitchDbHelper.ChannelQuery.status));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                } // onClick
            });
        } // bindView
    } // ListAdapter

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
     */
    public static void updateTwitchChannels(final Context context) {
        // initialize JsonGetter
        final TwitchChannelGetter twitchChannelGetter = new TwitchChannelGetter(context);

        twitchChannelGetter.updateAllFollowedChannels();
    } // updateTwitchChannels

} // TwitchActivity