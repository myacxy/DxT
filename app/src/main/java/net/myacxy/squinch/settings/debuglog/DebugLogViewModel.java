package net.myacxy.squinch.settings.debuglog;

import android.databinding.ObservableArrayList;

import net.myacxy.squinch.base.ViewModel;
import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.helpers.tracking.DebugLogTracker;

public class DebugLogViewModel implements ViewModel, DebugLogTracker.OnDebugLogChangedListener {

    private final DataHelper dataHelper;
    private final DebugLogTracker debugLogTracker;

    private ObservableArrayList<DebugLogEntry> debugLogEntries = new ObservableArrayList<>();

    public DebugLogViewModel(DataHelper dataHelper, DebugLogTracker debugLogTracker) {
        this.dataHelper = dataHelper;
        this.debugLogTracker = debugLogTracker;

        debugLogTracker.setOnDebugLogChangedListener(this);
        debugLogEntries.addAll(dataHelper.getDebugLogEntries());
    }

    public ObservableArrayList<DebugLogEntry> getDebugLogEntries() {
        return debugLogEntries;
    }

    @Override
    public void onEntryAdded(DebugLogEntry debugLogEntry) {
        debugLogEntries.add(debugLogEntry);
    }
}
