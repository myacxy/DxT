package net.myacxy.squinch.helpers.tracking;

import android.content.SharedPreferences;

import net.myacxy.squinch.settings.debuglog.DebugLogEntry;
import net.myacxy.squinch.utils.JsonUtil;
import net.myacxy.squinch.utils.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static net.myacxy.squinch.settings.debuglog.DebugLogEntry.TYPE_EXCEPTION;
import static net.myacxy.squinch.settings.debuglog.DebugLogEntry.TYPE_LOG;
import static net.myacxy.squinch.settings.debuglog.DebugLogEntry.TYPE_USER;

public class SharedPreferencesTracker implements Tracker {

    private SharedPreferences sharedPreferences;

    public SharedPreferencesTracker(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void exception(Throwable throwable) {
        Single.just(throwable.toString())
                .map(message -> new DebugLogEntry(System.currentTimeMillis(), TYPE_EXCEPTION, message))
                .map(JsonUtil::toJson)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String json) {
                        Set<String> exceptionSet = new HashSet<>(sharedPreferences.getStringSet(TYPE_EXCEPTION, Collections.emptySet()));
                        exceptionSet.add(json);
                        sharedPreferences.edit().putStringSet(TYPE_EXCEPTION, exceptionSet).apply();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.e(throwable);
                    }
                });
    }

    @Override
    public void user(@NotNull Map<String, Boolean> booleans, @NotNull Map<String, Integer> ints, @NotNull Map<String, String> strings) {
        Observable.just(StringUtils.booleanMapToString(booleans), StringUtils.integerMapToString(ints), StringUtils.stringMapToString(strings))
                .map(message -> new DebugLogEntry(System.currentTimeMillis(), TYPE_USER, message))
                .map(JsonUtil::toJson)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<String>>() {
                    @Override
                    public void onSuccess(List<String> jsonLogs) {
                        Set<String> userSet = new HashSet<>(sharedPreferences.getStringSet(TYPE_USER, Collections.emptySet()));
                        userSet.addAll(jsonLogs);
                        sharedPreferences.edit().putStringSet(TYPE_USER, userSet).apply();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.e(throwable);
                    }
                });
    }

    @Override
    public void log(@NotNull Map<String, Boolean> booleans, @NotNull Map<String, Integer> ints, @NotNull Map<String, String> strings) {
        Observable.just(StringUtils.booleanMapToString(booleans), StringUtils.integerMapToString(ints), StringUtils.stringMapToString(strings))
                .map(message -> new DebugLogEntry(System.currentTimeMillis(), TYPE_LOG, message))
                .map(JsonUtil::toJson)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<String>>() {
                    @Override
                    public void onSuccess(List<String> jsonLogs) {
                        Set<String> logSet = new HashSet<>(sharedPreferences.getStringSet(TYPE_LOG, Collections.emptySet()));
                        logSet.addAll(jsonLogs);
                        sharedPreferences.edit().putStringSet(TYPE_LOG, logSet).apply();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.e(throwable);
                    }
                });
    }

    public static class DeviceLogTree extends Timber.Tree {

        private final Tracker tracker;

        public DeviceLogTree(Tracker tracker) {
            this.tracker = tracker;
        }

        @Override
        protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
            Map<String, Integer> ints = Collections.singletonMap("priority", priority);
            Map<String, Boolean> booleans = Collections.emptyMap();
            Map<String, String> strings = new HashMap<String, String>(2) {{
                put("tag", tag);
                put("message", message);
            }};
            tracker.log(booleans, ints, strings);
            if (t != null) {
                tracker.exception(t);
            }
        }
    }
}
