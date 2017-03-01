package net.myacxy.squinch;

import android.content.Intent;
import android.support.annotation.IntDef;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import net.myacxy.retrotwitch.v5.api.streams.Stream;
import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.helpers.tracking.Th;
import net.myacxy.squinch.models.events.DashclockUpdateEvent;
import net.myacxy.squinch.views.activities.SettingsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TwitchExtension extends DashClockExtension {
    private DataHelper dataHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dataHelper = new DataHelper(getApplicationContext());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onUpdateData(@UpdateReason int reason) {

        String status;
        String title;
        StringBuilder body = new StringBuilder();

        SimpleUser user = dataHelper.getUser();
        if (user != null) {
            List<UserFollow> follows = dataHelper.getUserFollows();
            List<Long> channels = dataHelper.getDeselectedChannelIds();
            List<Stream> streams = dataHelper.getLiveStreams();

            int allFollows = follows.size();
            int filteredFollows = follows.size();
            for (UserFollow follow : follows) {
                if (channels.contains(follow.getChannel().getId())) {
                    filteredFollows -= 1;
                }
            }
            List<Stream> filteredStreams = new ArrayList<>(streams.size());
            for (Stream stream : streams) {
                if (!channels.contains(stream.getId())) {
                    filteredStreams.add(stream);
                }
            }

            for (int i = 0; i < filteredStreams.size(); i++) {
                Stream stream = filteredStreams.get(i);
                String line = String.format("%s (%s): %s", stream.getChannel().getDisplayName(), stream.getGame(), stream.getChannel().getStatus());
                body.append(line);
                body.append(i == filteredStreams.size() - 1 ? "\n" : "");
            }

            status = String.valueOf(filteredStreams.size());
            title = String.format(Locale.getDefault(), "F%d/%d | S%d/%d", filteredFollows, allFollows, filteredStreams.size(), streams.size());
        } else {
            status = "―";
            title = "User is null";
        }

        // publish data
        publishUpdate(new ExtensionData()
                .visible(true)
                .icon(R.drawable.ic_glitch_white_24dp)
                .status(status)
                .expandedTitle(title)
                .expandedBody(body.toString())
                .clickIntent(new Intent(this, SettingsActivity.class))
        );
    } // onUpdateData

    @Subscribe
    public void onEvent(DashclockUpdateEvent event) {
        Th.l(this, "updateEvent=%d", event.getUpdateReason());
        onUpdateData(event.getUpdateReason());
    }

    @IntDef({
            UPDATE_REASON_UNKNOWN,
            UPDATE_REASON_INITIAL,
            UPDATE_REASON_PERIODIC,
            UPDATE_REASON_SETTINGS_CHANGED,
            UPDATE_REASON_CONTENT_CHANGED,
            UPDATE_REASON_SCREEN_ON,
            UPDATE_REASON_MANUAL
    })
    public @interface UpdateReason {
    }

} // TwitchExtension
