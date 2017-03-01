package net.myacxy.squinch.utils;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.retrotwitch.v5.api.common.Direction;
import net.myacxy.retrotwitch.v5.api.common.SortBy;
import net.myacxy.retrotwitch.v5.api.common.StreamType;
import net.myacxy.retrotwitch.v5.api.common.TwitchConstants;
import net.myacxy.retrotwitch.v5.api.streams.Stream;
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

    public static Single<List<UserFollow>> getAllUserFollows(long userId, Consumer<List<UserFollow>> progress) {
        RequestMeta requestMeta = new RequestMeta();
        return Observable.range(0, Integer.MAX_VALUE)
                .concatMap(page ->
                        RxRetroTwitch.getInstance()
                                .users()
                                .getUserFollows(
                                        userId,
                                        requestMeta.limit,
                                        requestMeta.offset,
                                        Direction.DEFAULT,
                                        SortBy.DEFAULT
                                )
                )
                .takeUntil(response -> response.code() != 200
                        || response.body().getUserFollows().size() == 0
                        || !requestMeta.hasNext(response.body().getTotal())
                )
                .reduceWith(ArrayList::new, (userFollows, response) -> {
                    if (response.code() != 200) {
                        throw new HttpException(response);
                    }
                    userFollows.addAll(response.body().getUserFollows());
                    progress.accept(userFollows);
                    return userFollows;
                });
    }

    public static Single<List<Stream>> getAllLiveStreams(List<UserFollow> userFollows, Consumer<List<Stream>> progress) {
        RequestMeta requestMeta = new RequestMeta();
        return Observable.fromIterable(userFollows)
                .map(UserFollow::getChannel)
                .toList()
                .toObservable()
                .concatMap(channels ->
                        Observable.range(0, Integer.MAX_VALUE)
                                .concatMap(page -> RxRetroTwitch.getInstance()
                                        .streams()
                                        .getStreams(channels,
                                                null,
                                                null,
                                                StreamType.ALL,
                                                requestMeta.limit,
                                                requestMeta.offset
                                        )
                                )
                                .takeUntil(response -> response.code() != 200
                                        || response.body().getStreams().size() == 0
                                        || !requestMeta.hasNext(response.body().getTotal())
                                )
                )
                .reduceWith(ArrayList::new, (streams, response) -> {
                    if (response.code() != 200) {
                        throw new HttpException(response);
                    }
                    for (Stream stream : response.body().getStreams()) {
                        if (stream != null) {
                            streams.add(stream);
                        }
                    }
                    progress.accept(streams);
                    return streams;
                });
    }

    private static class RequestMeta {
        int offset = 0;
        int limit = TwitchConstants.MAX_LIMIT;

        boolean hasNext(int total) {
            offset += limit;
            return offset < total;
        }
    }
}
