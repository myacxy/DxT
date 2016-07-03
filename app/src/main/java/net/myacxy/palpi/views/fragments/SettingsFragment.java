package net.myacxy.palpi.views.fragments;

import android.databinding.Observable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.orhanobut.logger.Logger;

import net.myacxy.palpi.R;
import net.myacxy.palpi.SimpleViewModelLocator;
import net.myacxy.palpi.databinding.FragmentSettingsBinding;
import net.myacxy.retrotwitch.models.User;
import net.myacxy.retrotwitch.utils.StringUtil;

public class SettingsFragment extends Fragment
{
    public static final String TAG = SettingsFragment.class.getSimpleName();

    private FragmentSettingsBinding mBinding;

    private Observable.OnPropertyChangedCallback mCallback = new Observable.OnPropertyChangedCallback()
    {
        @Override
        public void onPropertyChanged(Observable observable, int i)
        {
            if(observable == mBinding.getViewModel().user) {
                Logger.t(1).d(String.valueOf(isAdded()));
                User user = mBinding.getViewModel().user.get();
                if (user != null)
                {
                    Toast.makeText(getContext(), user.logo, Toast.LENGTH_SHORT).show();
                    Logger.t(1).v(user.logo != null ? user.logo : "");

                    if (!StringUtil.isBlank(user.logo))
                    {
                        Uri uri = Uri.parse(user.logo);
                        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                                .setProgressiveRenderingEnabled(true)
                                .build();
                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(mBinding.sdvStAvatar.getController())
                                .build();
                        mBinding.sdvStAvatar.setController(controller);
                        return;
                    }
                }
                mBinding.sdvStAvatar.setImageURI(Uri.EMPTY);
            } else if (observable == mBinding.getViewModel().userError) {
                mBinding.metStUserName.setError(mBinding.getViewModel().userError.get());
            }
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    } // onCreate

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mBinding.setViewModel(SimpleViewModelLocator.getInstance().getSettingsViewModel());

        mBinding.metStUserName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE)
            {
                Toast.makeText(getContext(), v.getText(), Toast.LENGTH_SHORT).show();
                mBinding.getViewModel().onChangeUserName(v.getText().toString());
                return true;
            }
            return false;
        });

        mBinding.getViewModel().userError.addOnPropertyChangedCallback(mCallback);

        GenericDraweeHierarchy hierarchy = mBinding.sdvStAvatar.getHierarchy();
        RoundingParams roundingParams = RoundingParams.asCircle()
                .setBorderColor(ContextCompat.getColor(getContext(), R.color.base12))
                .setBorderWidth(getResources().getDimension(R.dimen.divider));
        hierarchy.setRoundingParams(roundingParams);
        mBinding.sdvStAvatar.setHierarchy(hierarchy);
        mBinding.getViewModel().user.addOnPropertyChangedCallback(mCallback);

        mBinding.rlStHideExtension.setOnClickListener(v -> mBinding.swStHideExtension.performClick());
        mBinding.rlStUserName.setOnClickListener(v -> mBinding.metStUserName.requestFocus());
        mBinding.rlStChannelSelection.setOnClickListener(v -> {
            Fragment fragment = getFragmentManager().findFragmentByTag(ChannelSelectionFragment.TAG);
            if (fragment == null)
            {
                fragment = new ChannelSelectionFragment();
            }
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_st_container, fragment, ChannelSelectionFragment.TAG)
                    .commit();
        });
    }

    @Override
    public void onDestroy()
    {
        mBinding.getViewModel().user.removeOnPropertyChangedCallback(mCallback);
        mBinding.getViewModel().userError.removeOnPropertyChangedCallback(mCallback);
        mBinding.unbind();
        super.onDestroy();
    }
}
