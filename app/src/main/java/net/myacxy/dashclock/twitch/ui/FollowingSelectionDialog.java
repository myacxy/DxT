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

package net.myacxy.dashclock.twitch.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import net.myacxy.dashclock.twitch.R;
import net.myacxy.dashclock.twitch.TwitchExtension;
import net.myacxy.dashclock.twitch.io.TwitchContract.ChannelEntry;
import net.myacxy.dashclock.twitch.io.TwitchDbHelper;

import java.util.HashSet;
import java.util.Set;

public class FollowingSelectionDialog extends MultiSelectListPreference
{
    // previously selected / saved channels
    private Set<String> mSelectedFollowedChannels;
    // currently selected / unsaved channels
    private Set<String> mSelectedFollowedChannelsTemp;
    private ListAdapter mAdapter;
    protected TwitchDbHelper mDbHelper;
    protected Cursor mCursor;

    public FollowingSelectionDialog(Context context) {
        this(context, null);
    }

    public FollowingSelectionDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** TODO: javadoc / comments */
    @Override
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {

        mAdapter = new ListAdapter(getContext());
        mDbHelper = new TwitchDbHelper(getContext());
        // get previously selected channels
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        mSelectedFollowedChannels = sp.getStringSet(TwitchExtension.PREF_SELECTED_FOLLOWED_CHANNELS,
                new HashSet<String>());
        mSelectedFollowedChannelsTemp = new HashSet<String>(mSelectedFollowedChannels);

        mCursor = mDbHelper.getChannelsCursor(false, TwitchDbHelper.State.ALL, ChannelEntry.COLUMN_NAME_DISPLAY_NAME);
        // reassign the cursor
        mAdapter.swapCursor(mCursor);
        buildDialog(builder);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mCursor.close();
        mDbHelper.close();
        super.onDismiss(dialog);
    }

    /** TODO: javadoc / comments */
    void buildDialog(AlertDialog.Builder builder)
    {
        builder.setAdapter(mAdapter, null);
        builder.setTitle(R.string.pref_following_selection_title);
        // Set OK Button
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // save currently selected channels
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sp.edit();
                mSelectedFollowedChannels = new HashSet<String>(mSelectedFollowedChannelsTemp);
                editor.putStringSet(TwitchExtension.PREF_SELECTED_FOLLOWED_CHANNELS,
                        mSelectedFollowedChannels).apply();
                // update database
                TwitchDbHelper twitchDbHelper = new TwitchDbHelper(getContext());
                twitchDbHelper.updateSelectionStatus(mSelectedFollowedChannels);
                twitchDbHelper.updatePublishedData();
                // notify change listener
                getOnPreferenceChangeListener().onPreferenceChange(FollowingSelectionDialog.this,
                        mSelectedFollowedChannels);
            }
        });
        // Set Cancel Button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // set to previously selected channels
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                sp.edit().putStringSet(TwitchExtension.PREF_SELECTED_FOLLOWED_CHANNELS,
                        mSelectedFollowedChannels).apply();
                mSelectedFollowedChannelsTemp = new HashSet<String>(mSelectedFollowedChannels);
            }
        });
    } // buildDialog

    /** TODO: javadoc / comments */
    public class ListAdapter extends ResourceCursorAdapter
    {

        public ListAdapter(Context context)
        {
            // inflate row layout
            super(context, R.layout.list_item_following, null, false);
        }

        /** Set elements of each row */
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