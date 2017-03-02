package net.myacxy.squinch.views.adapters;

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

    protected class DebugLogEntryViewHolder extends RecyclerView.ViewHolder {

        private DebugLogEntryItemBinding mBinding;

        public DebugLogEntryViewHolder(DebugLogEntryItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(DebugLogEntry entry) {
            mBinding.setEntry(entry);
            mBinding.executePendingBindings();
        }
    }
}
