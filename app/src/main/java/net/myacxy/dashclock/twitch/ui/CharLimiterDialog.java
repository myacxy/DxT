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
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.myacxy.dashclock.twitch.R;
import net.myacxy.dashclock.twitch.TwitchExtension;

public class CharLimiterDialog extends Preference {

    public CharLimiterDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CharLimiterDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CharLimiterDialog(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        /// init builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getResources().getString(
                R.string.pref_char_limit_title));
        // init layout
        RelativeLayout layout = (RelativeLayout) View.inflate(getContext(),
                R.layout.dialog_char_limiter, null);
        builder.setView(layout);

        // retrieve previous setting
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String longestBody = sp.getString(TwitchExtension.PREF_LONGEST_BODY,
                getContext().getResources().getString(R.string.lorem_ipsum));
        SpannableString text = new SpannableString(longestBody);
        final int color = getContext().getResources().getColor(android.R.color.darker_gray);
        final int color2 = getContext().getResources().getColor(android.R.color.black);
        final int previousValue = sp.getInt(TwitchExtension.PREF_CHAR_LIMIT, 200);
        final NumberPicker numberPicker = (NumberPicker) layout.findViewById(R.id.pref_char_limiter_value);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(200);
        numberPicker.setValue(previousValue);

        final int maxLength = longestBody.length();
        text.setSpan(new ForegroundColorSpan(color), previousValue > maxLength ? maxLength : previousValue, maxLength, 0);

        final TextView textView = (TextView) layout.findViewById(R.id.pref_char_limiter_text);
        textView.setText(text);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                SpannableString newText = new SpannableString(longestBody);
                newText.setSpan(new ForegroundColorSpan(color2), 0,
                        newVal  > maxLength ? maxLength : newVal, 0);
                newText.setSpan(new ForegroundColorSpan(color),
                        newVal > maxLength ? maxLength : newVal, maxLength, 0);
                textView.setText(newText);
            }
        });

        // save current value on ok
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // notify change listener
                getOnPreferenceChangeListener().onPreferenceChange(CharLimiterDialog.this,
                        numberPicker.getValue());
            }
        });

        // reset to previous value on ok
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sp.edit().putInt(TwitchExtension.PREF_CHAR_LIMIT, previousValue).apply();
            }
        });
        builder.create();
        builder.show();
    }
}
