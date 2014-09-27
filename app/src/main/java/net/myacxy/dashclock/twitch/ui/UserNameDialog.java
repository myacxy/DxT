package net.myacxy.dashclock.twitch.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import net.myacxy.dashclock.twitch.TwitchExtension;

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
                    new HashSet<String>()).apply();
            // remove whitespaces
            String userName = getEditText().getText().toString();
            userName = userName.replaceAll("\\s+", "");
            editor.putString(TwitchExtension.PREF_USER_NAME, userName).apply();
            // update channels for new user name
            TwitchExtension.updateTwitchChannels(getContext(), null);
        }
        super.onClick(dialog, which);
    }
}
