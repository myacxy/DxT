package net.myacxy.squinch;

import android.content.Intent;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.views.activities.SettingsActivity;

public class TwitchExtension extends DashClockExtension
{
    private DataHelper dataHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dataHelper = new DataHelper(getApplicationContext());
    }

    @Override
    public void onUpdateData(int reason) {
        // publish data
        publishUpdate(new ExtensionData()
                .visible(true)
                .icon(R.drawable.ic_glitch_white_24dp)
                .status("Status")
                .expandedTitle("Expanded Title")
                .expandedBody("Expanded Body")
                .clickIntent(new Intent(this, SettingsActivity.class)));
    } // onUpdateData

} // TwitchExtension
