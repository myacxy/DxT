package net.myacxy.squinch.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.myacxy.squinch.R;
import net.myacxy.squinch.base.MvvmActivity;
import net.myacxy.squinch.base.SimpleViewModelLocator;
import net.myacxy.squinch.base.ViewModel;
import net.myacxy.squinch.helpers.FragmentHelper;
import net.myacxy.squinch.settings.channelselection.ChannelSelectionFragment;
import net.myacxy.squinch.settings.debuglog.DebugLogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

public class SettingsActivity extends MvvmActivity {

    @BindView(R.id.tb_st_toolbar)
    protected Toolbar toolbar;

    private FragmentHelper<SettingsActivity, SettingsScreen> fragmentHelper;

    @NonNull
    @Override
    protected ViewModel getViewModel() {
        return SimpleViewModelLocator.getInstance().getSettingsViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);

        fragmentHelper = new FragmentHelper<>(R.id.fl_st_container);
        fragmentHelper.changeFragment(this, SettingsScreen.SETTINGS, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fragmentHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fragmentHelper.onRestoreInstanceState(this, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (fragmentHelper.getCurrentFragment()) {
            case SETTINGS:
                finish();
                break;
            default:
                fragmentHelper.changeFragment(this, SettingsScreen.SETTINGS, false);
        }
    }

    @Subscribe
    public void onNavigationEvent(NavigationEvent event) {
        fragmentHelper.changeFragment(this, event.getSettingsScreen(), false);
    }

    public enum SettingsScreen implements FragmentHelper.FragmentInitializer {
        SETTINGS {
            @Override
            public SettingsFragment newInstance() {
                return new SettingsFragment();
            }

            @Override
            public String getTag() {
                return SettingsFragment.TAG;
            }
        },
        CHANNEL_SELECTION {
            @Override
            public ChannelSelectionFragment newInstance() {
                return new ChannelSelectionFragment();
            }

            @Override
            public String getTag() {
                return ChannelSelectionFragment.TAG;
            }
        },
        DEBUG_LOG {
            @Override
            public DebugLogFragment newInstance() {
                return new DebugLogFragment();
            }

            @Override
            public String getTag() {
                return DebugLogFragment.TAG;
            }
        };
    }

    public static class NavigationEvent {
        private SettingsScreen settingsScreen;

        public NavigationEvent(SettingsScreen settingsScreen) {
            this.settingsScreen = settingsScreen;
        }

        public SettingsScreen getSettingsScreen() {
            return settingsScreen;
        }
    }
} // SettingsActivity
