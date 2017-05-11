package net.myacxy.squinch;

import android.annotation.SuppressLint;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import net.myacxy.retrotwitch.Configuration;
import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.retrotwitch.v5.api.common.Direction;
import net.myacxy.retrotwitch.v5.api.common.SortBy;
import net.myacxy.retrotwitch.v5.api.common.StreamType;
import net.myacxy.retrotwitch.v5.api.common.TwitchConstants;
import net.myacxy.retrotwitch.v5.api.streams.Stream;
import net.myacxy.retrotwitch.v5.api.users.SimpleUsersResponse;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.retrotwitch.v5.api.users.UserFollowsResponse;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;

@SuppressLint("DefaultLocale")
public class RxJavaTest2 {

    private static final String CLIENT_ID_TESTING = "75gzbgqhk0tg6dhjbqtsphmy8sdayrr";

    @Before
    public void setup() {
        RxRetroTwitch.getInstance()
                .configure(new Configuration.ConfigurationBuilder()
                        .setLogLevel(HttpLoggingInterceptor.Level.BODY)
                        .setClientId(CLIENT_ID_TESTING)
                        .build()
                );
    }

    @Test
    public void example1() throws Exception {

        Single.just("sodapoppin")
                .flatMap(userName -> RetroTwitchUtil.translateUserNameToUserId(userName))
                .flatMap(response -> RetroTwitchUtil.getAllUserFollows(response.body().getUsers().get(0).getId()))
                .flatMap(userFollows -> RetroTwitchUtil.getAllLiveStreams(userFollows))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.trampoline())
                .toObservable().blockingSubscribe(System.out::println, System.out::println);

//        Observable<Long> getUserId = RxRetroTwitch.getInstance()
//                .users()
//                .translateUserNameToUserId("sodapoppin")
//                .toObservable()
//                .flatMap(response -> Observable.just(response.body().getUsers().get(0).getId()))
//                .(userId -> RxRetroTwitch.getInstance()
//                        .users()
//                        .getUserFollows(
//                                userId,
//                                TwitchConstants.MAX_LIMIT,
//                                TwitchConstants.DEFAULT_OFFSET,
//                                Direction.DEFAULT,
//                                SortBy.DEFAULT
//                        ));
//
//        Observable<Response<UserFollowsResponse>> first = ;
//
//        Observable<Response<UserFollowsResponse>> getAllUserFollows = Observable.merge(first,
//                Observable.range(1, Integer.MAX_VALUE)
//                        .concatMap(page -> RxRetroTwitch.getInstance()
//                                .users()
//                                .getUserFollows(
//                                        getUserId,
//                                        TwitchConstants.MAX_LIMIT,
//                                        TwitchConstants.MAX_LIMIT * page,
//                                        Direction.DEFAULT,
//                                        SortBy.DEFAULT
//                                ).toObservable().delaySubscription(first)
//                        ).takeUntil(RetroTwitchUtil.PREDICATE_REACHED_TOTAL)
//        );
//
//        getAllUserFollows.flatMap()
    }

    public static class RetroTwitchUtil {

        private static Predicate<Response<UserFollowsResponse>> PREDICATE_REACHED_TOTAL = response -> {
            if (response != null && !response.isSuccessful()) {
                UserFollowsResponse userFollowsResponse = response.body();
                if (userFollowsResponse.getUserFollows().size() == 0) {
                    return true;
                } else {
                    HttpUrl url = response.raw().request().url();
                    int limit = Integer.valueOf(url.queryParameter("limit"));
                    int offset = Integer.valueOf(url.queryParameter("offset"));
                    return offset + limit >= userFollowsResponse.getTotal();
                }
            }
            return true;
        };

        private RetroTwitchUtil() {
            throw new IllegalAccessError();
        }

        public static Single<List<UserFollow>> getAllUserFollows(long userId) {
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
                                    ).toObservable()
                    )
                    .takeUntil(PREDICATE_REACHED_TOTAL)
                    .reduceWith(ArrayList::new, (userFollows, response) -> {
                        if (response.code() != 200) {
                            throw new HttpException(response);
                        }
                        userFollows.addAll(response.body().getUserFollows());
//                    progress.accept(userFollows);
                        return userFollows;
                    });
        }

        private static boolean hasNoMoreUserFollows(Response<UserFollowsResponse> response) {
            if (response != null && !response.isSuccessful()) {
                UserFollowsResponse userFollowsResponse = response.body();
                if (userFollowsResponse.getUserFollows().size() == 0) {
                    return true;
                } else {
                    HttpUrl url = response.raw().request().url();
                    int limit = Integer.valueOf(url.queryParameter("limit"));
                    int offset = Integer.valueOf(url.queryParameter("offset"));
                    return offset + limit >= userFollowsResponse.getTotal();
                }
            }
            return true;
        }

        public static Single<List<Stream>> getAllLiveStreams(List<UserFollow> userFollows) {
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
                                    .takeUntil(response -> !response.isSuccessful()
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
//                    progress.accept(streams);
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

        @SuppressWarnings("Convert2MethodRef")
        private void bla() {
            Single.just("twitch")
                    .flatMap(userName -> translateUserNameToUserId(userName))
                    .flatMap(response -> getAllUserFollows(response.body().getUsers().get(0).getId()))
                    .flatMap(userFollows -> getAllLiveStreams(userFollows))
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .doOnError(e -> System.out.println(e.getMessage()))
                    .doOnSuccess(liveStreams -> System.out.println(liveStreams.size()))
                    .subscribe();
        }

        private static Single<Response<SimpleUsersResponse>> translateUserNameToUserId(String userName) {
            return RxRetroTwitch.getInstance().users().translateUserNameToUserId(userName);
        }
    }
}
