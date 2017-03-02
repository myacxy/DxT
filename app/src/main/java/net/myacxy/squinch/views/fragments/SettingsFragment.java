package net.myacxy.squinch.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.EditText;

import net.myacxy.squinch.R;
import net.myacxy.squinch.SimpleViewModelLocator;
import net.myacxy.squinch.viewmodels.SettingsViewModel;
import net.myacxy.squinch.views.activities.SettingsActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsFragment extends MvvmFragment {

    public static final String TAG = SettingsFragment.class.getSimpleName();

    @BindView(R.id.sw_st_hide_extension)
    protected SwitchCompat hideExtensionSwitch;
    @BindView(R.id.met_st_user_name)
    protected EditText userNameText;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected SettingsViewModel getViewModel() {
        return SimpleViewModelLocator.getInstance().getSettingsViewModel();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null && getViewModel().settings.user.get() != null) {
            userNameText.setText(getViewModel().settings.user.get().getName());
        }
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
            userNameText.setSelection(userNameText.getText().length());
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
