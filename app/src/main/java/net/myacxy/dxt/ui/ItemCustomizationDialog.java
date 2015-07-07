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

package net.myacxy.dxt.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import net.myacxy.dxt.R;
import net.myacxy.dxt.TwitchExtension;

import java.util.ArrayList;

public class ItemCustomizationDialog extends MultiSelectListPreference {

    ArrayList<String> keys = new ArrayList<String>() {{
        add(TwitchExtension.PREF_MAIN_LIST_SHOW_GAME);
        add(TwitchExtension.PREF_MAIN_LIST_SHOW_STATUS);
        add(TwitchExtension.PREF_MAIN_LIST_SHOW_VIEWERS);
        add(TwitchExtension.PREF_MAIN_LIST_SHOW_FOLLOWERS);
        add(TwitchExtension.PREF_MAIN_LIST_SHOW_UPDATED);
    }};

    public ItemCustomizationDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemCustomizationDialog(Context context) {
        super(context);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        final boolean[] checkedItems = new boolean[keys.size()];

        for(String key : keys) {
            checkedItems[keys.indexOf(key)] = getSharedPreferences().getBoolean(key, true);
        }

        builder.setMultiChoiceItems(R.array.pref_main_list_item_entries, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                for(String key : keys) {
                     getEditor().putBoolean(key, checkedItems[keys.indexOf(key)]).apply();
                }
            }
        });
    } // onPrepareDialogBuilder
} // ItemCustomizationDialog
