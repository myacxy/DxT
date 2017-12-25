package net.myacxy.squinch;

import net.myacxy.retrotwitch.Configuration;
import net.myacxy.retrotwitch.v5.RxRetroTwitch;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.logging.HttpLoggingInterceptor;

@Module
public class NetworkModule {

    @Provides
    @ApplicationScope
    public RxRetroTwitch retroTwitch(Configuration configuration) {
        return new RxRetroTwitch().configure(configuration);
    }

    @Provides
    @ApplicationScope
    public Configuration configuration(@Named("twitch_client_id") String twitchClientId) {
        return new Configuration.ConfigurationBuilder()
                .setLogLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE)
                .setClientId(twitchClientId)
                .build();
    }

    @Provides
    @ApplicationScope
    @Named("twitch_client_id")
    public String twitchClientId() {
        return "75gzbgqhk0tg6dhjbqtsphmy8sdayrr";
    }
}
