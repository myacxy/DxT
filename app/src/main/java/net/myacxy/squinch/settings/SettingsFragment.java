package net.myacxy.squinch.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import net.myacxy.squinch.R;
import net.myacxy.squinch.base.MvvmFragment;
import net.myacxy.squinch.base.SimpleViewModelLocator;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsFragment extends MvvmFragment {

    public static final String TAG = SettingsFragment.class.getSimpleName();

    @Inject
    SimpleViewModelLocator simpleViewModelLocator;

    @BindView(R.id.sw_st_hide_extension)
    protected SwitchCompat hideExtensionSwitch;
    @BindView(R.id.til_st_user_name)
    protected TextInputLayout userNameText;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected SettingsViewModel getViewModel() {
        return simpleViewModelLocator.getSettingsViewModel();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewModel().refresh();
    }

    @OnCheckedChanged(R.id.sw_st_hide_extension)
    protected void onHideExtensionCheckedChanged(boolean checked) {
        getViewModel().onHideExtensionChanged(checked);
    }

    @OnClick(R.id.rl_st_hide_extension)
    protected void onHideExtensionGroupClicked() {
        hideExtensionSwitch.performClick();
    }

    @OnClick(R.id.rl_st_user_name)
    protected void onUserNameGroupClicked() {
        if (!userNameText.hasFocus()) {
            userNameText.requestFocus();
            userNameText.getEditText().setSelection(userNameText.getEditText().getText().length());
        }
    }

    @OnClick(R.id.rl_st_channel_selection)
    protected void onChannelSelectionGroupClicked() {
        EventBus.getDefault()
                .post(new SettingsActivity.NavigationEvent(SettingsActivity.SettingsScreen.CHANNEL_SELECTION));
    }

    @OnClick(R.id.rl_st_debug_log)
    protected void onDebugLogGroupClicked() {
        EventBus.getDefault()
                .post(new SettingsActivity.NavigationEvent(SettingsActivity.SettingsScreen.DEBUG_LOG));
    }
}
