package net.myacxy.squinch.settings.debuglog;

import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.myacxy.squinch.databinding.DebugLogEntryItemBinding;
import net.myacxy.squinch.models.DebugLogEntry;

public class DebugLogAdapter extends RecyclerView.Adapter<DebugLogAdapter.DebugLogEntryViewHolder> {

    private final ObservableList<DebugLogEntry> entries;

    public DebugLogAdapter(ObservableList<DebugLogEntry> entries) {
        this.entries = entries;
    }

    @Override
    public DebugLogEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DebugLogEntryItemBinding binding = DebugLogEntryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DebugLogEntryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DebugLogEntryViewHolder holder, int position) {
        holder.bind(entries.get(position));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    protected static class DebugLogEntryViewHolder extends RecyclerView.ViewHolder {

        private DebugLogEntryItemBinding binding;

        public DebugLogEntryViewHolder(DebugLogEntryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DebugLogEntry entry) {
            binding.setEntry(entry);
            binding.executePendingBindings();
        }
    }
}
