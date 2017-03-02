package net.myacxy.squinch;

import android.content.Context;

import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.helpers.tracking.DebugLogTracker;
import net.myacxy.squinch.viewmodels.ChannelSelectionViewModel;
import net.myacxy.squinch.viewmodels.DebugViewModel;
import net.myacxy.squinch.viewmodels.MainViewModel;
import net.myacxy.squinch.viewmodels.SettingsViewModel;

import java.util.HashMap;
import java.util.Map;

public class SimpleViewModelLocator {

    private static SimpleViewModelLocator INSTANCE;

    private final MainViewModel mainViewModel;
    private final SettingsViewModel settingsViewModel;
    private final Map<String, ChannelSelectionViewModel> channelSelectionViewModels;
    private final DebugViewModel debugViewModel;

    private final DataHelper dataHelper;

    private SimpleViewModelLocator(Context context, DebugLogTracker debugLogTracker) {
        dataHelper = new DataHelper(context);

        mainViewModel = new MainViewModel();
        settingsViewModel = new SettingsViewModel(dataHelper);
        channelSelectionViewModels = new HashMap<>();
        debugViewModel = new DebugViewModel(dataHelper, debugLogTracker);
    }

    public static SimpleViewModelLocator getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("initialize not called");
        }
        return INSTANCE;
    }

    public static void initialize(Context applicationContext, DebugLogTracker debugLogTracker) {
        INSTANCE = new SimpleViewModelLocator(applicationContext, debugLogTracker);
    }

    public MainViewModel getMainViewModel() {
        return mainViewModel;
    }

    public SettingsViewModel getSettingsViewModel() {
        return settingsViewModel;
    }

    public ChannelSelectionViewModel getChannelSelectionViewModel(String userName) {
        if (channelSelectionViewModels.containsKey(userName)) {
            return channelSelectionViewModels.get(userName);
        }
        channelSelectionViewModels.clear();
        ChannelSelectionViewModel viewModel = new ChannelSelectionViewModel(dataHelper);
        channelSelectionViewModels.put(userName, viewModel);
        return viewModel;
    }

    public DebugViewModel getDebugViewModel() {
        return debugViewModel;
    }
}
