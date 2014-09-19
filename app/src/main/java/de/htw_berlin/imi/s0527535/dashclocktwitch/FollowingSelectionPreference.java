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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class FollowingSelectionPreference extends MultiSelectListPreference
{
    private Set<String> mSelectedFollowedChannels;
    private ArrayList<TwitchChannel> mAllFollowedChannels;
    private Context mContext;
    private ListAdapter mAdapter;
    private SQLiteDatabase mDb;

    public FollowingSelectionPreference(Context context) {
        this(context, null);
    }

    /**
     * TODO: javadoc
     *
     * @param context
     * @param attrs
     */
    public FollowingSelectionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAdapter = new ListAdapter(context);
        mDb = new TwitchDbHelper(context).getReadableDatabase();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSelectedFollowedChannels = sp.getStringSet(TwitchActivity.PREF_SELECTED_FOLLOWED_CHANNELS, new HashSet<String>());
        float lastUpdate = sp.getFloat(TwitchActivity.PREF_LAST_UPDATE, 0);
        float difference = (System.currentTimeMillis() - lastUpdate) / 60000f;
        int updateInterval = sp.getInt(TwitchActivity.PREF_UPDATE_INTERVAL, 0);
        if(difference >= updateInterval || difference == 0)
        {
            TwitchActivity.updateTwitchChannels(mContext, new Callback() {
                @Override
                public void run(Object object) {
                    mAllFollowedChannels = (ArrayList<TwitchChannel>) object;
                }
            });
        }
    }

    /**
     * TODO: javadoc / comments
     */
    @Override
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
        String sortOrder = TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME;

        Cursor cursor = mDb.query(
                TwitchContract.ChannelEntry.TABLE_NAME, // the table to query
                TwitchDbHelper.ChannelQuery.projection,          // the columns to return
                null,                                   // the columns for the WHERE clause
                null,                                   // the values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                sortOrder                               // the sort order
        );

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
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sp.edit();
                editor.putStringSet(TwitchActivity.PREF_SELECTED_FOLLOWED_CHANNELS, mSelectedFollowedChannels);
                editor.commit();
            }
        });
    }

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
         * Set elements of the row
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            final String displayName = cursor.getString(TwitchDbHelper.ChannelQuery.displayName);
            TextView selectionDisplayName = (TextView) view.findViewById(R.id.dialog_following_selection_display_name);
            selectionDisplayName.setText(displayName);

            TextView selectionGame = (TextView) view.findViewById(R.id.dialog_following_selection_game);
            selectionGame.setText(R.string.dialog_following_selection_game
                    + ": " + cursor.getString(TwitchDbHelper.ChannelQuery.game));

            TextView selectionStatus = (TextView) view.findViewById(R.id.dialog_following_selection_status);
            selectionStatus.setText(cursor.getString(TwitchDbHelper.ChannelQuery.status));

            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_following_selection_checkbox);

            checkBox.setChecked(mSelectedFollowedChannels.contains(displayName));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedFollowedChannels.contains(displayName)) {
                        mSelectedFollowedChannels.remove(displayName);
                        checkBox.setChecked(false);
                    } else {
                        mSelectedFollowedChannels.add(displayName);
                        checkBox.setChecked(true);
                    }
                }
            });
        }
    }
}
