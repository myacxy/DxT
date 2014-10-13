package net.myacxy.dashclock.twitch.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import net.myacxy.dashclock.twitch.R;
import net.myacxy.dashclock.twitch.io.AsyncTaskListener;
import net.myacxy.dashclock.twitch.io.TggManager;
import net.myacxy.dashclock.twitch.io.TwitchDbHelper;

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
//        mCursor = mDbHelper.getGamesCursor();
//        // reassign the cursor
//        mAdapter.swapCursor(mCursor);

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

        ImageButton button = (ImageButton) view.findViewById(R.id.dialog_abbr_add);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAdapter.notifyDataSetChanged();
            }
        });

        builder.setView(view);

        // set buttons
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setNeutralButton("Update DB", null);
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
                        dialog.dismiss();
                    }
                });

                Button update = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TggManager tggManager = new TggManager(getContext(), true);
                        tggManager.setAsyncTaskListener(new AsyncTaskListener() {
                            @Override
                            public void handleAsyncTaskFinished() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                        tggManager.run(10, 10);
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
        public ListAdapter(Context context)
        {
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
            EditText abbreviation = (EditText) view.findViewById(R.id.dialog_abbr_abbreviation);
            abbreviation.setText(cursor.getString(TwitchDbHelper.GameQuery.abbreviation));

            ImageButton delete = (ImageButton) view.findViewById(R.id.dialog_abbr_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } // bindView
    } // ListAdapter
} // AbbreviationDialog
