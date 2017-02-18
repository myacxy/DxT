package net.myacxy.squinch.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import net.myacxy.squinch.SimpleViewModelLocator;
import net.myacxy.squinch.databinding.SettingsFragmentBinding;
import net.myacxy.squinch.views.activities.SettingsActivity;

import org.greenrobot.eventbus.EventBus;

public class SettingsFragment extends Fragment {

    public static final String TAG = SettingsFragment.class.getSimpleName();

    private SettingsFragmentBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } // onCreate

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = SettingsFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.setViewModel(SimpleViewModelLocator.getInstance().getSettingsViewModel());

        mBinding.metStUserName.setOnKeyListener((v, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_ENTER) {
                Toast.makeText(getContext(), mBinding.metStUserName.getText(), Toast.LENGTH_SHORT).show();
                mBinding.getViewModel().onUserNameChanged(mBinding.metStUserName.getText().toString());
                return true;
            }
            return false;
        });

        mBinding.metStUserName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Toast.makeText(getContext(), v.getText(), Toast.LENGTH_SHORT).show();
                mBinding.getViewModel().onUserNameChanged(v.getText().toString());
                return true;
            }
            return false;
        });

        mBinding.rlStHideExtension.setOnClickListener(v -> mBinding.swStHideExtension.performClick());
        mBinding.swStHideExtension.setOnCheckedChangeListener((compoundButton, checked) -> mBinding.getViewModel().onHideExtensionChanged(checked));
        mBinding.rlStUserName.setOnClickListener(v -> mBinding.metStUserName.requestFocus());
        mBinding.rlStChannelSelection.setOnClickListener(v -> goToChannelSelection());
    }

    @Override
    public void onDestroy() {
        mBinding.unbind();
        super.onDestroy();
    }

    private void goToChannelSelection() {
        EventBus.getDefault().post(new SettingsActivity.NavigationEvent(SettingsActivity.SettingsScreen.CHANNEL_SELECTION));
    }
}
