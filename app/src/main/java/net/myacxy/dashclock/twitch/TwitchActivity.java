package net.myacxy.dashclock.twitch;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import net.myacxy.dashclock.twitch.io.TwitchDbHelper;
import net.myacxy.dashclock.twitch.ui.MainDialog;


public class TwitchActivity extends Activity {

    // initialize shared preference keys
    public static String PREF_USER_NAME = "pref_user_name";
    public static String PREF_CUSTOM_VISIBILITY = "pref_custom_visibility";
    public static String PREF_ALL_FOLLOWED_CHANNELS = "pref_all_followed_channels";
    public static String PREF_SELECTED_FOLLOWED_CHANNELS = "pref_selected_followed_channels";
    public static String PREF_UPDATE_INTERVAL = "pref_update_interval";
    public static String PREF_LAST_UPDATE = "pref_last_update";

    protected TwitchDbHelper mDbHelper;
    protected Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitch_main);

        initView();
    }

    @Override
    protected void onStop() {
        mDbHelper.close();
        mCursor.close();
        super.onDestroy();
    }

    /**
     * TODO: javadoc / comments
     */
    public void initView()
    {
        ListView listView = (ListView) TwitchActivity.this.findViewById(R.id.main_list);

        ListAdapter adapter = new ListAdapter(this);
        mDbHelper = new TwitchDbHelper(this);
        mCursor = mDbHelper.getChannelsCursor(true, true);

        // reassign the cursor
        adapter.swapCursor(mCursor);
        listView.setAdapter(adapter);
    }
    public class ListAdapter extends ResourceCursorAdapter
    {

        public ListAdapter(Context context)
        {
            // inflate row layout
            super(context, R.layout.list_item_following_short, null, false);
        }


        /** Set elements of each row */
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
        else if (id == R.id.action_dialog)
        {
            FragmentManager fragmentManager = getFragmentManager();
            MainDialog dialog = new MainDialog();
            dialog.show(fragmentManager, "dialog");
        }
        return super.onOptionsItemSelected(item);
    }
} // TwitchActivity
