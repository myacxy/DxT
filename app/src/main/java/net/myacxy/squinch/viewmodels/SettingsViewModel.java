package net.myacxy.squinch.viewmodels;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import net.myacxy.retrotwitch.utils.StringUtil;
import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.retrotwitch.v5.api.common.Error;
import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.retrotwitch.v5.api.users.SimpleUsersResponse;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.retrotwitch.v5.helpers.RxErrorFactory;
import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.helpers.tracking.Th;
import net.myacxy.squinch.models.SettingsModel;
import net.myacxy.squinch.models.events.DashclockUpdateEvent;
import net.myacxy.squinch.utils.RetroTwitchUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SettingsViewModel implements ViewModel {

    private final DataHelper dataHelper;

    public SettingsModel settings;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SettingsViewModel(DataHelper dataHelper) {
        this.dataHelper = dataHelper;
        settings = dataHelper.recoverSettings();
        refresh();
    }

    public void refresh() {
        settings.userError.set(null);
        settings.userLogo.set(settings.user.get() != null ? settings.user.get().getLogo() : null);
        settings.deselectedChannelIds.set(dataHelper.getDeselectedChannelIds());
        updateSelectedChannelsText(dataHelper.getUserFollows(), settings.deselectedChannelIds.get());
    }

    public Consumer<String> getUser() {
        return userName -> {

            compositeDisposable.clear();
            settings.userError.set(null);
            settings.tmpUser.set(null);

            Th.l(this, "#getUser=%s", userName);
            if (userName != null && (userName = userName.trim()).length() != 0) {
                updateSelectedChannelsText(Collections.emptyList(), Collections.emptyList());
                settings.isLoadingUser.set(true);

                Disposable disposable = RxRetroTwitch.getInstance()
                        .users()
                        .translateUserNameToUserId(userName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(getUserObserver());

                compositeDisposable.add(disposable);
            }
        };
    }

    public void onHideExtensionChanged(boolean hide) {
        settings.isEmptyExtensionHidden.set(hide);
        dataHelper.setHideEmptyExtension(hide);
    }

    private void updateSelectedChannelsText(final List<UserFollow> userFollows, List<Long> deselectedChannelIds) {
        if (!userFollows.isEmpty()) {
            Observable.fromIterable(userFollows)
                    .filter(userFollow -> deselectedChannelIds.contains(userFollow.getChannel().getId()))
                    .count()
                    .subscribeOn(Schedulers.computation())
                    .subscribe((deselected, t) -> {
                        String text = String.format(Locale.getDefault(), "%d\u2009/\u2009%d", userFollows.size() - deselected, userFollows.size());
                        settings.selectedChannelsText.set(text);
                    });
            return;
        }
        settings.selectedChannelsText.set("0\u2009/\u20090");
    }

    private void onUserError(Throwable throwable) {
        Error error = RxErrorFactory.fromThrowable(throwable);
        Th.ex(throwable);
        settings.user.set(null);
        settings.userFollows.get().clear();
        dataHelper.setUser(null);
        dataHelper.setUserFollows(null);

        settings.userLogo.set(null);
        settings.isLoadingUser.set(false);
        settings.userError.set(error.getMessage());
        updateSelectedChannelsText(Collections.emptyList(), settings.deselectedChannelIds.get());
    }

    private void onUserFollowsError(Throwable throwable) {
        onUserError(throwable);
    }

    private DisposableObserver<Response<SimpleUsersResponse>> getUserObserver() {
        return new DisposableObserver<Response<SimpleUsersResponse>>() {

            @Override
            public void onNext(Response<SimpleUsersResponse> response) {
                Error error = RxErrorFactory.fromResponse(response);
                if (error != null) {
                    onUserError(new HttpException(response));
                    return;
                }

                settings.tmpUser.set(response.body().getUsers().get(0));
            }

            @Override
            public void onComplete() {
                if (settings.tmpUser.get() != null && !StringUtil.isEmpty(settings.tmpUser.get().getName())) {
                    Disposable disposable = RetroTwitchUtil.getAllUserFollows(settings.tmpUser.get().getId(),
                            progress -> {
                                updateSelectedChannelsText(progress, settings.deselectedChannelIds.get());
                                Th.l(SettingsViewModel.this, "userFollows.progress=%s", progress.toString());
                            })
                            .subscribeOn(Schedulers.io())
                            .doOnError(throwable -> onUserFollowsError(throwable))
                            .doOnSuccess(userFollows -> {
                                SimpleUser user = settings.tmpUser.get();
                                settings.user.set(user);
                                settings.userFollows.set(userFollows);
                                dataHelper.setUser(user);
                                dataHelper.setUserFollows(userFollows);

                                updateSelectedChannelsText(userFollows, settings.deselectedChannelIds.get());
                                settings.userLogo.set(user.getLogo());
                                settings.isLoadingUser.set(false);
                                Th.l(SettingsViewModel.this, "#onComplete.user=%s", user);
                            })
                            .flatMap(
                                    userFollows -> RetroTwitchUtil.getAllLiveStreams(userFollows,
                                            progress -> Th.l(SettingsViewModel.this, progress.toString()))
                            ).subscribe(
                                    streams -> {
                                        Th.l(SettingsViewModel.this, "#onComplete.streams=%s", streams);
                                        dataHelper.setLiveStreams(streams);
                                        EventBus.getDefault().post(new DashclockUpdateEvent(DashClockExtension.UPDATE_REASON_SETTINGS_CHANGED));
                                    },
                                    throwable -> onUserFollowsError(throwable)
                            );
                    compositeDisposable.add(disposable);
                } else {
                    settings.isLoadingUser.set(false);
                }
            }

            @Override
            public void onError(Throwable t) {
                onUserError(t);
            }
        };
    }
}
