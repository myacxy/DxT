package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

public class TwitchSettingsActivity extends PreferenceActivity
{
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_twitch);
        // set up user name preference
        final Preference prefUserName = findPreference("pref_user_name");
        // change the view's summary text to its value
        bindPreferenceSummaryToValue(prefUserName);

        // set up update interval preference
        bindPreferenceSummaryToValue(findPreference("pref_update_interval"));
        // TODO: refactor
        findPreference("pref_update_interval").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                RelativeLayout linearLayout = new RelativeLayout(preference.getContext());
                final NumberPicker aNumberPicker = new NumberPicker(preference.getContext());
                aNumberPicker.setMaxValue(50);
                aNumberPicker.setMinValue(1);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
                RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                linearLayout.setLayoutParams(params);
                linearLayout.addView(aNumberPicker,numPicerParams);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(preference.getContext());
                alertDialogBuilder.setTitle("Select the number");
                alertDialogBuilder.setView(linearLayout);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Log.e("", "New Quantity Value : " + aNumberPicker.getValue());

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }
        });
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     *
     * @see <a href="http://goo.gl/6UXtX4">Lars Vogel</a>
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference
                        .setSummary(index >= 0 ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone
                                .getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     * @see <a href="http://goo.gl/6UXtX4">Lars Vogel</a>
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference
                .setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager.getDefaultSharedPreferences(
                        preference.getContext()).getString(preference.getKey(), ""));
    }
}
