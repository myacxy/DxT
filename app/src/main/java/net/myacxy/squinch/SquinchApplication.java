package net.myacxy.squinch;

import android.app.job.JobScheduler;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;

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
    } // onCreate

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerSquinchApplicationComponent.builder().create(this);
    }
} // SquinchApplication
