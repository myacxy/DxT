package net.myacxy.squinch;

import android.app.job.JobScheduler;
import android.content.Context;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;

import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.squinch.di.DaggerSquinchComponent;
import net.myacxy.squinch.helpers.tracking.TrackingHelper;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class SquinchApplication extends DaggerApplication {

    @Inject
    TrackingHelper th;
    @Inject
    RxRetroTwitch rxRetroTwitch;

    private Crashlytics crashlytics;

    @Override
    public void onCreate() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        crashlytics = new Crashlytics();
        Fabric.with(getApplicationContext(), crashlytics);
        super.onCreate();

        LeakCanary.install(this);

        Timber.d("onCreate");

        Fresco.initialize(getApplicationContext());

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
        return DaggerSquinchComponent.builder().crashlytics(crashlytics).create(this);
    }
} // SquinchApplication
