package net.myacxy.ditch.views.activities;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.orhanobut.logger.Logger;

import net.myacxy.ditch.R;
import net.myacxy.ditch.SimpleViewModelLocator;
import net.myacxy.ditch.databinding.ActivitySettingsBinding;
import net.myacxy.retrotwitch.utils.StringUtil;

public class SettingsActivity extends AppCompatActivity
{
    private ActivitySettingsBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        mBinding.setViewModel(SimpleViewModelLocator.getInstance().getSettingsViewModel());

        mBinding.metStUserName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE)
            {
                Toast.makeText(SettingsActivity.this, v.getText(), Toast.LENGTH_SHORT).show();
                mBinding.getViewModel().onChangeUserName(v.getText().toString());
                return true;
            }
            return false;
        });

        mBinding.getViewModel().userNameError.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback()
        {
            @Override
            public void onPropertyChanged(Observable observable, int i)
            {
                mBinding.metStUserName.setError(mBinding.getViewModel().userNameError.get());
            }
        });

        GenericDraweeHierarchy hierarchy = mBinding.sdvStAvatar.getHierarchy();
        hierarchy.setRoundingParams(RoundingParams.asCircle());
        mBinding.sdvStAvatar.setHierarchy(hierarchy);
        mBinding.getViewModel().userAvatarUrl.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback()
        {
            @Override
            public void onPropertyChanged(Observable observable, int i)
            {
                String url = mBinding.getViewModel().userAvatarUrl.get();
                Toast.makeText(SettingsActivity.this, mBinding.getViewModel().userAvatarUrl.get(), Toast.LENGTH_SHORT).show();
                Logger.t(1).v(url != null ? url : "");

                if (!StringUtil.isBlank(url))
                {
                    mBinding.sdvStAvatar.setImageURI(Uri.parse(url));
                } else {
                    mBinding.sdvStAvatar.setImageURI(Uri.EMPTY);
                }
            }
        });
    }
} // SettingsActivity
