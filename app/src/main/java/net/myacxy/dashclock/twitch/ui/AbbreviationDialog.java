package net.myacxy.dashclock.twitch.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import net.myacxy.dashclock.twitch.R;
import net.myacxy.dashclock.twitch.io.TwitchDbHelper;
import net.myacxy.dashclock.twitch.models.TwitchGame;

public class AbbreviationDialog extends Preference {

    private ListAdapter mAdapter;
    protected TwitchDbHelper mDbHelper;
    protected Cursor mCursor;

    public AbbreviationDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AbbreviationDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbbreviationDialog(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        mAdapter = new ListAdapter(getContext());
        mDbHelper = new TwitchDbHelper(getContext());
        mCursor = mDbHelper.getGamesCursor(true);
        // reassign the cursor
        mAdapter.swapCursor(mCursor);

        buildDialog();
    }

    /** TODO: javadoc / comments */
    void buildDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_abbr_title);
        View view = View.inflate(getContext(), R.layout.dialog_abbr, null);
        ListView listView = (ListView) view.findViewById(R.id.dialog_abbr_list);
        listView.setAdapter(mAdapter);

        builder.setView(view);

        // set buttons
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setNeutralButton(R.string.dialog_abbr_add, null);
        builder.setPositiveButton(android.R.string.ok, null);

        final AlertDialog dialog = builder.create();
        // new onShowListener to prevent automatic dismiss
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button cancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // add dialog
                    }
                });

                Button add = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                Button ok = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // notify change listener
                        getOnPreferenceChangeListener().onPreferenceChange(AbbreviationDialog.this,
                                mAdapter.getCount());
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    } // buildDialog

    /** TODO: javadoc / comments */
    public class ListAdapter extends ResourceCursorAdapter
    {
        public ListAdapter(Context context) {
            // inflate row layout
            super(context, R.layout.list_item_abbr, null, false);
        }

        /** Set elements of each row */
        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            TextView game = (TextView) view.findViewById(
                    R.id.dialog_abbr_game);
            game.setText(cursor.getString(TwitchDbHelper.GameQuery.name));

            // initialize view for status
            TextView abbreviation = (TextView) view.findViewById(R.id.dialog_abbr_abbreviation);
            abbreviation.setText(cursor.getString(TwitchDbHelper.GameQuery.abbreviation));

            ImageButton delete = (ImageButton) view.findViewById(R.id.dialog_abbr_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TwitchGame twitchGame = new TwitchGame(cursor.getString(TwitchDbHelper.GameQuery.name), null);
                    new TwitchDbHelper(getContext()).insertOrReplaceGameEntry(twitchGame);
                    changeCursor(mDbHelper.getGamesCursor(true));
                    mAdapter.notifyDataSetChanged();
                }
            });
        } // bindView
    } // ListAdapter
} // AbbreviationDialog
