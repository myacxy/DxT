package net.myacxy.ditch;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class TwitchExtension extends DashClockExtension
{
    @Override
    public void onUpdateData(int reason) {
        // publish data
        publishUpdate(new ExtensionData()
                .visible(true)
                .icon(R.drawable.twitch_white)
                .status("Status")
                .expandedTitle("Expanded Title")
                .expandedBody("Expanded Body")
                .clickIntent(null));
    } // onUpdateData

} // TwitchExtension