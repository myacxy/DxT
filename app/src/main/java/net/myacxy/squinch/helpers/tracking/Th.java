package net.myacxy.squinch.helpers.tracking;

import android.support.annotation.NonNull;

import net.myacxy.squinch.helpers.tracking.builder.ExceptionLogBuilder;
import net.myacxy.squinch.helpers.tracking.builder.LogBuilder;

public class Th {

    private Th() {
        throw new IllegalAccessError();
    }

    public static void ex(Throwable throwable) {
        new ExceptionLogBuilder(TrackingHelper.getInstance())
                .withTrackers(Tracker.LOGGER, Tracker.CRASHLYTICS)
                .withThrowable(throwable)
                .post();
    }

    public static void l(String tag, String msg, Object... objects) {
        new LogBuilder(TrackingHelper.getInstance())
                .withTrackers(Tracker.LOGGER, Tracker.CRASHLYTICS)
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
