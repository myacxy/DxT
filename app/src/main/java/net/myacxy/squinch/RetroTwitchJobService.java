package net.myacxy.squinch;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

import com.google.android.apps.dashclock.api.DashClockExtension;

import net.myacxy.retrotwitch.v5.api.streams.Stream;
import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.helpers.tracking.Th;
import net.myacxy.squinch.models.events.DashclockUpdateEvent;
import net.myacxy.squinch.utils.RetroTwitchUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class RetroTwitchJobService extends JobService {

    private static int JOB_ID = 0;

    private Disposable disposable;

    public static JobInfo newJob(Context context) {
        Th.l(RetroTwitchJobService.class, "newJob=%d", JOB_ID);
        return new JobInfo.Builder(JOB_ID++, new ComponentName(context, RetroTwitchJobService.class))
                .setPeriodic(TimeUnit.MINUTES.toMillis(60))
//                .setMinimumLatency(TimeUnit.MINUTES.toMillis(45))
//                .setOverrideDeadline(TimeUnit.MINUTES.toMillis(120))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setBackoffCriteria(TimeUnit.SECONDS.toMillis(30), JobInfo.BACKOFF_POLICY_EXPONENTIAL)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
//                .setPersisted(true) // Manifest.permission.RECEIVE_BOOT_COMPLETED
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Th.l(this, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Th.l(this, "onDestroy");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Th.l(this, "onStartJob=%d", params.getJobId());

        DataHelper dataHelper = new DataHelper(getApplicationContext());
        SimpleUser user = dataHelper.getUser();

        if (user == null) {
            Th.l(this, "user=%s", "null");
            return false;
        }

        Th.l(this, "user=%s", user);

        RetroTwitchUtil.getAllUserFollows(user.getId(), progress -> Th.l(this, "userFollows.progress=%d", progress.size()))
                .subscribeOn(Schedulers.io())
                .doOnSuccess(dataHelper::setUserFollows)
                .doOnError(Th::ex)
                .flatMap(userFollows -> RetroTwitchUtil.getAllLiveStreams(userFollows, progress -> Th.l(this, "streams.progress=%d", progress.size())))
                .subscribe(new SingleObserver<List<Stream>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        RetroTwitchJobService.this.disposable = disposable;
                    }

                    @Override
                    public void onSuccess(List<Stream> streams) {
                        Th.l(RetroTwitchJobService.this, "streams=%d", streams.size());
                        dataHelper.setLiveStreams(streams);
                        EventBus.getDefault().post(new DashclockUpdateEvent(DashClockExtension.UPDATE_REASON_SETTINGS_CHANGED));
                        jobFinished(params, false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Th.ex(throwable);
                        jobFinished(params, true);
                    }
                });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Th.l(this, "onStopJob=%d", params.getJobId());
        if (disposable != null) {
            disposable.dispose();
        }
        return true;
    }
}
