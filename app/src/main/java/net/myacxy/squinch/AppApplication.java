package net.myacxy.squinch;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import net.myacxy.retrotwitch.Configuration;
import net.myacxy.retrotwitch.v5.RxRetroTwitch;

import io.fabric.sdk.android.Fabric;
import okhttp3.logging.HttpLoggingInterceptor;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        RxRetroTwitch.getInstance()
                .configure(new Configuration.ConfigurationBuilder()
                        .setLogLevel(HttpLoggingInterceptor.Level.HEADERS)
                        .setClientId("75gzbgqhk0tg6dhjbqtsphmy8sdayrr")
                        .build()
                );

        Logger.init()
                .methodCount(2)
                .logLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE)
                .methodOffset(0)
                .logTool(new AndroidLogTool());

        Fresco.initialize(getApplicationContext());

        SimpleViewModelLocator.initialize(getApplicationContext());
    } // onCreate
} // AppApplication
