package net.myacxy.squinch.helpers;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

public class FragmentHelper<A extends AppCompatActivity, FI extends FragmentHelper.FragmentInitializer>
{
    private static final String EXTRA_CURRENT_FRAGMENT = "current.fragment";

    private final int fragmentContainer;
    private FI currentFragment;

    public FragmentHelper(@IdRes int fragmentContainer)
    {
        this.fragmentContainer = fragmentContainer;
    }

    public void changeFragment(A activity, FI fragment, boolean tryToReuse)
    {
        Fragment newFragment = null;
        String tag = fragment.getTag();

        if (tryToReuse)
        {
            newFragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        }

        if (newFragment == null)
        {
            newFragment = fragment.newInstance();
        }

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainer, newFragment, tag)
                .commit();

        currentFragment = fragment;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(EXTRA_CURRENT_FRAGMENT, currentFragment);
    }

    public void onRestoreInstanceState(A activity, Bundle savedInstanceBundle) {
        // noinspection unchecked
        currentFragment = (FI) savedInstanceBundle.getSerializable(EXTRA_CURRENT_FRAGMENT);
        changeFragment(activity, currentFragment, true);
    }

    public FI getCurrentFragment() {
        return currentFragment;
    }

    public interface FragmentInitializer extends Serializable
    {
        Fragment newInstance();

        String getTag();
    }
}
