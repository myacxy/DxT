package net.myacxy.squinch.views.adapters;

import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.myacxy.retrotwitch.models.UserFollow;
import net.myacxy.squinch.databinding.SimpleChannelItemBinding;
import net.myacxy.squinch.helpers.CustomBindings;

public class SelectableUserFollowsAdapter extends RecyclerView.Adapter<SelectableUserFollowsAdapter.SelectableUserFollowViewHolder>
{
    private ObservableArrayList<UserFollow> mUserFollows = new ObservableArrayList<>();

    public SelectableUserFollowsAdapter(ObservableArrayList<UserFollow> userFollows)
    {
        mUserFollows = userFollows;
    }

    @Override
    public SelectableUserFollowViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        SimpleChannelItemBinding binding = SimpleChannelItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SelectableUserFollowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(SelectableUserFollowViewHolder holder, int position)
    {
        holder.bind(mUserFollows.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mUserFollows.size();
    }

    protected static class SelectableUserFollowViewHolder extends RecyclerView.ViewHolder
    {

        private SimpleChannelItemBinding mBinding;

        public SelectableUserFollowViewHolder(SimpleChannelItemBinding binding)
        {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(UserFollow userFollow)
        {
            mBinding.setUserFollow(userFollow);
            mBinding.executePendingBindings();
            mBinding.getRoot().setOnClickListener(v -> mBinding.accbChSelected.setChecked(!mBinding.accbChSelected.isChecked()));
            CustomBindings.loadImage(mBinding.sdvChAvatar, userFollow.channel.logo);
        }
    }
}
