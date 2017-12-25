package net.myacxy.squinch.helpers.tracking;

public class Tracker {

    public static ITracker CRASHES = new CrashTracker();
    public static ITracker LOGS = new LogTracker();
    public static DebugLogTracker DEBUG_LOGS = new DebugLogTracker();

    private static ITracker[] VALUES = new ITracker[]{
            CRASHES,
            LOGS,
            DEBUG_LOGS
    };

    private Tracker() {
        throw new IllegalAccessError();
    }

    public static ITracker[] values() {
        return VALUES;
    }
}
