package net.myacxy.dashclock.twitch.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import net.myacxy.dashclock.twitch.R;
import net.myacxy.dashclock.twitch.io.TwitchDbHelper;

public class AbbreviationDialog extends ListPreference {

    private Context mContext;
    private ListAdapter mAdapter;
    protected TwitchDbHelper mDbHelper;
    protected Cursor mCursor;

    public AbbreviationDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbbreviationDialog(Context context) {
        super(context);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
    }

    /** TODO: javadoc / comments */
    public class ListAdapter extends ResourceCursorAdapter
    {

        public ListAdapter(Context context)
        {
            // inflate row layout
            super(context, R.layout.list_item_abbr, null, false);
        }

        /** Set elements of each row */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // initialize view for display name
            final String displayName = cursor.getString(TwitchDbHelper.GameQuery.name);
            TextView game = (TextView) view.findViewById(
                    R.id.dialog_abbr_game);
            game.setText(cursor.getString(TwitchDbHelper.GameQuery.name));

            // initialize view for status
            TextView abbreviation = (TextView) view.findViewById(R.id.dialog_abbr_abbreviation);
            abbreviation.setText(cursor.getString(TwitchDbHelper.GameQuery.abbreviation));

        } // bindView
    } // ListAdapter
}
