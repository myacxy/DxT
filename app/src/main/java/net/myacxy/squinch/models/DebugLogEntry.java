package net.myacxy.squinch.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.StringDef;

public class DebugLogEntry extends BaseObservable {

    public static final String TYPE_LOG = "Log";
    public static final String TYPE_EXCEPTION = "Exception";
    public static final String TYPE_USER = "User";

    private long time;
    private String type;
    private String message;

    public DebugLogEntry(long time, String type, String message) {
        this.time = time;
        this.type = type;
        this.message = message;
    }

    @Bindable
    public long getTime() {
        return time;
    }

    @Bindable
    public String getType() {
        return type;
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    @StringDef({TYPE_LOG, TYPE_EXCEPTION, TYPE_USER})
    public @interface DebugLogEntryType {
    }
}
