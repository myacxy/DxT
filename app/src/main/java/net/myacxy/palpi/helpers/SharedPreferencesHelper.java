package net.myacxy.palpi.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import net.myacxy.palpi.models.SettingsModel;

public class SharedPreferencesHelper
{
    private final Context mContext;
    private SharedPreferences mSharedPreferences;

    public SharedPreferencesHelper(Context context)
    {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public String getUserName(@Nullable String defValue)
    {
        return mSharedPreferences.getString(Setting.STRING_USER_NAME.getKey(), defValue);
    }

    public void setUserName(String userName)
    {
        mSharedPreferences.edit().putString(Setting.STRING_USER_NAME.getKey(), userName).apply();
    }

    public int getUpdateInterval(int defValue)
    {
        return mSharedPreferences.getInt(Setting.INT_UPDATE_INTERVAL.getKey(), defValue);
    }

    public void setUpdateInterval(@IntRange(from = 15) int updateInterval)
    {
        mSharedPreferences.edit().putInt(Setting.INT_UPDATE_INTERVAL.getKey(), updateInterval).apply();
    }

    public boolean getHideEmptyExtension(boolean defValue)
    {
        return mSharedPreferences.getBoolean(Setting.BOOL_CUSTOM_VISIBILITY.getKey(), defValue);
    }

    public void setHideEmptyExtension(boolean hideEmptyExtension)
    {
        mSharedPreferences.edit().putBoolean(Setting.BOOL_CUSTOM_VISIBILITY.getKey(), hideEmptyExtension).apply();
    }

    public SettingsModel getSettings()
    {
        return new SettingsModel()
        {
            {
                // TODO: 16.05.2016
//                user = getUserName(null);
                updateInterval = getUpdateInterval(15);
                hideEmptyExtension = getHideEmptyExtension(true);
            }
        };
    }

    enum Setting implements SharedPreferencesKey
    {
        BOOL_CUSTOM_VISIBILITY("pref.custom.visibility"),
        INT_UPDATE_INTERVAL("pref.update.interval"),
        STRING_USER_NAME("pref.user.name");

        private final String mKey;

        Setting(String key)
        {
            mKey = key;
        }

        @Override
        public String getKey()
        {
            return mKey;
        }
    }

    private interface SharedPreferencesKey
    {
        String getKey();
    }
}
