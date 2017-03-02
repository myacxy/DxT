package net.myacxy.squinch.helpers.tracking;

import android.support.annotation.NonNull;

public class Th {

    private Th() {
        throw new IllegalAccessError();
    }

    public static void ex(Throwable throwable) {
        TrackingHelper.getInstance()
                .exception()
                .withTrackers(Tracker.LOGS, Tracker.CRASHES, Tracker.DEBUG_LOGS)
                .withThrowable(throwable)
                .post();
    }

    public static void l(String tag, String msg, Object... objects) {
        TrackingHelper.getInstance()
                .log()
                .withTrackers(Tracker.LOGS, Tracker.CRASHES, Tracker.DEBUG_LOGS)
                .addProperty(tag, String.format(msg, objects))
                .post();
    }

    public static void l(Class<?> clazz, String msg, Object... objects) {
        Th.l(clazz.getSimpleName(), msg, objects);
    }

    public static void l(@NonNull Object object, String msg, Object... objects) {
        Th.l(object.getClass(), msg, objects);
    }
}
