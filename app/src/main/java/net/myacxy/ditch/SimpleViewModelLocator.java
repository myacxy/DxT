package net.myacxy.ditch;

import android.content.Context;

import net.myacxy.ditch.helpers.SharedPreferencesHelper;
import net.myacxy.ditch.models.SettingsModel;
import net.myacxy.ditch.viewmodels.MainViewModel;
import net.myacxy.ditch.viewmodels.SettingsViewModel;

public class SimpleViewModelLocator
{
    private static SimpleViewModelLocator INSTANCE;
    private final MainViewModel mMainViewModel;
    private final SettingsViewModel mSettingsViewModel;

    public SimpleViewModelLocator(Context context)
    {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        SettingsModel settings = sharedPreferencesHelper.getSettings();

        mMainViewModel = new MainViewModel();
        mSettingsViewModel = new SettingsViewModel(settings);
    }

    public static SimpleViewModelLocator getInstance()
    {
        if(INSTANCE == null) {
            throw new IllegalStateException("initialize not called");
        }
        return INSTANCE;
    }

    public static void initialize(Context applicationContext)
    {
        INSTANCE = new SimpleViewModelLocator(applicationContext);
    }

    public MainViewModel getMainViewModel()
    {
        return mMainViewModel;
    }

    public SettingsViewModel getSettingsViewModel()
    {
        return mSettingsViewModel;
    }
}
