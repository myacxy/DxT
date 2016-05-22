package net.myacxy.ditch.viewmodels;

import android.databinding.ObservableArrayList;

import com.orhanobut.logger.Logger;

import net.myacxy.ditch.views.adapters.SelectableUserFollowsAdapter;
import net.myacxy.retrotwitch.RxCaller;
import net.myacxy.retrotwitch.api.Direction;
import net.myacxy.retrotwitch.api.SortBy;
import net.myacxy.retrotwitch.helpers.RxErrorFactory;
import net.myacxy.retrotwitch.models.Error;
import net.myacxy.retrotwitch.models.UserFollow;
import net.myacxy.retrotwitch.models.UserFollowsContainer;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChannelSelectionViewModel
{
    public ObservableArrayList<UserFollow> userFollows = new ObservableArrayList<>();
    private String mUserName;
    private SelectableUserFollowsAdapter mAdapter;
    private Subscription mSubscription;
    private Observer<UserFollowsContainer> mObserver = new Observer<UserFollowsContainer>()
    {
        @Override
        public void onCompleted()
        {
            mAdapter.notifyDataSetChanged();
            mSubscription.unsubscribe();
            mSubscription = null;
            Logger.t(1).d(mUserName + " " + String.valueOf(userFollows.size()));
        }

        @Override
        public void onError(Throwable e)
        {
            Error error = RxErrorFactory.fromThrowable(e);
            Logger.t(1).d(error.message);
        }

        @Override
        public void onNext(UserFollowsContainer userFollowsContainer)
        {
            userFollows.addAll(userFollowsContainer.userFollows);
        }
    };

    public ChannelSelectionViewModel(String userName) {
        mUserName = userName;


        mAdapter = new SelectableUserFollowsAdapter(userFollows);

        getUserFollows();
    }

    private void getUserFollows()
    {
        mSubscription = RxCaller.getInstance()
                .getAllUserFollows(mUserName, Direction.DEFAULT, SortBy.CREATED_AT, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
    }

    public SelectableUserFollowsAdapter getAdapter() {
        return mAdapter;
    }
}
