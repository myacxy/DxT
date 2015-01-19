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

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import net.myacxy.dashclock.twitch.TwitchExtension;
import net.myacxy.dashclock.twitch.database.TwitchContract;
import net.myacxy.dashclock.twitch.database.TwitchDbHelper;
import net.myacxy.dashclock.twitch.io.AsyncTaskListener;

import java.util.HashSet;

/**
 * Simple EditTextPreference that reacts to clicking the OK button.
 *
 */
public class UserNameDialog extends EditTextPreference
{
    public UserNameDialog(Context context) {
        super(context);
    }

    public UserNameDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserNameDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            // edit preferences
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sp.edit();
            // deselect previously selected channels
            editor.putStringSet(TwitchExtension.PREF_SELECTED_FOLLOWED_CHANNELS,
                    new HashSet<String>())
                  .apply();
            editor.putStringSet(TwitchExtension.PREF_ALL_FOLLOWED_CHANNELS,
                    new HashSet<String>())
                  .apply();
            TwitchDbHelper dbHelper = new TwitchDbHelper(getContext());

            dbHelper.getWritableDatabase()
                    .delete(TwitchContract.ChannelEntry.TABLE_NAME, null, null);
            dbHelper.close();
            dbHelper.updatePublishedData();

            // remove whitespaces
            final String userName = getEditText().getText().toString().trim();
            editor.putString(TwitchExtension.PREF_USER_NAME, userName).apply();
            // update channels for new user name
            TwitchExtension.getInstance().updateTwitchChannels(getContext(),
                    true,
                    new AsyncTaskListener() {
                @Override
                public void handleAsyncTaskFinished() {
                    new TwitchDbHelper(getContext()).updatePublishedData();
                    getOnPreferenceChangeListener().onPreferenceChange(UserNameDialog.this, userName);
                }
            });
        }
        super.onClick(dialog, which);
    }
}
