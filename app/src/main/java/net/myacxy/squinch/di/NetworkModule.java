package net.myacxy.squinch.di;

import net.myacxy.retrotwitch.Configuration;
import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.squinch.BuildConfig;
import net.myacxy.squinch.base.di.PerApplication;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.logging.HttpLoggingInterceptor;

@Module
public class NetworkModule {

    @Provides
    @PerApplication
    @Named("twitch_client_id")
    public String twitchClientId() {
        return "75gzbgqhk0tg6dhjbqtsphmy8sdayrr";
    }

    @Provides
    @PerApplication
    public Configuration configuration(@Named("twitch_client_id") String twitchClientId) {
        return new Configuration.ConfigurationBuilder()
                .setLogLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE)
                .setClientId(twitchClientId)
                .build();
    }

    @Provides
    @PerApplication
    public RxRetroTwitch rxRetroTwitch(Configuration configuration) {
        return new RxRetroTwitch().configure(configuration);
    }
}
