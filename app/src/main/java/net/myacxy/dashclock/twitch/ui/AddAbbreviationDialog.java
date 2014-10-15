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
import android.widget.TextView;

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
        builder.setTitle("add abbreviation");

        View view = View.inflate(getActivity(), R.layout.dialog_abbr_add, null);
        builder.setView(view);

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getGames());
        mGameTextView = (AutoCompleteTextView) view.findViewById(R.id.dialog_abbr_add_game);
        mGameTextView.setAdapter(adapter);
        mAbbrTextView = (TextView) view.findViewById(R.id.dialog_abbr_add_abbr);

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TwitchDbHelper dbHelper = new TwitchDbHelper(getActivity());
                String gameName = mGameTextView.getText().toString();
                String abbr = mAbbrTextView.getText().toString();
                TwitchGame game = new TwitchGame(gameName, abbr);
                dbHelper.insertOrReplaceGameEntry(game);
            }
        });
        return builder.create();
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
