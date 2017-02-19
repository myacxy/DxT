package net.myacxy.squinch.utils;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.retrotwitch.v5.api.common.Direction;
import net.myacxy.retrotwitch.v5.api.common.SortBy;
import net.myacxy.retrotwitch.v5.api.common.TwitchConstants;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

// TODO: 19.02.2017 move helper methods into library?
public class RetroTwitchUtil {

    private RetroTwitchUtil() {
        throw new IllegalAccessError();
    }

    public static Single<ArrayList<UserFollow>> getAllUserFollows(long userId, Consumer<List<UserFollow>> progress) {
        return Observable.range(0, Integer.MAX_VALUE)
                .concatMap(page ->
                        RxRetroTwitch.getInstance()
                                .users()
                                .getUserFollows(
                                        userId,
                                        TwitchConstants.MAX_LIMIT,
                                        page * TwitchConstants.MAX_LIMIT,
                                        Direction.DEFAULT,
                                        SortBy.DEFAULT
                                )
                )
                .takeUntil(response -> response.code() != 200 || response.body().getUserFollows().size() == 0)
                .reduceWith(() -> new ArrayList<UserFollow>(), (userFollows, response) -> {
                    if (response.code() != 200) {
                        throw new HttpException(response);
                    }
                    userFollows.addAll(response.body().getUserFollows());
                    progress.accept(userFollows);
                    return userFollows;
                });
    }
}
