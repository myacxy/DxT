package net.myacxy.squinch.settings.debuglog;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

public class DebugLogEntry extends BaseObservable implements Comparable<DebugLogEntry> {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DebugLogEntry that = (DebugLogEntry) o;

        if (time != that.time) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (time ^ (time >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DebugLogEntry{" +
                "time=" + time +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull DebugLogEntry other) {
        return Long.compare(time, other.time);
    }

    @StringDef({TYPE_LOG, TYPE_EXCEPTION, TYPE_USER})
    public @interface DebugLogEntryType {
    }
}
