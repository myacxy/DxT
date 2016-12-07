package net.myacxy.squinch.views.adapters;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import net.myacxy.retrotwitch.models.UserFollow;
import net.myacxy.retrotwitch.utils.StringUtil;
import net.myacxy.squinch.databinding.SimpleChannelItemBinding;

public class SelectableUserFollowsAdapter extends RecyclerView.Adapter<SelectableUserFollowsAdapter.SelectableUserFollowViewHolder>
{
    private ObservableArrayList<UserFollow> mUserFollows = new ObservableArrayList<>();

    public SelectableUserFollowsAdapter(ObservableArrayList<UserFollow> userFollows)
    {
        mUserFollows = userFollows;
        mUserFollows.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<UserFollow>>()
        {
            @Override
            public void onChanged(ObservableList<UserFollow> userFollows)
            {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<UserFollow> userFollows, int i, int i1)
            {

            }

            @Override
            public void onItemRangeInserted(ObservableList<UserFollow> userFollows, int i, int i1)
            {

            }

            @Override
            public void onItemRangeMoved(ObservableList<UserFollow> userFollows, int i, int i1, int i2)
            {

            }

            @Override
            public void onItemRangeRemoved(ObservableList<UserFollow> userFollows, int i, int i1)
            {

            }
        });
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

            if (!StringUtil.isBlank(userFollow.channel.logo))
            {
                Uri uri = Uri.parse(userFollow.channel.logo);
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setProgressiveRenderingEnabled(true)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(mBinding.sdvChAvatar.getController())
                        .build();
                mBinding.sdvChAvatar.setController(controller);
                mBinding.sdvChAvatar.getHierarchy()
                        .setRoundingParams(
                                RoundingParams.asCircle()
                                        .setBorder(Color.parseColor("#ffffff"), 1.0f)
                        );
            } else
            {
                mBinding.sdvChAvatar.setImageURI(Uri.EMPTY);
            }
        }
    }
}
