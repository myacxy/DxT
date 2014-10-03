package net.myacxy.dashclock.twitch.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import net.myacxy.dashclock.twitch.R;
import net.myacxy.dashclock.twitch.TwitchExtension;
import net.myacxy.dashclock.twitch.io.AsyncTaskListener;
import net.myacxy.dashclock.twitch.io.TwitchDbHelper;

public class MainDialog extends DialogFragment {

    protected DialogListener mListener;
    protected TwitchDbHelper mDbHelper;
    protected Cursor mCursor;

    public interface DialogListener
    {
        public void onDialogDismiss(DialogInterface dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_main, null, false);
        initView(view);
        builder.setView(view);
        // enable buttons
        builder.setNegativeButton(R.string.dialog_main_dismiss, null);
        builder.setPositiveButton(R.string.dialog_main_update, null);

        final AlertDialog alertDialog = builder.create();
        // override OnShowListener so that the dialog will
        // not be dismissed if a button was pressed
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                // set dismiss button
                Button dismissButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                // set update button
                Button updateButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TwitchExtension.updateTwitchChannels(builder.getContext(),
                                new ProgressDialog(builder.getContext()),
                                new AsyncTaskListener() {
                            @Override
                            public void handleAsyncTaskFinished() {
                                if(isVisible()) {
                                    initView(view);
                                }
                                new TwitchDbHelper(getActivity()).updatePublishedData();
                            }
                        });
                    }
                });
            }
        });
        return alertDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mListener.onDialogDismiss(dialog);
        mDbHelper.close();
        mCursor.close();
        super.onDismiss(dialog);
    }

    /**
     * TODO: javadoc / comments
     */
    public void initView(View view) {
        // initialize
        ListAdapter adapter = new ListAdapter(getActivity());
        mDbHelper = new TwitchDbHelper(getActivity());
        // get list from the dialog view
        ListView listView = (ListView) view.findViewById(R.id.dialog_main_list);
        // get cursor for all channels
        mCursor = mDbHelper.getChannelsCursor(false, true);
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
}
