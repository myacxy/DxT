package net.myacxy.squinch;

import android.content.Intent;
import android.widget.Toast;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import net.myacxy.retrotwitch.v5.api.streams.Stream;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.utils.RetroTwitchUtil;
import net.myacxy.squinch.views.activities.SettingsActivity;

import java.util.List;
import java.util.Locale;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TwitchExtension extends DashClockExtension
{
    private DataHelper dataHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dataHelper = new DataHelper(getApplicationContext());
    }

    @Override
    public void onUpdateData(int reason) {

        List<UserFollow> userFollows = dataHelper.getUserFollows();

        if (userFollows.size() > 0) {
            RetroTwitchUtil.getAllLiveStreams(dataHelper.getUserFollows(),
                    progress -> {
                        if (!progress.isOnComplete()) {
                            System.out.println(progress.getValue());
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<List<Stream>>() {
                        @Override
                        public void onSubscribe(Disposable disposable) {

                        }

                        @Override
                        public void onSuccess(List<Stream> streams) {
                            Toast.makeText(TwitchExtension.this, String.format(Locale.getDefault(), "%d online", streams.size()), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }
                    });
        }

        // publish data
        publishUpdate(new ExtensionData()
                .visible(true)
                .icon(R.drawable.ic_glitch_white_24dp)
                .status("Status")
                .expandedTitle("Expanded Title")
                .expandedBody("Expanded Body")
                .clickIntent(new Intent(this, SettingsActivity.class)));
    } // onUpdateData

} // TwitchExtension
