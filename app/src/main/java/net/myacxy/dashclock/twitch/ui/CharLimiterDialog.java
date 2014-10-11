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

import java.util.HashSet;
import java.util.Set;

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

    private String getLongestString(Set<String> strings) {
        String result = "";
        for(String string : strings)
            if(string.length() > result.length())
                result = string;
        return result;
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
        Set<String> allChannels = sp.getStringSet(TwitchExtension.PREF_EXPANDED_BODY, new HashSet<String>());
        final String longestBody = getLongestString(allChannels);
        final SpannableString text = new SpannableString(longestBody);
        final int color = getContext().getResources().getColor(android.R.color.darker_gray);
        final int color2 = getContext().getResources().getColor(android.R.color.black);
        final int previousValue = sp.getInt(TwitchExtension.PREF_CHAR_LIMIT, 100);
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
                text.setSpan(new ForegroundColorSpan(color2), 0,
                        newVal - 1 > maxLength ? maxLength : newVal - 1, 0);
                text.setSpan(new ForegroundColorSpan(color),
                        newVal > maxLength ? maxLength : newVal, maxLength, 0);
                textView.setText(text);
            }
        });

        // save current value on ok
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sp.edit().putInt(TwitchExtension.PREF_CHAR_LIMIT, numberPicker.getValue()).apply();

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
