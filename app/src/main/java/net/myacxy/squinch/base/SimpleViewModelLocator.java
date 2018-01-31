package net.myacxy.squinch.base;

import android.content.Context;

import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.helpers.tracking.DebugLogTracker;
import net.myacxy.squinch.settings.SettingsViewModel;
import net.myacxy.squinch.settings.channelselection.ChannelSelectionViewModel;
import net.myacxy.squinch.settings.debuglog.DebugLogViewModel;

import java.util.HashMap;
import java.util.Map;

public class SimpleViewModelLocator {

    private static SimpleViewModelLocator INSTANCE;

    private final SettingsViewModel settingsViewModel;
    private final Map<String, ChannelSelectionViewModel> channelSelectionViewModels;
    private final DebugLogViewModel debugLogViewModel;

    private final DataHelper dataHelper;

    private SimpleViewModelLocator(Context context, DebugLogTracker debugLogTracker) {
        dataHelper = new DataHelper(context);

        settingsViewModel = new SettingsViewModel(dataHelper);
        channelSelectionViewModels = new HashMap<>();
        debugLogViewModel = new DebugLogViewModel(dataHelper, debugLogTracker);
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

    public DebugLogViewModel getDebugLogViewModel() {
        return debugLogViewModel;
    }
}
