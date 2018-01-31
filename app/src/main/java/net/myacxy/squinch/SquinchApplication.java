package net.myacxy.squinch;

import android.app.job.JobScheduler;
import android.content.Context;
import android.os.StrictMode;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;

import net.myacxy.squinch.base.SimpleViewModelLocator;
import net.myacxy.squinch.di.DaggerSquinchComponent;
import net.myacxy.squinch.helpers.tracking.Th;
import net.myacxy.squinch.helpers.tracking.Tracker;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class SquinchApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        super.onCreate();

        LeakCanary.install(this);

        Th.l(this, "onCreate");

        Fresco.initialize(getApplicationContext());

        SimpleViewModelLocator.initialize(getApplicationContext(), Tracker.DEBUG_LOGS);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
        jobScheduler.schedule(RetroTwitchJobService.newJob(this));

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                            .detectCustomSlowCalls()
                            .detectNetwork()
                            .detectDiskReads()
                            .detectDiskWrites()
                            .penaltyLog()
                            .build());
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog()
                            .build());
        }
    } // onCreate

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerSquinchComponent.builder().create(this);
    }
} // SquinchApplication
