package net.myacxy.dashclock.twitch.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import net.myacxy.dashclock.twitch.R;
import net.myacxy.dashclock.twitch.database.GameQuery;
import net.myacxy.dashclock.twitch.database.TwitchDbHelper;
import net.myacxy.dashclock.twitch.io.DialogListener;

import java.util.ArrayList;

public class AbbreviationDialog extends MultiSelectListPreference {

    private ListAdapter mAdapter;
    protected TwitchDbHelper mDbHelper;
    protected Cursor mCursor;
    protected ArrayList<String> mSelectedGames;

    public AbbreviationDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbbreviationDialog(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        mSelectedGames = new ArrayList<>();
        mAdapter = new ListAdapter(getContext());
        mDbHelper = new TwitchDbHelper(getContext());
        mCursor = mDbHelper.getGamesCursor(true);
        // reassign the cursor
        mAdapter.swapCursor(mCursor);

        builder.setTitle(R.string.dialog_abbr_title);
        View view = View.inflate(getContext(), R.layout.dialog_abbr, null);
        ListView listView = (ListView) view.findViewById(R.id.dialog_abbr_list);
        listView.setAdapter(mAdapter);

        builder.setView(view);

        // set buttons
        builder.setNegativeButton(R.string.dialog_abbr_delete, null);
        builder.setNeutralButton(R.string.dialog_abbr_add, null);
        builder.setPositiveButton(android.R.string.ok, null);

        final AlertDialog dialog = builder.create();
        // new onShowListener to prevent automatic dismiss
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button delete = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TwitchDbHelper dbHelper = new TwitchDbHelper(getContext());
                        for(String game : mSelectedGames) {
                            dbHelper.deleteAbbreviation(game);
                        }
                        mAdapter.changeCursor(mDbHelper.getGamesCursor(true));
                        mAdapter.notifyDataSetChanged();
                        mSelectedGames.clear();
                    }
                });

                final Button add = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Activity activity = (Activity) getContext();
                        FragmentManager fragmentManager = activity.getFragmentManager();
                        AddAbbreviationDialog addDialog = new AddAbbreviationDialog();
                        addDialog.setDialogListener(new DialogListener() {
                            @Override
                            public void onDialogDismiss(DialogInterface dialog) {
                                mAdapter.changeCursor(mDbHelper.getGamesCursor(true));
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                        addDialog.show(fragmentManager, "addAbbrDialog");

                    }
                });

                Button ok = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // notify change listener
                getOnPreferenceChangeListener().onPreferenceChange(AbbreviationDialog.this,
                        mAdapter.getCount());
                mCursor.close();
                mDbHelper.close();
                mDbHelper.updatePublishedData();
            }
        });

        dialog.show();
    }

    /** TODO: javadoc / comments */
    public class ListAdapter extends ResourceCursorAdapter
    {
        public ListAdapter(Context context) {
            // inflate row layout
            super(context, R.layout.list_item_abbr, null, false);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }

        /** Set elements of each row */
        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            final int position = cursor.getPosition();
            TextView game = (TextView) view.findViewById(
                    R.id.dialog_abbr_game);
            String gameName = cursor.getString(GameQuery.name);
            game.setText(gameName);

            // initialize view for status
            TextView abbreviation = (TextView) view.findViewById(R.id.dialog_abbr_abbreviation);
            abbreviation.setText(cursor.getString(GameQuery.abbreviation));

            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_abbr_check);
            if(mSelectedGames.contains(gameName)) checkBox.setChecked(true);
            else checkBox.setChecked(false);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                    cursor.moveToPosition(position);
                    if(checkBox.isChecked()) {
                        mSelectedGames.add(cursor.getString(GameQuery.name));
                    } else {
                        mSelectedGames.remove(cursor.getString(GameQuery.name));
                    }
                }
            });
        } // bindView
    } // ListAdapter
} // AbbreviationDialog
