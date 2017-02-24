package net.myacxy.squinch.models.events;

import android.support.annotation.Keep;

import net.myacxy.squinch.TwitchExtension;

@Keep
public class DashclockUpdateEvent {

    private final int updateReason;

    public DashclockUpdateEvent(@TwitchExtension.UpdateReason int updateReason) {
        this.updateReason = updateReason;
    }

    @TwitchExtension.UpdateReason
    public int getUpdateReason() {
        return updateReason;
    }
}
