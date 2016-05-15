package net.myacxy.ditch;

import android.app.Application;

import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import net.myacxy.retrotwitch.RetroTwitch;

import okhttp3.logging.HttpLoggingInterceptor;

public class AppApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        RetroTwitch.getInstance()
                .configure()
                .setLogLevel(HttpLoggingInterceptor.Level.BODY)
                .apply();

        Logger.init()                       // default PRETTYLOGGER or use just init()
            .methodCount(2)                 // default 2
            .logLevel(LogLevel.FULL)        // default LogLevel.FULL
            .methodOffset(0)                // default 0
            .logTool(new AndroidLogTool()); // custom log tool, optional
    } // onCreate
} // AppApplication
