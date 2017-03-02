package net.myacxy.squinch.helpers.tracking;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import java.util.Map;

import io.fabric.sdk.android.Fabric;

import static net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder.PROPERTY_USER_EMAIL;
import static net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder.PROPERTY_USER_ID;
import static net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder.PROPERTY_USER_NAME;

class CrashTracker implements ITracker {

    @Override
    public void initialize(Context context) {
        Fabric.with(context, new Crashlytics());
    }

    @Override
    public void exception(Throwable throwable) {
        Crashlytics.logException(throwable);
    }

    @Override
    public void user(Map<String, String> properties) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            switch (entry.getKey()) {
                case PROPERTY_USER_EMAIL:
                    Crashlytics.setUserEmail(entry.getValue());
                    break;
                case PROPERTY_USER_ID:
                    Crashlytics.setUserIdentifier(entry.getValue());
                    break;
                case PROPERTY_USER_NAME:
                    Crashlytics.setUserName(entry.getValue());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void log(Map<String, String> properties) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            Crashlytics.log(String.format("%s: %s", entry.getKey(), entry.getValue()));
        }
    }
}
