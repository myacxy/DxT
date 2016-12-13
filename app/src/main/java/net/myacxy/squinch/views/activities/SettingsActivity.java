package net.myacxy.squinch.views.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.myacxy.squinch.R;
import net.myacxy.squinch.databinding.SettingsActivityBinding;
import net.myacxy.squinch.helpers.FragmentHelper;
import net.myacxy.squinch.views.fragments.ChannelSelectionFragment;
import net.myacxy.squinch.views.fragments.SettingsFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SettingsActivity extends AppCompatActivity
{
    private SettingsActivityBinding binding;
    private FragmentHelper<SettingsActivity, SettingsScreen> fragmentHelper;

    @Override
    protected void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        setSupportActionBar(binding.tbStToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentHelper = new FragmentHelper<>(R.id.fl_st_container);
        fragmentHelper.changeFragment(this, SettingsScreen.SETTINGS, true);
    } // onCreate

    @Override
    protected void onStop()
    {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        binding.unbind();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        fragmentHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        fragmentHelper.onRestoreInstanceState(this, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        switch (fragmentHelper.getCurrentFragment())
        {
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

    public enum SettingsScreen implements FragmentHelper.FragmentInitializer
    {
        SETTINGS
                {
                    @Override
                    public SettingsFragment newInstance()
                    {
                        return new SettingsFragment();
                    }

                    @Override
                    public String getTag()
                    {
                        return SettingsFragment.TAG;
                    }
                },
        CHANNEL_SELECTION
                {
                    @Override
                    public ChannelSelectionFragment newInstance()
                    {
                        return new ChannelSelectionFragment();
                    }

                    @Override
                    public String getTag()
                    {
                        return ChannelSelectionFragment.TAG;
                    }
                };
    }

    public static class NavigationEvent {
        private SettingsScreen settingsScreen;

        public NavigationEvent(SettingsScreen settingsScreen)
        {
            this.settingsScreen = settingsScreen;
        }

        public SettingsScreen getSettingsScreen()
        {
            return settingsScreen;
        }
    }
} // SettingsActivity
