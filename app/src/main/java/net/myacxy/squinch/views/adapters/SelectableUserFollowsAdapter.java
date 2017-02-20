package net.myacxy.squinch.views.adapters;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

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
    private final List<Long> deselectedChannelIds;

    public SelectableUserFollowsAdapter(DataHelper dataHelper) {
        this.dataHelper = dataHelper;
        userFollows = dataHelper.getUserFollows();
        deselectedChannelIds = dataHelper.getDeselectedChannelIds();
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

        @BindView(R.id.sdv_ch_avatar)
        protected SimpleDraweeView avatar;

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

            selected.setChecked(!deselectedChannelIds.contains(userFollow.getChannel().getId()));
        }

        @OnClick(R.id.rl_ch_item)
        protected void onItemClicked() {
            selected.performClick();
        }

        @OnCheckedChanged(R.id.accb_ch_selected)
        protected void onSelectionChanged(boolean checked) {
            long id = mBinding.getUserFollow().getChannel().getId();
            if (checked) {
                if (deselectedChannelIds.contains(id)) {
                    deselectedChannelIds.remove(id);
                    dataHelper.setDeselectedChannelIds(deselectedChannelIds);
                }
            } else {
                if (!deselectedChannelIds.contains(id)) {
                    deselectedChannelIds.add(id);
                    dataHelper.setDeselectedChannelIds(deselectedChannelIds);
                }
            }
        }
    }
}
