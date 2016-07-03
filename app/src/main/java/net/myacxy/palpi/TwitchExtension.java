package net.myacxy.palpi;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class TwitchExtension extends DashClockExtension
{
    @Override
    public void onUpdateData(int reason) {
        // publish data
        publishUpdate(new ExtensionData()
                .visible(true)
                .icon(R.drawable.ic_glitch_white_24dp)
                .status("Status")
                .expandedTitle("Expanded Title")
                .expandedBody("Expanded Body")
                .clickIntent(null));
    } // onUpdateData

} // TwitchExtension
