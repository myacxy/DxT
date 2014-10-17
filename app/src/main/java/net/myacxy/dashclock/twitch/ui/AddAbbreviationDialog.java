package net.myacxy.dashclock.twitch.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import net.myacxy.dashclock.twitch.database.GameQuery;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = getString(R.string.dialog_abbr_add_title);
        builder.setTitle(title);

        View view = View.inflate(getActivity(), R.layout.dialog_abbr_add, null);
        builder.setView(view);

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getGames());
        mGameTextView = (AutoCompleteTextView) view.findViewById(R.id.dialog_abbr_add_game);
        mGameTextView.setAdapter(adapter);
        mAbbrTextView = (TextView) view.findViewById(R.id.dialog_abbr_add_abbr);

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, null);

        final AlertDialog dialog = builder.create();
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

                Button ok = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String gameName = mGameTextView.getText().toString().trim();
                        String abbr = mAbbrTextView.getText().toString().trim();

                        if(gameName.length() == 0) {
                            Toast.makeText(getActivity(),
                                    getString(R.string.dialog_abbr_add_hint_game_missing),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if(abbr.length() == 0) {
                            Toast.makeText(getActivity(),
                                    getString(R.string.dialog_abbr_add_hint_abbr_missing),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if(gameName.length() > 0 && abbr.length() > 0) {
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

    private ArrayList<String> getGames() {
        ArrayList<String> games = new ArrayList<>();
        TwitchDbHelper dbHelper = new TwitchDbHelper(getActivity());
        Cursor cursor = dbHelper.getGamesCursor(false);
        while (cursor.moveToNext()) games.add(cursor.getString(GameQuery.name));
        cursor.close();
        dbHelper.close();
        return games;
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
