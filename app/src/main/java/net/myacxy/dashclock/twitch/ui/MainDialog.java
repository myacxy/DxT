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

/**
 * MergeAdapter
 *
 * Copyright    (c) 2008-2009 CommonsWare, LLC http://commonsware.com/
 * Portions     (c) 2009 Google, Inc.
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use
 * this file except in compliance with the License. A copy of the License is
 * located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.myacxy.dashclock.twitch.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;

import net.myacxy.dashclock.twitch.R;
import net.myacxy.dashclock.twitch.TwitchExtension;
import net.myacxy.dashclock.twitch.database.ChannelQuery;
import net.myacxy.dashclock.twitch.database.TwitchContract;
import net.myacxy.dashclock.twitch.database.TwitchDbHelper;
import net.myacxy.dashclock.twitch.io.AsyncTaskListener;
import net.myacxy.dashclock.twitch.io.DialogListener;
import net.myacxy.dashclock.twitch.io.TwitchUserFollowsGetter;
import net.myacxy.dashclock.twitch.models.TwitchGame;

public class MainDialog extends DialogFragment {

    protected SharedPreferences mSp;
    protected DialogListener mListener;
    protected TwitchDbHelper mDbHelper;
    protected Cursor mCursor;
    private TwitchUserFollowsGetter followsGetter;
    private View mDialogView;
    boolean hideNeutral;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mSp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        hideNeutral = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mDialogView = inflater.inflate(R.layout.dialog_main, null);
        initView();
        builder.setView(mDialogView);
        // enable buttons
        builder.setNegativeButton(R.string.dismiss, null);
        if(!hideNeutral) builder.setNeutralButton(R.string.dialog_main_toggle_offline, null);
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

                if(!hideNeutral) {
                    // set toggle button
                    final Button toggleOfflineButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);

                    toggleOfflineButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean toggle = mSp.getBoolean(TwitchExtension.PREF_DIALOG_SHOW_OFFLINE, false);
                            mSp.edit().putBoolean(TwitchExtension.PREF_DIALOG_SHOW_OFFLINE, !toggle).apply();
                            initView();
                        }
                    });
                }

                // set update button
                Button updateButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        followsGetter = TwitchExtension.updateTwitchChannels(builder.getContext(),
                                true,
                                new AsyncTaskListener() {
                            @Override
                            public void handleAsyncTaskFinished() {
                                Log.d("MainDialog", "Update finished.");
                                if(getActivity() != null) {
                                    // reinit view and update data
                                    initView();
                                    new TwitchDbHelper(getActivity()).updatePublishedData();
                                }
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
        mDbHelper.close();
        mCursor.close();
        mListener.onDialogDismiss(dialog);
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        // cancel async tasks
        if(followsGetter == null);
        else if(followsGetter.getStatus() != AsyncTask.Status.FINISHED)
            followsGetter.cancel(true);
        else if(followsGetter.tcocManager != null)
            if(followsGetter.tcocManager.getStatus() != AsyncTask.Status.FINISHED)
                followsGetter.tcocManager.cancel(true);

        super.onDestroy();
    }

    /**
     * Initializes the ListView inside the main dialog. A MergeAdapter is being used
     * to separate online and offline channels and display a corresponding header.
     */
    public void initView() {
        boolean showOffline = showOffline = mSp.getBoolean(TwitchExtension.PREF_DIALOG_SHOW_OFFLINE, false);

        // adapter merging multiple views and adapters
        MergeAdapter mergeAdapter = new MergeAdapter();
        if(!hideNeutral && showOffline) {
            // add online header
            TextView header = new TextView(getActivity());
            header.setText(R.string.dialog_header_online);
            header.setTextAppearance(getActivity(), android.R.style.TextAppearance_Holo_DialogWindowTitle);
            mergeAdapter.addView(header);
            // add divider
            View divider = View.inflate(getActivity(), R.layout.divider, null);
            mergeAdapter.addView(divider);
        }

        // initialize database
        boolean selected = mSp.getBoolean(TwitchExtension.PREF_CUSTOM_VISIBILITY, false);
        String sortOrder = TwitchContract.ChannelEntry.COLUMN_NAME_NAME;
        mDbHelper = new TwitchDbHelper(getActivity());
        // get cursor for the channels that are online
        mCursor = mDbHelper.getChannelsCursor(selected, TwitchDbHelper.State.ONLINE, sortOrder);
        // add online list adapter
        ListAdapter listAdapter = new ListAdapter(getActivity());
        listAdapter.swapCursor(mCursor);
        mergeAdapter.addAdapter(listAdapter);

        // display offline channels?
        if(!hideNeutral && showOffline) {
            // add offline header
            TextView header = new TextView(getActivity());
            header = new TextView(getActivity());
            header.setText(R.string.dialog_header_offline);
            header.setTextAppearance(getActivity(), android.R.style.TextAppearance_Holo_DialogWindowTitle);
            mergeAdapter.addView(header);
            // add divider
            View divider = View.inflate(getActivity(), R.layout.divider, null);
            mergeAdapter.addView(divider);
            // get cursor for the channels that are offline
            mCursor = mDbHelper.getChannelsCursor(selected, TwitchDbHelper.State.OFFLINE, sortOrder);
            // add offline list adapter
            listAdapter = new ListAdapter(getActivity());
            listAdapter.swapCursor(mCursor);
            mergeAdapter.addAdapter(listAdapter);
        }

        // get list from the dialog view
        ListView listView = (ListView) mDialogView.findViewById(R.id.dialog_main_list);
        listView.setAdapter(mergeAdapter);
    }

    public class ListAdapter extends ResourceCursorAdapter
    {
        public ListAdapter(Context context)
        {
            // inflate row layout
            super(context, R.layout.list_item_main, null, false);
        }

        /** Set elements of each row */
        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            // initialize view for display name
            String displayName = cursor.getString(ChannelQuery.displayName);
            TextView displayNameView = (TextView) view.findViewById(
                    R.id.main_list_item_display_name_text);
            displayNameView.setText(displayName);
            // initialize view for game
            TextView gameView = (TextView) view.findViewById(
                    R.id.main_list_item_game);
            TwitchGame game = mDbHelper.getGame(cursor.getInt(ChannelQuery.gameId));
            gameView.setText(context.getResources().getString(R.string.main_list_item_game) + ": " + game.name);
            // initialize view for status
            TextView statusView = (TextView) view.findViewById(
                    R.id.main_list_item_status);
            statusView.setText(cursor.getString(ChannelQuery.status));
        } // bindView
    } // ListAdapter
} // MainDialog
