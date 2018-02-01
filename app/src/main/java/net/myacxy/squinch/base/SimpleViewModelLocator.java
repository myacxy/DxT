package net.myacxy.squinch.base;

import android.content.SharedPreferences;

import net.myacxy.retrotwitch.v5.RxRetroTwitch;
import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.settings.SettingsViewModel;
import net.myacxy.squinch.settings.channelselection.ChannelSelectionViewModel;
import net.myacxy.squinch.settings.debuglog.DebugLogViewModel;

import java.util.HashMap;
import java.util.Map;

public class SimpleViewModelLocator {

    private final SettingsViewModel settingsViewModel;
    private final Map<String, ChannelSelectionViewModel> channelSelectionViewModels;
    private final DebugLogViewModel debugLogViewModel;
    private final DataHelper dataHelper;

    public SimpleViewModelLocator(RxRetroTwitch rxRetroTwitch, DataHelper dataHelper, SharedPreferences debugLogSharedPreferences) {
        this.dataHelper = dataHelper;

        settingsViewModel = new SettingsViewModel(rxRetroTwitch, dataHelper);
        channelSelectionViewModels = new HashMap<>();
        debugLogViewModel = new DebugLogViewModel(dataHelper, debugLogSharedPreferences);
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
