package net.myacxy.squinch;

import android.app.Application;
import android.app.job.JobScheduler;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;

import net.myacxy.retrotwitch.Configuration;
import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.squinch.helpers.tracking.Th;
import net.myacxy.squinch.helpers.tracking.Tracker;
import net.myacxy.squinch.helpers.tracking.TrackingHelper;

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

        TrackingHelper.initialize(this);

        RxRetroTwitch.getInstance()
                .configure(new Configuration.ConfigurationBuilder()
                        .setLogLevel(BuildConfig.DEBUG ? Level.BASIC : Level.NONE)
                        .setClientId(CLIENT_ID_TESTING)
                        .build()
                );

        Th.l(this, "onCreate");

        Fresco.initialize(getApplicationContext());

        SimpleViewModelLocator.initialize(getApplicationContext(), Tracker.DEBUG_LOGS);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
        jobScheduler.schedule(RetroTwitchJobService.newJob(this));
    } // onCreate
} // AppApplication
