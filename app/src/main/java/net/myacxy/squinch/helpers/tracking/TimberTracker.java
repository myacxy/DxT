package net.myacxy.squinch.helpers.tracking;

import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import net.myacxy.squinch.utils.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import retrofit2.Response;
import timber.log.Timber;
import timber.log.Timber.Tree;

class TimberTracker implements Tracker {

    public TimberTracker(Tree... trees) {
        Timber.plant(trees);
    }

    @Override
    public void exception(Throwable throwable) {
        Timber.e(throwable);
    }

    @Override
    public void user(@NotNull Map<String, Boolean> booleans, @NotNull Map<String, Integer> ints, @NotNull Map<String, String> strings) {
        log(booleans, ints, strings);
    }

    @Override
    public void log(@NotNull Map<String, Boolean> booleans, @NotNull Map<String, Integer> ints, @NotNull Map<String, String> strings) {
        Timber.d(StringUtils.booleanMapToString(booleans));
        Timber.d(StringUtils.integerMapToString(ints));
        Timber.d(StringUtils.stringMapToString(strings));
    }

    public static class DebugTree extends Timber.DebugTree {
        @Override
        protected String createStackElementTag(@NonNull StackTraceElement element) {
            return String.format("[%s:%s:%s]",
                    super.createStackElementTag(element),
                    element.getMethodName(),
                    element.getLineNumber()
            );
        }
    }

    public static class CrashlyticsTree extends Tree {

        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            switch (priority) {
                case Log.VERBOSE:
                case Log.DEBUG:
                case Log.INFO:
                    return;
            }
            Crashlytics.log(priority, tag, message);

            if (t != null) {
                if (t instanceof HttpException) {
                    Response response = ((HttpException) t).response();
                    Crashlytics.setInt("code", response.code());
                    Crashlytics.setString("message", response.message());
                    try {
                        Crashlytics.setString("errorBody", response.errorBody().string());
                    } catch (Exception e) {
                        // do nothing
                    }
                }
                Crashlytics.logException(t);
            }
        }
    }
}
