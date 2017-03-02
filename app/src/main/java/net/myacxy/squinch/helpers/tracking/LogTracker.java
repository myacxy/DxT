package net.myacxy.squinch.helpers.tracking;

import android.content.Context;

import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import net.myacxy.squinch.BuildConfig;
import net.myacxy.squinch.utils.StringUtil;

import java.util.Map;

class LogTracker implements ITracker {

    @Override
    public void initialize(Context context) {
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
        Logger.d(StringUtil.mapToString(properties));
    }
}
