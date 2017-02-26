package net.myacxy.squinch.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.myacxy.retrotwitch.v5.api.streams.Stream;
import net.myacxy.retrotwitch.v5.api.users.SimpleUser;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.squinch.models.SettingsModel;
import net.myacxy.squinch.utils.JsonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataHelper {

    private SharedPreferences sp;

    public DataHelper(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Nullable
    public SimpleUser getUser() {
        String json = Setting.USER.load(sp, null);
        if (json == null) {
            return null;
        }
        return JsonUtil.fromJson(json, SimpleUser.class);
    }

    public void setUser(@Nullable SimpleUser user) {
        Setting.USER.save(sp, user != null ? JsonUtil.toJson(user) : null);
    }

    public int getUpdateInterval(int defValue) {
        return Setting.UPDATE_INTERVAL.load(sp, defValue);
    }

    public void setUpdateInterval(@IntRange(from = 15) int updateInterval) {
        Setting.UPDATE_INTERVAL.save(sp, updateInterval);
    }

    public boolean getHideEmptyExtension(boolean defValue) {
        return Setting.HIDE_EMPTY_EXTENSION.load(sp, defValue);
    }

    public void setHideEmptyExtension(boolean hideEmptyExtension) {
        Setting.HIDE_EMPTY_EXTENSION.save(sp, hideEmptyExtension);
    }

    public SettingsModel recoverSettings() {
        SettingsModel settings = new SettingsModel();
        settings.user.set(getUser());
        settings.userFollows.set(getUserFollows());
        settings.liveStreams.set(getLiveStreams());
        settings.updateInterval.set(getUpdateInterval(60));
        settings.isEmptyExtensionHidden.set(getHideEmptyExtension(true));
        return settings;
    }

    @NonNull
    public List<UserFollow> getUserFollows() {
        String json = Setting.USER_FOLLOWS.load(sp, null);
        if (json == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(JsonUtil.fromJson(json, UserFollow[].class)));
    }

    public void setUserFollows(@Nullable List<UserFollow> userFollows) {
        Setting.USER_FOLLOWS.save(sp, userFollows != null ? JsonUtil.toJson(userFollows) : null);
    }

    public List<Long> getDeselectedChannelIds() {
        String json = Setting.DESELECTED_CHANNEL_IDS.load(sp, null);
        if (json == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(JsonUtil.fromJson(json, Long[].class)));
    }

    public void setDeselectedChannelIds(List<Long> channelIds) {
        Setting.DESELECTED_CHANNEL_IDS.save(sp, JsonUtil.toJson(channelIds));
    }

    @NonNull
    public List<Stream> getLiveStreams() {
        String json = Setting.LIVE_STREAMS.load(sp, null);
        if (json == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(JsonUtil.fromJson(json, Stream[].class)));
    }

    public void setLiveStreams(@Nullable List<Stream> streams) {
        Setting.LIVE_STREAMS.save(sp, streams != null ? JsonUtil.toJson(streams) : null);
    }

    private interface SharedPreference<T> {

        String getKey();

        void save(SharedPreferences sp, T value);

        T load(SharedPreferences sp, T defValue);
    }

    private static class Setting {

        static final SharedPreference<Boolean> HIDE_EMPTY_EXTENSION = new SharedPreference<Boolean>() {

            @Override
            public String getKey() {
                return "pref.custom.visibility";
            }

            @Override
            public void save(SharedPreferences sp, Boolean value) {
                sp.edit().putBoolean(getKey(), value).apply();
            }

            @Override
            public Boolean load(SharedPreferences sp, Boolean defValue) {
                return sp.getBoolean(getKey(), defValue);
            }
        };

        static final SharedPreference<Integer> UPDATE_INTERVAL = new SharedPreference<Integer>() {

            @Override
            public String getKey() {
                return "pref.update.interval";
            }

            @Override
            public void save(SharedPreferences sp, Integer value) {
                sp.edit().putInt(getKey(), value).apply();
            }

            @Override
            public Integer load(SharedPreferences sp, Integer defValue) {
                return sp.getInt(getKey(), defValue);
            }
        };

        static final SharedPreference<String> USER = new SharedPreference<String>() {

            @Override
            public String getKey() {
                return "pref.user";
            }

            @Override
            public void save(SharedPreferences sp, String value) {
                sp.edit().putString(getKey(), value).apply();
            }

            @Override
            public String load(SharedPreferences sp, String defValue) {
                return sp.getString(getKey(), defValue);
            }
        };

        static final SharedPreference<String> USER_FOLLOWS = new SharedPreference<String>() {

            @Override
            public String getKey() {
                return "pref.user.follows";
            }

            @Override
            public void save(SharedPreferences sp, String value) {
                sp.edit().putString(getKey(), value).apply();
            }

            @Override
            public String load(SharedPreferences sp, String defValue) {
                return sp.getString(getKey(), defValue);
            }
        };

        static final SharedPreference<String> DESELECTED_CHANNEL_IDS = new SharedPreference<String>() {

            @Override
            public String getKey() {
                return "pref.channels.deselected.id";
            }

            @Override
            public void save(SharedPreferences sp, String value) {
                sp.edit().putString(getKey(), value).apply();
            }

            @Override
            public String load(SharedPreferences sp, String defValue) {
                return sp.getString(getKey(), defValue);
            }
        };

        static final SharedPreference<String> LIVE_STREAMS = new SharedPreference<String>() {

            @Override
            public String getKey() {
                return "pref.streams.live";
            }

            @Override
            public void save(SharedPreferences sp, String value) {
                sp.edit().putString(getKey(), value).apply();
            }

            @Override
            public String load(SharedPreferences sp, String defValue) {
                return sp.getString(getKey(), defValue);
            }
        };
    }
}
