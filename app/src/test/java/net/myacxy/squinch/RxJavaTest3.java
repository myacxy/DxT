package net.myacxy.squinch;

import android.annotation.SuppressLint;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import net.myacxy.retrotwitch.v5.api.users.SimpleUsersResponse;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

@SuppressLint("DefaultLocale")
public class RxJavaTest3 {

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request.Builder builder = chain.request()
                        .newBuilder()
                        .header("Accept", "application/vnd.twitchtv.v5+json")
                        .header("Client-ID", "75gzbgqhk0tg6dhjbqtsphmy8sdayrr");
                return chain.proceed(builder.build());
            }).build();

    Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl("https://api.twitch.tv/kraken/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client);

    public interface TwitchUsersService {
        @GET("users")
        Call<SimpleUsersResponse> translateUserNameToUserId(@Query("login") String userName);
    }

    @Test
    public void retrofit() throws Exception {

        Retrofit retrofit = retrofitBuilder.build();

        TwitchUsersService service = retrofit.create(TwitchUsersService.class);

        // synchronous
        Call<SimpleUsersResponse> translate = service.translateUserNameToUserId("twitch");
        System.out.println(translate.execute().body().getUsers().get(0).getId());

        // asynchronous
        CountDownLatch latch = new CountDownLatch(1);
        translate = service.translateUserNameToUserId("twitch");
        translate.enqueue(new Callback<SimpleUsersResponse>() {
            @Override
            public void onResponse(Call<SimpleUsersResponse> call, Response<SimpleUsersResponse> response) {
                System.out.println(response.body().getUsers().get(0).getId());
                latch.countDown();
            }

            @Override
            public void onFailure(Call<SimpleUsersResponse> call, Throwable throwable) {
                throwable.printStackTrace();
                latch.countDown();
            }
        });
        latch.await();
    }

    public interface RxTwitchUsersService {
        @GET("users")
        Single<Response<SimpleUsersResponse>> translateUserNameToUserId(@Query("login") String userName);
    }

    @Test
    public void rxRetrofit() {

        Retrofit retrofit = retrofitBuilder
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        RxTwitchUsersService service = retrofit.create(RxTwitchUsersService.class);

        Single<Response<SimpleUsersResponse>> twitch = service.translateUserNameToUserId("twitch");
        System.out.println(twitch.blockingGet().body().getUsers().get(0).getId());
    }
}
