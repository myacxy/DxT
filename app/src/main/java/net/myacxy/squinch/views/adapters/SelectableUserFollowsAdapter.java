package net.myacxy.squinch.views.adapters;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import net.myacxy.retrotwitch.v5.api.channels.SimpleChannel;
import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.squinch.R;
import net.myacxy.squinch.databinding.SimpleChannelItemBinding;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTouch;

public class SelectableUserFollowsAdapter extends RecyclerView.Adapter<SelectableUserFollowsAdapter.SelectableUserFollowViewHolder> {

    private final List<UserFollow> userFollows;
    private final List<Long> deselectedChannelIds;
    private final UserFollowsDataListener dataListener;
    private final UserFollowsViewListener viewListener;

    public SelectableUserFollowsAdapter(List<UserFollow> userFollows, List<Long> deselectedChannelIds, UserFollowsDataListener dataListener, UserFollowsViewListener viewListener) {
        this.userFollows = userFollows;
        this.deselectedChannelIds = deselectedChannelIds;
        this.dataListener = dataListener;
        this.viewListener = viewListener;
    }

    @Override
    public SelectableUserFollowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SimpleChannelItemBinding binding = SimpleChannelItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SelectableUserFollowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(SelectableUserFollowViewHolder holder, int position) {
        holder.bind(userFollows.get(position));
    }

    @Override
    public int getItemCount() {
        return userFollows.size();
    }

    public interface UserFollowsDataListener {
        void onChannelSelectionChanged(SimpleChannel channel, boolean selected);

        void onChannelMoved(int from, int to);
    }

    public interface UserFollowsViewListener {
        void onStartDrag(RecyclerView.ViewHolder holder);

        void onChannelMoved(int from, int to);
    }

    public static class TouchCallback extends ItemTouchHelper.Callback {

        private UserFollowsDataListener dataListener;
        private UserFollowsViewListener viewListener;

        public TouchCallback(UserFollowsDataListener dataListener, UserFollowsViewListener viewListener) {
            this.dataListener = dataListener;
            this.viewListener = viewListener;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            dataListener.onChannelMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            viewListener.onChannelMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }
    }

    protected class SelectableUserFollowViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.accb_ch_selected)
        protected AppCompatCheckBox selected;

        private SimpleChannelItemBinding mBinding;
        private GestureDetector gestureDetector;

        public SelectableUserFollowViewHolder(SimpleChannelItemBinding binding) {
            super(binding.getRoot());
            ButterKnife.bind(this, itemView);
            mBinding = binding;
            gestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    viewListener.onStartDrag(SelectableUserFollowViewHolder.this);
                }
            });
        }

        public void bind(UserFollow userFollow) {
            mBinding.setUserFollow(userFollow);
            mBinding.executePendingBindings();

            selected.setChecked(!deselectedChannelIds.contains(userFollow.getChannel().getId()));
        }

        @OnTouch(R.id.rl_ch_item)
        protected boolean onTouch(MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        @OnClick(R.id.rl_ch_item)
        protected void onItemClicked() {
            selected.performClick();
        }

        @OnCheckedChanged(R.id.accb_ch_selected)
        protected void onSelectionChanged(boolean checked) {
            dataListener.onChannelSelectionChanged(mBinding.getUserFollow().getChannel(), checked);
        }
    }
}
