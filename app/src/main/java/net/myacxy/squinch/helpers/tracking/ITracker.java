package net.myacxy.squinch.helpers.tracking;

import android.content.Context;

import java.util.Map;

public interface ITracker {
    void initialize(Context context);

    void exception(Throwable throwable);

    void user(Map<String, String> properties);

    void log(Map<String, String> properties);
}
