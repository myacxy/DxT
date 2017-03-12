package net.myacxy.squinch.viewmodels;

import android.databinding.ObservableArrayList;

import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.helpers.tracking.DebugLogTracker;
import net.myacxy.squinch.models.DebugLogEntry;

public class DebugViewModel implements ViewModel, DebugLogTracker.OnDebugLogChangedListener {

    private final DataHelper dataHelper;
    private final DebugLogTracker debugLogTracker;

    private ObservableArrayList<DebugLogEntry> debugLogEntries = new ObservableArrayList<>();

    public DebugViewModel(DataHelper dataHelper, DebugLogTracker debugLogTracker) {
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
