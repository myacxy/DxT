package net.myacxy.squinch.helpers.tracking;

import java.util.ArrayList;
import java.util.List;

public class Tracker {

    public static ITracker CRASHES = new CrashTracker();
    public static ITracker LOGS = new LogTracker();
    public static DebugLogTracker DEBUG_LOGS = new DebugLogTracker();

    private static List<ITracker> ALL_TRACKERS = new ArrayList<ITracker>() {{
        add(CRASHES);
        add(LOGS);
        add(DEBUG_LOGS);
    }};

    private Tracker() {
        throw new IllegalAccessError();
    }

    static List<ITracker> getAll() {
        return ALL_TRACKERS;
    }
}
