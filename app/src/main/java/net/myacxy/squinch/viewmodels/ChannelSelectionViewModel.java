package net.myacxy.squinch.viewmodels;

import android.databinding.ObservableArrayList;

import com.orhanobut.logger.Logger;

import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.retrotwitch.v5.api.common.Direction;
import net.myacxy.retrotwitch.v5.api.common.Error;
import net.myacxy.retrotwitch.v5.api.common.SortBy;
import net.myacxy.retrotwitch.v5.api.users.SimpleUsersResponse;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.retrotwitch.v5.api.users.UserFollowsResponse;
import net.myacxy.retrotwitch.v5.helpers.RxErrorFactory;
import net.myacxy.squinch.views.adapters.SelectableUserFollowsAdapter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ChannelSelectionViewModel {

    private String mUserName;
    private SelectableUserFollowsAdapter mAdapter;

    private ObservableArrayList<UserFollow> userFollows = new ObservableArrayList<>();
    private Disposable mUserFollowsSubscription;
    private DisposableObserver<Response<UserFollowsResponse>> mUserFollowsObserver = new DisposableObserver<Response<UserFollowsResponse>>() {
        @Override
        public void onNext(Response<UserFollowsResponse> response) {
            userFollows.addAll(response.body().getUserFollows());
        }

        @Override
        public void onComplete() {
            mAdapter.notifyDataSetChanged();
            mUserFollowsSubscription.dispose();
            mUserFollowsSubscription = null;
            Logger.t(1).d(mUserName + " " + String.valueOf(userFollows.size()));
        }

        @Override
        public void onError(Throwable e) {
            Error error = RxErrorFactory.fromThrowable(e);
            Logger.t(1).d(error.getMessage());
        }
    };

    private Disposable mUserSubscription;
    private DisposableObserver<Response<SimpleUsersResponse>> mUserObserver = new DisposableObserver<Response<SimpleUsersResponse>>() {
        @Override
        public void onNext(Response<SimpleUsersResponse> response) {
            mUserFollowsSubscription = RxRetroTwitch.getInstance()
                    .users()
                    .getUserFollows(response.body().getUsers().get(0).getId(), 100, 0, Direction.DEFAULT, SortBy.CREATED_AT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(mUserFollowsObserver);
        }

        @Override
        public void onComplete() {
            mUserSubscription.dispose();
            mUserSubscription = null;
        }

        @Override
        public void onError(Throwable e) {
            Error error = RxErrorFactory.fromThrowable(e);
            Logger.t(1).d(error.getMessage());
        }
    };

    public ChannelSelectionViewModel(String userName) {
        mUserName = userName;
        mAdapter = new SelectableUserFollowsAdapter(userFollows);

        getUserFollows();
    }

    private void getUserFollows() {
        mUserSubscription = RxRetroTwitch.getInstance()
                .users()
                .translateUserNameToUserId(mUserName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(mUserObserver);
    }

    public SelectableUserFollowsAdapter getAdapter() {
        return mAdapter;
    }
}
