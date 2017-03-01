package net.myacxy.squinch.helpers.tracking;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import net.myacxy.squinch.BuildConfig;

import java.util.Iterator;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

import static net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder.PROPERTY_USER_EMAIL;
import static net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder.PROPERTY_USER_ID;
import static net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder.PROPERTY_USER_NAME;

public enum Tracker {
    CRASHLYTICS {
        @Override
        protected void initialize(Context context) {
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
    },
    LOGGER {
        @Override
        protected void initialize(Context context) {
            Logger.init()
                    .methodCount(0)
                    .logLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE)
                    .methodOffset(0)
                    .logTool(new AndroidLogTool());
        }

        @Override
        public void exception(Throwable throwable) {
            Logger.e(throwable, null);
        }

        @Override
        public void user(Map<String, String> properties) {
            log(properties);
        }

        @Override
        public void log(Map<String, String> properties) {
            StringBuilder sb = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = properties.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                sb.append(String.format("%s: %s", entry.getKey(), entry.getValue()));
                if (iterator.hasNext()) {
                    sb.append("\n");
                }
            }
            Logger.d(sb.toString());
        }
    };


    protected abstract void initialize(Context context);

    public abstract void exception(Throwable throwable);

    public abstract void user(Map<String, String> properties);

    public abstract void log(Map<String, String> properties);
}
