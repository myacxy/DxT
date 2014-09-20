package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import java.util.HashSet;

/**
 * Simple EditTextPreference that reacts to clicking the OK button.
 *
 */
public class UserNamePreference extends EditTextPreference
{
    public UserNamePreference(Context context) {
        super(context);
    }

    public UserNamePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserNamePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sp.edit();
            editor.putStringSet(TwitchActivity.PREF_SELECTED_FOLLOWED_CHANNELS,
                    new HashSet<String>());
            String userName = getEditText().getText().toString();
            userName = userName.replaceAll("\\s+", "");
            editor.putString(TwitchActivity.PREF_USER_NAME, userName);
            editor.commit();
            TwitchActivity.updateTwitchChannels(getContext(), null);
        }
        super.onClick(dialog, which);
    }

}
