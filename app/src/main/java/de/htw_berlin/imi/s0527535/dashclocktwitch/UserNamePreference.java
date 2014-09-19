package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import java.util.HashSet;

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
            TwitchActivity.updateTwitchChannels(getContext(), null);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            sp.edit().putStringSet(TwitchActivity.PREF_SELECTED_FOLLOWED_CHANNELS,
                    new HashSet<String>()).commit();
        }
        super.onClick(dialog, which);
    }
}
