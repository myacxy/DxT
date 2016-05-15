package net.myacxy.ditch.viewmodels;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import net.myacxy.ditch.models.SettingsModel;

public class SettingsViewModel
{
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableBoolean hideEmptyExtension = new ObservableBoolean();
    public ObservableInt updateInterval = new ObservableInt();

    public SettingsViewModel(SettingsModel settings) {
        userName.set(settings.userName);
        hideEmptyExtension.set(settings.hideEmptyExtension);
        updateInterval.set(settings.updateInterval);
    }
}
