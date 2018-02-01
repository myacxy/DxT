package net.myacxy.squinch.settings;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.android.apps.dashclock.api.DashClockExtension;

import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.retrotwitch.v5.api.common.RetroTwitchError;
import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.retrotwitch.v5.api.users.SimpleUsersResponse;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.retrotwitch.v5.helpers.RxRetroTwitchErrorFactory;
import net.myacxy.squinch.base.ViewModel;
import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.models.events.DashclockUpdateEvent;
import net.myacxy.squinch.utils.RetroTwitchUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;

public class SettingsViewModel extends BaseObservable implements ViewModel {

    private final RxRetroTwitch rxRetroTwitch;
    private final DataHelper dataHelper;
    private final SettingsModel settings;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SettingsViewModel(RxRetroTwitch rxRetroTwitch, DataHelper dataHelper) {
        this.rxRetroTwitch = rxRetroTwitch;
        this.dataHelper = dataHelper;
        settings = dataHelper.getSettings();
        refresh();
    }

    public void refresh() {
        settings.setUserError(null);
    }

    @Bindable
    public SettingsModel getSettings() {
        return settings;
    }

    public Consumer<String> getUser() {
        return userName -> {
            compositeDisposable.clear();
            dataHelper.setUser(null);
            dataHelper.setUserFollows(null);
            dataHelper.setLiveStreams(null);
            settings.setUserError(null);

            Timber.d("#getUser=%s", userName);
            if (userName != null && (userName = userName.trim()).length() != 0) {
                settings.setLoadingUser(true);

                rxRetroTwitch.users()
                        .translateUserNameToUserId(userName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(compositeDisposable::add)
                        .doOnError(t -> onUserError(RxRetroTwitchErrorFactory.fromThrowable(t)))
                        .doOnSuccess(response -> {
                            if (onGetUserSuccess(response)) {
                                getUserFollows(settings.getUser());
                            }
                        })
                        .subscribe();
            }
        };
    }

    public void onHideExtensionChanged(boolean hide) {
        dataHelper.setHideEmptyExtension(hide);
    }

    private void onUserError(RetroTwitchError error) {
        Timber.e(error.getCause(), "%s", error.toString());
        dataHelper.setUser(null);
        dataHelper.setUserFollows(null);

        settings.setLoadingUser(false);
        settings.setUserError(error.getMessage());
    }

    private boolean onGetUserSuccess(Response<SimpleUsersResponse> response) {
        RetroTwitchError error = RxRetroTwitchErrorFactory.fromResponse(response);
        if (error != null) {
            onUserError(error);
            return false;
        } else if (response.body().getUsers().size() == 0) {
            onUserError(new RetroTwitchError("USER_NAME_TO_USER_ID_EMPTY", 200, "User does not exist"));
            return false;
        }

        SimpleUser user = response.body().getUsers().get(0);
        Timber.d("#onComplete.user=%s", user);
        dataHelper.setUser(user);
        return true;
    }

    private void getUserFollows(SimpleUser user) {
        RetroTwitchUtil.getAllUserFollows(rxRetroTwitch, user.getId(),
                progress -> {
                    dataHelper.setUserFollows(progress);
                    Timber.d("userFollows.progress=%d", progress.size());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add)
                .doOnError(t -> onUserError(RxRetroTwitchErrorFactory.fromThrowable(t)))
                .doOnSuccess(userFollows -> {
                    dataHelper.setUserFollows(userFollows);
                    settings.setLoadingUser(false);
                    Timber.d("#onComplete.user=%s", user);
                    getLiveStreams(userFollows);
                })
                .subscribe();
    }

    private void getLiveStreams(List<UserFollow> userFollows) {
        RetroTwitchUtil.getAllLiveStreams(rxRetroTwitch, userFollows, progress -> Timber.d("streams.progress=%d", progress.size()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add)
                .doOnError(t -> onUserError(RxRetroTwitchErrorFactory.fromThrowable(t)))
                .doOnSuccess(streams -> {
                    Timber.d("#onComplete.streams=%d", streams.size());
                    dataHelper.setLiveStreams(streams);
                    EventBus.getDefault().post(new DashclockUpdateEvent(DashClockExtension.UPDATE_REASON_SETTINGS_CHANGED));
                })
                .subscribe();
    }
}
