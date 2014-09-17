package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Set;


public class FollowingSelectionPreference extends MultiSelectListPreference
{
    private Set<String> mSelectedFollowedChannels;
    private ArrayList<TwitchChannel> allFollowedChannels;
    private Context context;

    public FollowingSelectionPreference(Context context) {
        this(context, null);
    }

    public FollowingSelectionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        float lastUpdate = sp.getFloat(TwitchActivity.PREF_LAST_UPDATE, 0);
        float difference = (System.currentTimeMillis() - lastUpdate) / 60000f;
        int updateInterval = sp.getInt(TwitchActivity.PREF_UPDATE_INTERVAL, 0);
        if(difference >= updateInterval || difference == 0)
        {
            TwitchActivity.updateTwitchChannels(context, new Callback() {
                @Override
                public void run(Object object) {
                    allFollowedChannels = (ArrayList<TwitchChannel>) object;
                }
            });
        }
    }

    void buildDialog()
    {

    }
}
