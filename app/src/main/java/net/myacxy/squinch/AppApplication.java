package net.myacxy.squinch;

import android.app.Application;
import android.app.job.JobScheduler;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;

import net.myacxy.retrotwitch.Configuration;
import net.myacxy.retrotwitch.v5.RxRetroTwitch;

import io.fabric.sdk.android.Fabric;
import okhttp3.logging.HttpLoggingInterceptor.Level;

public class AppApplication extends Application {

    private static final String CLIENT_ID_TESTING = "75gzbgqhk0tg6dhjbqtsphmy8sdayrr";

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        Fabric.with(this, new Crashlytics());

        RxRetroTwitch.getInstance()
                .configure(new Configuration.ConfigurationBuilder()
                        .setLogLevel(BuildConfig.DEBUG ? Level.NONE : Level.NONE)
                        .setClientId(CLIENT_ID_TESTING)
                        .build()
                );

        Logger.init()
                .methodCount(2)
                .logLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE)
                .methodOffset(0)
                .logTool(new AndroidLogTool());

        Logger.d("App created");

        Fresco.initialize(getApplicationContext());

        SimpleViewModelLocator.initialize(getApplicationContext());

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
        jobScheduler.schedule(RetroTwitchJobService.newJob(this));
    } // onCreate
} // AppApplication
