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
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.myacxy.dashclock.twitch.R;
import net.myacxy.dashclock.twitch.database.TwitchDbHelper;
import net.myacxy.dashclock.twitch.io.DialogListener;
import net.myacxy.dashclock.twitch.models.TwitchGame;

import java.util.ArrayList;
import java.util.List;


public class AddAbbreviationDialog extends DialogFragment {

    protected AutoCompleteTextView mGameTextView;
    protected TextView mAbbrTextView;
    protected DialogListener mListener;

    public void setDialogListener(DialogListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // initialize builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = getString(R.string.dialog_abbr_add_title);
        builder.setTitle(title);

        // inflate view with custom layout
        View view = View.inflate(getActivity(), R.layout.dialog_abbr_add, null);
        builder.setView(view);

        // add suggestions adapter
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getGameTitles());
        mGameTextView = (AutoCompleteTextView) view.findViewById(R.id.dialog_abbr_add_game);
        mGameTextView.setAdapter(adapter);
        mAbbrTextView = (TextView) view.findViewById(R.id.dialog_abbr_add_abbr);

        // enable buttons
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, null);

        final AlertDialog dialog = builder.create();
        // set custom on show listener to prevent dialog from automatically dismissing
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // init cancel button
                Button cancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // init ok button
                Button ok = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String gameName = mGameTextView.getText().toString().trim();
                        String abbr = mAbbrTextView.getText().toString().trim();

                        // no game given
                        if(gameName.length() == 0) {
                            Toast.makeText(getActivity(),
                                    getString(R.string.dialog_abbr_add_hint_game_missing),
                                    Toast.LENGTH_SHORT).show();
                        }
                        // no abbr given
                        else if(abbr.length() == 0) {
                            Toast.makeText(getActivity(),
                                    getString(R.string.dialog_abbr_add_hint_abbr_missing),
                                    Toast.LENGTH_SHORT).show();
                        }
                        // input seems valid
                        else if(gameName.length() > 0 && abbr.length() > 0) {
                            // add to database and dismiss
                            TwitchDbHelper dbHelper = new TwitchDbHelper(getActivity());
                            TwitchGame game = new TwitchGame(gameName, abbr);
                            dbHelper.insertOrReplaceGameEntry(game);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mListener.onDialogDismiss(dialog);
        super.onDismiss(dialog);
    }

    /**
     * Retrieves all games from the database in order to receive their names
     *
     * @return list of all titles from games inside the database
     */
    private ArrayList<String> getGameTitles() {
        ArrayList<String> titles = new ArrayList<>();
        // get all games
        TwitchDbHelper dbHelper = new TwitchDbHelper(getActivity());
        ArrayList<TwitchGame> games = dbHelper.getGames(false);
        // add each title
        for(TwitchGame game : games) titles.add(game.name);

        return titles;
    }

    private class AutoCompleteAdapter extends ArrayAdapter<String> {

        public AutoCompleteAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            v.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    // hide the keyboard if an item in the list has been touched
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        InputMethodManager imm = (InputMethodManager) getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(
                                mGameTextView.getWindowToken(), 0);
                    }

                    return false;
                }
            });

            return v;
        }
    }
}
