package net.myacxy.squinch.helpers.tracking;

import com.crashlytics.android.Crashlytics;

import net.myacxy.squinch.utils.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder.PROPERTY_USER_EMAIL;
import static net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder.PROPERTY_USER_ID;
import static net.myacxy.squinch.helpers.tracking.builder.UserLogBuilder.PROPERTY_USER_NAME;

class CrashlyticsTracker implements Tracker {

    private final Crashlytics crashlytics;

    public CrashlyticsTracker(Crashlytics crashlytics) {
        this.crashlytics = crashlytics;
    }

    @Override
    public void exception(Throwable throwable) {
        crashlytics.core.logException(throwable);
    }

    @Override
    public void user(@NotNull Map<String, Boolean> booleans, @NotNull Map<String, Integer> ints, @NotNull Map<String, String> strings) {
        // booleans
        for (Map.Entry<String, Boolean> entry : booleans.entrySet()) {
            crashlytics.core.setBool(entry.getKey(), entry.getValue());
        }

        // ints
        for (Map.Entry<String, Integer> entry : ints.entrySet()) {
            crashlytics.core.setInt(entry.getKey(), entry.getValue());
        }

        // strings
        for (Map.Entry<String, String> entry : strings.entrySet()) {
            switch (entry.getKey()) {
                case PROPERTY_USER_EMAIL:
                    crashlytics.core.setUserEmail(entry.getValue());
                    break;
                case PROPERTY_USER_ID:
                    crashlytics.core.setUserIdentifier(entry.getValue());
                    break;
                case PROPERTY_USER_NAME:
                    crashlytics.core.setUserName(entry.getValue());
                    break;
                default:
                    crashlytics.core.setString(entry.getKey(), entry.getValue());
                    break;
            }
        }
    }

    @Override
    public void log(@NotNull Map<String, Boolean> booleans, @NotNull Map<String, Integer> ints, @NotNull Map<String, String> strings) {
        crashlytics.core.log(StringUtils.booleanMapToString(booleans));
        crashlytics.core.log(StringUtils.integerMapToString(ints));
        crashlytics.core.log(StringUtils.stringMapToString(strings));
    }
}
