package net.myacxy.ditch.views.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import net.myacxy.ditch.R;
import net.myacxy.ditch.views.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SettingsFragment.TAG);
        if(fragment == null) {
            fragment = new SettingsFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_st_container, fragment, SettingsFragment.TAG)
                .commit();
    } // onCreate
} // SettingsActivity
