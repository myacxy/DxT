package net.myacxy.palpi;

import android.content.Context;

import net.myacxy.palpi.helpers.SharedPreferencesHelper;
import net.myacxy.palpi.models.SettingsModel;
import net.myacxy.palpi.viewmodels.ChannelSelectionViewModel;
import net.myacxy.palpi.viewmodels.MainViewModel;
import net.myacxy.palpi.viewmodels.SettingsViewModel;

import java.util.HashMap;
import java.util.Map;

public class SimpleViewModelLocator
{
    private static SimpleViewModelLocator INSTANCE;
    private final MainViewModel mMainViewModel;
    private final SettingsViewModel mSettingsViewModel;
    private final Map<String, ChannelSelectionViewModel> mChannelSelectionViewModels;

    public SimpleViewModelLocator(Context context)
    {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        SettingsModel settings = sharedPreferencesHelper.getSettings();

        mMainViewModel = new MainViewModel();
        mSettingsViewModel = new SettingsViewModel(settings);
        mChannelSelectionViewModels = new HashMap<>();
    }

    public static SimpleViewModelLocator getInstance()
    {
        if (INSTANCE == null)
        {
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

    public ChannelSelectionViewModel getChannelSelectionViewModel(String userName)
    {
        if (mChannelSelectionViewModels.containsKey(userName))
        {
            return mChannelSelectionViewModels.get(userName);
        }
        ChannelSelectionViewModel viewModel = new ChannelSelectionViewModel(userName);
        mChannelSelectionViewModels.put(userName, viewModel);
        return viewModel;
    }
}
