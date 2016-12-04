package net.myacxy.squinch.viewmodels;

import android.databinding.ObservableArrayList;

import com.orhanobut.logger.Logger;

import net.myacxy.squinch.views.adapters.SelectableUserFollowsAdapter;
import net.myacxy.retrotwitch.RxCaller;
import net.myacxy.retrotwitch.api.Direction;
import net.myacxy.retrotwitch.api.SortBy;
import net.myacxy.retrotwitch.helpers.RxErrorFactory;
import net.myacxy.retrotwitch.models.Error;
import net.myacxy.retrotwitch.models.UserFollow;
import net.myacxy.retrotwitch.models.UserFollowsContainer;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChannelSelectionViewModel
{
    public ObservableArrayList<UserFollow> userFollows = new ObservableArrayList<>();
    private String mUserName;
    private SelectableUserFollowsAdapter mAdapter;
    private Disposable mSubscription;
    private Observer<UserFollowsContainer> mObserver = new Observer<UserFollowsContainer>()
    {
        @Override
        public void onSubscribe(Disposable disposable)
        {
            mSubscription = disposable;
        }

        @Override
        public void onNext(UserFollowsContainer userFollowsContainer)
        {
            userFollows.addAll(userFollowsContainer.userFollows);
        }

        @Override
        public void onComplete()
        {
            mAdapter.notifyDataSetChanged();
            mSubscription.dispose();
            mSubscription = null;
            Logger.t(1).d(mUserName + " " + String.valueOf(userFollows.size()));
        }

        @Override
        public void onError(Throwable e)
        {
            Error error = RxErrorFactory.fromThrowable(e);
            Logger.t(1).d(error.message);
        }
    };

    public ChannelSelectionViewModel(String userName) {
        mUserName = userName;


        mAdapter = new SelectableUserFollowsAdapter(userFollows);

        getUserFollows();
    }

    private void getUserFollows()
    {
        RxCaller.getInstance()
                .getUserFollows(mUserName, 100, 0, Direction.DEFAULT, SortBy.CREATED_AT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
    }

    public SelectableUserFollowsAdapter getAdapter() {
        return mAdapter;
    }
}