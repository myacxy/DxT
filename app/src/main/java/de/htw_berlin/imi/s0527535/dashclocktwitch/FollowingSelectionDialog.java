package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;


public class FollowingSelectionDialog extends MultiSelectListPreference
{
    // previously selected / saved channels
    private Set<String> mSelectedFollowedChannels;
    // currently selected / unsaved channels
    private Set<String> mSelectedFollowedChannelsTemp;
    private Context mContext;
    private ListAdapter mAdapter;
    private SQLiteDatabase mDb;

    public FollowingSelectionDialog(Context context) {
        this(context, null);
    }

    public FollowingSelectionDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        // initialize
        mContext = context;
        mAdapter = new ListAdapter(context);
        mDb = new TwitchDbHelper(context).getReadableDatabase();
        // get previously selected channels
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSelectedFollowedChannels = sp.getStringSet(TwitchActivity.PREF_SELECTED_FOLLOWED_CHANNELS,
                new HashSet<String>());
        mSelectedFollowedChannelsTemp = new HashSet<String>(mSelectedFollowedChannels);
    }

    /**
     * TODO: javadoc / comments
     */
    @Override
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {

        String sortOrder = TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME;

        // get all entries of the table from the database
        Cursor cursor = mDb.query(
                TwitchContract.ChannelEntry.TABLE_NAME, // the table to query
                TwitchDbHelper.ChannelQuery.projection, // the columns to return
                null,                                   // the columns for the WHERE clause
                null,                                   // the values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                sortOrder                               // the sort order
        );
        // reassign the cursor
        mAdapter.swapCursor(cursor);
        buildDialog(builder);
    }

    /**
     * TODO: javadoc / comments
     */
    void buildDialog(AlertDialog.Builder builder)
    {
        builder.setAdapter(mAdapter, null);
        builder.setTitle(R.string.pref_following_selection_title);
        // Set OK Button
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // save currently selected channels
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sp.edit();
                mSelectedFollowedChannels = new HashSet<String>(mSelectedFollowedChannelsTemp);
                editor.putStringSet(TwitchActivity.PREF_SELECTED_FOLLOWED_CHANNELS,
                        mSelectedFollowedChannels);
                editor.apply();
                TwitchDbHelper twitchDbHelper = new TwitchDbHelper(mContext);

                twitchDbHelper.updateSelectionStatus(mSelectedFollowedChannels);
            }
        });
        // Set Cancel Button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // set to previously selected channels
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                sp.edit().putStringSet(TwitchActivity.PREF_SELECTED_FOLLOWED_CHANNELS,
                        mSelectedFollowedChannels)
                         .apply();
                mSelectedFollowedChannelsTemp = new HashSet<String>(mSelectedFollowedChannels);
            }
        });
    } // buildDialog

    /**
     * TODO: javadoc / comments
     */
    public class ListAdapter extends ResourceCursorAdapter
    {

        public ListAdapter(Context context)
        {
            // inflate row layout
            super(context, R.layout.list_item_following, null, false);
        }

        /**
         * Set elements of each row
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // initialize view for display name
            final String displayName = cursor.getString(TwitchDbHelper.ChannelQuery.displayName);
            boolean online = cursor.getInt(TwitchDbHelper.ChannelQuery.online) == 1;
            TextView selectionDisplayName = (TextView) view.findViewById(
                    R.id.dialog_following_selection_display_name);
            selectionDisplayName.setText(displayName + " (" + (online ? "online" : "offline") + ")");
            // initialize view for game
            TextView selectionGame = (TextView) view.findViewById(
                    R.id.dialog_following_selection_game);
            selectionGame.setText(context.getResources().getString(R.string.dialog_following_selection_game)
                    + ": " + cursor.getString(TwitchDbHelper.ChannelQuery.game));
            // initialize view for status
            TextView selectionStatus = (TextView) view.findViewById(
                    R.id.dialog_following_selection_status);
            selectionStatus.setText(cursor.getString(TwitchDbHelper.ChannelQuery.status));
            // check the checkbox if was selected beforehand
            final CheckBox checkBox = (CheckBox) view.findViewById(
                    R.id.dialog_following_selection_checkbox);
            checkBox.setChecked(mSelectedFollowedChannels.contains(displayName));

            // enable clicking the check box
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // remove on uncheck
                    if (mSelectedFollowedChannelsTemp.contains(displayName)) {
                        mSelectedFollowedChannelsTemp.remove(displayName);
                        checkBox.setChecked(false);
                    }
                    // add on check
                    else {
                        mSelectedFollowedChannelsTemp.add(displayName);
                        checkBox.setChecked(true);
                    }
                } // onClick
            });
        } // bindView
    } // ListAdapter
} // FollowingSelectionPreference