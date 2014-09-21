package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

public class NumberPickerDialog extends Preference {

    public NumberPickerDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NumberPickerDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberPickerDialog(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        // set up number picker
        final NumberPicker numberPicker = new NumberPicker(getContext());
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(60);
        // init builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getResources().getString(R.string.dialog_update_interval_title));
        // set layout
        RelativeLayout layout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(numberPicker, params);
        builder.setView(layout);
        // retrieve previous setting
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        final int previousValue = sp.getInt(TwitchActivity.PREF_UPDATE_INTERVAL, 5);
        numberPicker.setValue(previousValue);
        // save current value on ok
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sp.edit().putInt(TwitchActivity.PREF_UPDATE_INTERVAL, numberPicker.getValue()).commit();
                if (TwitchJsonGetter.checkRecentlyUpdated(getContext())) {
                    TwitchActivity.updateTwitchChannels(getContext(), null);
                }
            }
        });
        // reset to previous value on ok
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sp.edit().putInt(TwitchActivity.PREF_UPDATE_INTERVAL, previousValue).commit();
            }
        });
        builder.create();
        builder.show();
    }
}