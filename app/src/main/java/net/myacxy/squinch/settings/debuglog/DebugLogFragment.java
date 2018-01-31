package net.myacxy.squinch.settings.debuglog;

import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.myacxy.squinch.R;
import net.myacxy.squinch.base.MvvmFragment;
import net.myacxy.squinch.base.SimpleViewModelLocator;

import butterknife.BindView;

public class DebugLogFragment extends MvvmFragment {

    public static final String TAG = DebugLogFragment.class.getSimpleName();

    @BindView(R.id.rv_dl_entries)
    protected RecyclerView entries;

    private DebugLogAdapter adapter;
    private StickyOnListChangedCallback callback;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_debug_log;
    }

    @Override
    protected DebugLogViewModel getViewModel() {
        return SimpleViewModelLocator.getInstance().getDebugLogViewModel();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        llm.setStackFromEnd(true);

        adapter = new DebugLogAdapter(getViewModel().getDebugLogEntries());
        entries.setLayoutManager(llm);
        entries.setAdapter(adapter);
        entries.setHasFixedSize(true);

        callback = new StickyOnListChangedCallback(entries);
        getViewModel().getDebugLogEntries().addOnListChangedCallback(callback);
    }

    @Override
    public void onDestroyView() {
        getViewModel().getDebugLogEntries().removeOnListChangedCallback(callback);
        super.onDestroyView();
    }

    public static class StickyOnListChangedCallback extends ObservableList.OnListChangedCallback {

        private RecyclerView recyclerView;

        public StickyOnListChangedCallback(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public void onChanged(ObservableList sender) {
            recyclerView.getAdapter().notifyDataSetChanged();
            stickToLastItem();
        }

        @Override
        public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
            recyclerView.getAdapter().notifyItemRangeChanged(positionStart, itemCount);
            stickToLastItem();
        }

        @Override
        public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
            recyclerView.getAdapter().notifyItemRangeInserted(positionStart, itemCount);
            stickToLastItem();
        }

        @Override
        public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            recyclerView.getAdapter().notifyItemRangeRemoved(fromPosition, itemCount);
            recyclerView.getAdapter().notifyItemRangeInserted(toPosition, itemCount);
            stickToLastItem();
        }

        @Override
        public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
            recyclerView.getAdapter().notifyItemRangeRemoved(positionStart, itemCount);
            stickToLastItem();
        }

        private void stickToLastItem() {
            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        }
    }
}
