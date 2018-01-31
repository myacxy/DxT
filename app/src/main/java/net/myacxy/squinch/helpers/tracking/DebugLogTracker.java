package net.myacxy.squinch.helpers.tracking;

import android.content.Context;
import android.content.SharedPreferences;

import net.myacxy.squinch.settings.debuglog.DebugLogEntry;
import net.myacxy.squinch.utils.JsonUtil;
import net.myacxy.squinch.utils.StringUtil;

import java.util.Map;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static net.myacxy.squinch.settings.debuglog.DebugLogEntry.TYPE_EXCEPTION;
import static net.myacxy.squinch.settings.debuglog.DebugLogEntry.TYPE_LOG;
import static net.myacxy.squinch.settings.debuglog.DebugLogEntry.TYPE_USER;

public class DebugLogTracker implements ITracker {

    private SharedPreferences sp;
    private OnDebugLogChangedListener listener;

    @Override
    public void initialize(Context context) {
        sp = context.getSharedPreferences("log", Context.MODE_PRIVATE);
    }

    @Override
    public void exception(Throwable throwable) {
        Single.just(throwable.toString())
                .map(message -> {
                    DebugLogEntry debugLogEntry = new DebugLogEntry(System.currentTimeMillis(), TYPE_EXCEPTION, message);
                    sp.edit().putString(TYPE_EXCEPTION, JsonUtil.toJson(debugLogEntry)).apply();
                    return debugLogEntry;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<DebugLogEntry>() {
                    @Override
                    public void onSuccess(DebugLogEntry debugLogEntry) {
                        if (listener != null) {
                            listener.onEntryAdded(debugLogEntry);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Th.ex(throwable);
                    }
                });
    }

    @Override
    public void user(Map<String, String> properties) {

        Single.just(StringUtil.mapToString(properties))
                .map(message -> {
                    DebugLogEntry debugLogEntry = new DebugLogEntry(System.currentTimeMillis(), TYPE_USER, message);
                    sp.edit().putString(TYPE_USER, JsonUtil.toJson(debugLogEntry)).apply();
                    return debugLogEntry;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<DebugLogEntry>() {
                    @Override
                    public void onSuccess(DebugLogEntry debugLogEntry) {
                        if (listener != null) {
                            listener.onEntryAdded(debugLogEntry);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Th.ex(throwable);
                    }
                });
    }

    @Override
    public void log(Map<String, String> properties) {

        Single.just(StringUtil.mapToString(properties))
                .map(message -> {
                    DebugLogEntry debugLogEntry = new DebugLogEntry(System.currentTimeMillis(), TYPE_LOG, message);
                    sp.edit().putString(TYPE_LOG, JsonUtil.toJson(debugLogEntry)).apply();
                    return debugLogEntry;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<DebugLogEntry>() {
                    @Override
                    public void onSuccess(DebugLogEntry debugLogEntry) {
                        if (listener != null) {
                            listener.onEntryAdded(debugLogEntry);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Th.ex(throwable);
                    }
                });
    }

    public void setOnDebugLogChangedListener(OnDebugLogChangedListener listener) {
        this.listener = listener;
    }

    public interface OnDebugLogChangedListener {
        void onEntryAdded(DebugLogEntry debugLogEntry);
    }
}
