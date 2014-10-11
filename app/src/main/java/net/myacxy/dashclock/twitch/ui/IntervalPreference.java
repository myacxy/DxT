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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import net.myacxy.dashclock.twitch.R;
import net.myacxy.dashclock.twitch.TwitchExtension;
import net.myacxy.dashclock.twitch.io.AsyncTaskListener;
import net.myacxy.dashclock.twitch.io.TcocManager;
import net.myacxy.dashclock.twitch.io.TwitchDbHelper;

public class IntervalPreference extends Preference {

    public IntervalPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public IntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntervalPreference(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        // set up number picker
        final NumberPicker numberPicker = new NumberPicker(getContext());
        numberPicker.setMinValue(5);
        numberPicker.setMaxValue(60);
        // init builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getResources().getString(R.string.dialog_update_interval_title));
        // set layout
        RelativeLayout layout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(numberPicker, params);
        builder.setView(layout);
        // retrieve previous setting
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        final int previousValue = sp.getInt(TwitchExtension.PREF_UPDATE_INTERVAL, 5);
        numberPicker.setValue(previousValue);
        // save current value on ok
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // save value to preferences
                sp.edit().putInt(TwitchExtension.PREF_UPDATE_INTERVAL, numberPicker.getValue())
                         .apply();
                // update channels if last update is older than new interval
                if (!TcocManager.checkRecentlyUpdated(getContext())) {
                    TwitchExtension.updateTwitchChannels(getContext(),
                            new ProgressDialog(getContext()),
                            new AsyncTaskListener() {
                        @Override
                        public void handleAsyncTaskFinished() {
                            new TwitchDbHelper(getContext()).updatePublishedData();
                        }
                    });
                }
                // notify change listener
                getOnPreferenceChangeListener().onPreferenceChange(IntervalPreference.this,
                        numberPicker.getValue());
            }
        });
        // reset to previous value on ok
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sp.edit().putInt(TwitchExtension.PREF_UPDATE_INTERVAL, previousValue).apply();
            }
        });
        builder.create();
        builder.show();
    }
}