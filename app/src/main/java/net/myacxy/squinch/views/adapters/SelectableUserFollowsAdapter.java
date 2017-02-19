package net.myacxy.squinch.views.adapters;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.myacxy.retrotwitch.v5.api.users.UserFollow;
import net.myacxy.squinch.R;
import net.myacxy.squinch.databinding.SimpleChannelItemBinding;
import net.myacxy.squinch.helpers.BindingAdapters;
import net.myacxy.squinch.helpers.DataHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SelectableUserFollowsAdapter extends RecyclerView.Adapter<SelectableUserFollowsAdapter.SelectableUserFollowViewHolder> {

    private final DataHelper dataHelper;
    private final List<UserFollow> userFollows;
    private final List<UserFollow> deselectedUserFollows;

    public SelectableUserFollowsAdapter(DataHelper dataHelper) {
        this.dataHelper = dataHelper;
        userFollows = dataHelper.getUserFollows();
        deselectedUserFollows = dataHelper.getDeselectedFollows();
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

    protected class SelectableUserFollowViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.accb_ch_selected)
        protected AppCompatCheckBox selected;

        private SimpleChannelItemBinding mBinding;

        public SelectableUserFollowViewHolder(SimpleChannelItemBinding binding) {
            super(binding.getRoot());
            ButterKnife.bind(this, itemView);
            mBinding = binding;
        }

        public void bind(UserFollow userFollow) {
            mBinding.setUserFollow(userFollow);
            mBinding.executePendingBindings();
            BindingAdapters.loadImage(mBinding.sdvChAvatar, userFollow.getChannel().getLogo());

            selected.setChecked(!deselectedUserFollows.contains(userFollow));
        }

        @OnClick(R.id.rl_ch_item)
        protected void onItemClicked() {
            selected.performClick();
        }

        @OnCheckedChanged(R.id.accb_ch_selected)
        protected void onSelectionChanged(boolean checked) {
            UserFollow userFollow = mBinding.getUserFollow();
            if (checked) {
                if (deselectedUserFollows.contains(userFollow)) {
                    deselectedUserFollows.remove(userFollow);
                    dataHelper.setDeselectedFollows(deselectedUserFollows);
                }
            } else {
                if (!deselectedUserFollows.contains(userFollow)) {
                    deselectedUserFollows.add(userFollow);
                    dataHelper.setDeselectedFollows(deselectedUserFollows);
                }
            }
        }
    }
}
