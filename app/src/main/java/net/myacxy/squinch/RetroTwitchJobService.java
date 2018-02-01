package net.myacxy.squinch;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

import com.google.android.apps.dashclock.api.DashClockExtension;

import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.retrotwitch.v5.api.streams.Stream;
import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.models.events.DashclockUpdateEvent;
import net.myacxy.squinch.utils.RetroTwitchUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class RetroTwitchJobService extends JobService {

    private static int JOB_ID = 0;

    @Inject
    RxRetroTwitch rxRetroTwitch;

    private Disposable disposable;

    public static JobInfo newJob(Context context) {
        Timber.d("newJob=%d", JOB_ID);
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
        AndroidInjection.inject(this);
        super.onCreate();
        Timber.d("onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Timber.d("onStartJob=%d", params.getJobId());

        DataHelper dataHelper = new DataHelper(getApplicationContext());
        SimpleUser user = dataHelper.getUser();

        Timber.d("user=%s", String.valueOf(user));
        if (user == null) {
            return false;
        }

        disposable = RetroTwitchUtil.getAllUserFollows(rxRetroTwitch, user.getId(), progress -> Timber.d("userFollows.progress=%d", progress.size()))
                .subscribeOn(Schedulers.io())
                .doOnSuccess(dataHelper::setUserFollows)
                .doOnError(Timber::e)
                .flatMap(userFollows -> RetroTwitchUtil.getAllLiveStreams(rxRetroTwitch, userFollows, progress -> Timber.d("streams.progress=%d", progress.size())))
                .subscribeWith(new DisposableSingleObserver<List<Stream>>() {
                    @Override
                    public void onSuccess(List<Stream> streams) {
                        Timber.d("streams=%d", streams.size());
                        dataHelper.setLiveStreams(streams);
                        EventBus.getDefault().post(new DashclockUpdateEvent(DashClockExtension.UPDATE_REASON_SETTINGS_CHANGED));
                        jobFinished(params, false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.e(throwable);
                        jobFinished(params, true);
                    }
                });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Timber.d("onStopJob=%d", params.getJobId());
        if (disposable != null) {
            disposable.dispose();
        }
        return true;
    }
}
