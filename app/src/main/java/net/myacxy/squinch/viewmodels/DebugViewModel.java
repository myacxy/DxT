package net.myacxy.squinch.viewmodels;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Handler;
import android.os.Looper;

import net.myacxy.squinch.helpers.DataHelper;
import net.myacxy.squinch.helpers.tracking.DebugLogTracker;
import net.myacxy.squinch.models.DebugLogEntry;
import net.myacxy.squinch.views.adapters.DebugLogAdapter;

public class DebugViewModel implements ViewModel, DebugLogTracker.OnDebugLogChangedListener {

    private final DataHelper dataHelper;
    private final DebugLogTracker debugLogTracker;

    private Handler handler = new Handler(Looper.getMainLooper());
    private DebugLogAdapter adapter;
    private ObservableArrayList<DebugLogEntry> debugLogEntries = new ObservableArrayList<>();
    private ObservableList.OnListChangedCallback<ObservableList<DebugLogEntry>> onListChangedCallback = new ObservableList.OnListChangedCallback<ObservableList<DebugLogEntry>>() {
        @Override
        public void onChanged(ObservableList<DebugLogEntry> sender) {
//            handler.post(() -> adapter.notifyDataSetChanged());
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<DebugLogEntry> sender, int positionStart, int itemCount) {
//            handler.post(() -> adapter.notifyItemRangeChanged(positionStart, itemCount));
            adapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<DebugLogEntry> sender, int positionStart, int itemCount) {
//            handler.post(() -> adapter.notifyItemRangeInserted(positionStart, itemCount));
            adapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<DebugLogEntry> sender, int fromPosition, int toPosition, int itemCount) {
            adapter.notifyItemRangeRemoved(fromPosition, itemCount);
            adapter.notifyItemRangeInserted(toPosition, itemCount);
        }

        @Override
        public void onItemRangeRemoved(ObservableList<DebugLogEntry> sender, int positionStart, int itemCount) {
//            handler.post(() -> adapter.notifyItemRangeRemoved(positionStart, itemCount));
            adapter.notifyItemRangeRemoved(positionStart, itemCount);
        }
    };

    public DebugViewModel(DataHelper dataHelper, DebugLogTracker debugLogTracker) {
        this.dataHelper = dataHelper;
        this.debugLogTracker = debugLogTracker;
        debugLogEntries.addAll(dataHelper.getDebugLogEntries());
    }

    public DebugLogAdapter createAdapter() {
        adapter = new DebugLogAdapter(debugLogEntries);
        return adapter;
    }

    @Override
    public void onEntryAdded(DebugLogEntry debugLogEntry) {
        debugLogEntries.add(debugLogEntry);
    }

    public void attach() {
        debugLogTracker.setOnDebugLogChangedListener(this);
        debugLogEntries.addOnListChangedCallback(onListChangedCallback);
    }

    public void deattach() {
        debugLogEntries.removeOnListChangedCallback(onListChangedCallback);
        adapter = null;
    }
}
