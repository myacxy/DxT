/**
 * Copyright (c) 2014, Johannes Hoffmann
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.myacxy.dxt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;

import net.myacxy.dxt.database.TwitchDbHelper;
import net.myacxy.dxt.io.AsyncTaskListener;
import net.myacxy.dxt.io.TtggManager;
import net.myacxy.dxt.ui.AbbreviationDialog;
import net.myacxy.dxt.ui.CharLimiterDialog;
import net.myacxy.dxt.ui.FollowingSelectionDialog;
import net.myacxy.dxt.ui.IntervalDialog;
import net.myacxy.dxt.ui.UserNameDialog;
import net.myacxy.dxt.ui.UserVoiceDialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TwitchSettingsActivity extends BaseSettingsActivity
{
    protected SharedPreferences mSharedPreferences;
    protected UserNameDialog userNamePreference;
    protected AbbreviationDialog abbreviationPreference;
    protected FollowingSelectionDialog followingSelectionPreference;
    protected Preference updateGameDbPreference;
    protected Preference donatePreference;
    protected CheckBoxPreference hideEmptyPreference;
    protected IntervalDialog intervalPreference;
    protected CharLimiterDialog charLimiterPreference;
    protected CheckBoxPreference customVisibilityPreference;
    protected MultiSelectListPreference itemCustomizationPreference;
    protected UserVoiceDialog userVoicePreference;

    private Config mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.twitch_white);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // UserVoice
        mConfig = new Config("myacxy.uservoice.com");
        Map<String, String> customFields = new HashMap<>();
        customFields.put("Type", "DashClock Twitch");
        mConfig.setCustomFields(customFields);
        mConfig.setForumId(272219);
        UserVoice.init(mConfig, TwitchSettingsActivity.this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        intervalPreference = (IntervalDialog) findPreference("pref_update_interval");
        followingSelectionPreference =
                (FollowingSelectionDialog) findPreference("pref_following_selection");
        userNamePreference = (UserNameDialog) findPreference("pref_user_name");
        abbreviationPreference = (AbbreviationDialog) findPreference("pref_abbr");
        charLimiterPreference = (CharLimiterDialog) findPreference("pref_char_limiter");
        customVisibilityPreference = (CheckBoxPreference) findPreference("pref_custom_visibility");
        donatePreference = findPreference("pref_donate");
        hideEmptyPreference = (CheckBoxPreference) findPreference("pref_hide_empty");
        updateGameDbPreference = findPreference("pref_game_db_update");
        itemCustomizationPreference = (MultiSelectListPreference)
                findPreference("pref_main_list_item_customization");
        userVoicePreference = (UserVoiceDialog) findPreference("pref_user_voice");

        bindUserNamePreference();
        bindIntervalPreference();
        bindCharLimiterPreference();
        bindSelectionPreference();
        bindAbbreviationsPreference();
        bindGameDbPreference();

        customVisibilityPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new TwitchDbHelper(getApplicationContext()).updatePublishedData();
                return true;
            }
        });

        itemCustomizationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                return false;
            }
        });

        donatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=PX9PJ4USMUWU8";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

        hideEmptyPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean isChecked = ((CheckBoxPreference) preference).isChecked();
                mSharedPreferences.edit()
                        .putBoolean(TwitchExtension.PREF_HIDE_EMPTY, isChecked)
                        .apply();
                new TwitchDbHelper(getApplicationContext()).updatePublishedData();
                return true;
            }
        });

        updateGameDbPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final TtggManager ttggManager = new TtggManager(TwitchSettingsActivity.this, true);
                ttggManager.setAsyncTaskListener(new AsyncTaskListener() {
                    @Override
                    public void handleAsyncTaskFinished() {
                        TwitchDbHelper dbHelper = new TwitchDbHelper(getApplicationContext());

                        int gamesCount = dbHelper.getGames(false).size();
                        updateGameDbPreference.getOnPreferenceChangeListener().onPreferenceChange(
                                updateGameDbPreference, gamesCount);

                        int abbreviatedGamesCount = dbHelper.getGames(true).size();
                        AbbreviationDialog abbrPreference =
                                (AbbreviationDialog) findPreference("pref_abbr");
                        abbrPreference.getOnPreferenceChangeListener()
                                .onPreferenceChange(abbrPreference, abbreviatedGamesCount);

                        new TwitchDbHelper(getApplicationContext()).updatePublishedData();
                    }
                });
                ttggManager.run(500, 100);
                return true;
            }
        });

        userVoicePreference.setListener(new UserVoiceDialog.DialogItemClickedListener() {
            @Override
            public void itemClicked(int position) {
                switch (position) {
                    // Help Center
                    case 0:
                        UserVoice.launchUserVoice(TwitchSettingsActivity.this);
                        break;
                    // Feedback Forum
                    case 1:
                        UserVoice.launchForum(TwitchSettingsActivity.this);
                        break;
                    // Contact Form
                    case 2:
                        UserVoice.launchContactUs(TwitchSettingsActivity.this);
                        break;
                    // Post Idea Form
                    case 3:
                        UserVoice.launchPostIdea(TwitchSettingsActivity.this);
                        break;
                }
            }
        });
    } // onPostCreate

    private void bindIntervalPreference() {

        intervalPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int count = (int) newValue;
                String summary = getResources().getQuantityString(R.plurals.pref_update_interval_summary,
                        count, count);
                preference.setSummary(summary);
                return true;
            }
        });

        int currentValue = mSharedPreferences.getInt(TwitchExtension.PREF_UPDATE_INTERVAL, 15);
        intervalPreference.getOnPreferenceChangeListener().onPreferenceChange(intervalPreference, currentValue);
    } // bindIntervalPreference

    private void bindSelectionPreference() {

        followingSelectionPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Set<String> allChannels = mSharedPreferences.getStringSet(
                        TwitchExtension.PREF_ALL_FOLLOWED_CHANNELS, new HashSet<String>());
                int totalCount = allChannels.size();
                Set<String> selectedChannels = (Set<String>) newValue;
                int selectedCount = selectedChannels.size();
                String summary = getResources()
                        .getQuantityString(R.plurals.pref_following_selection_summary,
                                totalCount, selectedCount, totalCount);
                preference.setSummary(summary);

                return true;
            }
        });

        Set<String> currentValue = mSharedPreferences.getStringSet(
                TwitchExtension.PREF_SELECTED_FOLLOWED_CHANNELS, new HashSet<String>());
        followingSelectionPreference.getOnPreferenceChangeListener().onPreferenceChange(followingSelectionPreference, currentValue);
    } // bindSelectionPreference

    private void bindUserNamePreference() {

        userNamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                TwitchDbHelper dbHelper = new TwitchDbHelper(getApplicationContext());

                //
                preference.setSummary(newValue.toString());

                // reset custom selections
                if(followingSelectionPreference.getOnPreferenceChangeListener() != null)
                {
                    followingSelectionPreference.getOnPreferenceChangeListener().onPreferenceChange(
                            followingSelectionPreference, new HashSet<String>());
                }


                // update games database count
                int gamesCount = dbHelper.getGames(false).size();
                if(updateGameDbPreference.getOnPreferenceChangeListener() != null)
                {
                    updateGameDbPreference.getOnPreferenceChangeListener().onPreferenceChange(
                            updateGameDbPreference, gamesCount);
                }


                // update abbreviation count
                int abbreviatedGamesCount = dbHelper.getGames(true).size();
                if(abbreviationPreference.getOnPreferenceChangeListener() != null)
                {
                    abbreviationPreference.getOnPreferenceChangeListener()
                            .onPreferenceChange(abbreviationPreference, abbreviatedGamesCount);
                }

                return true;
            }
        });

        String currentValue = mSharedPreferences.getString(
                TwitchExtension.PREF_USER_NAME, "test_user1");
        userNamePreference.getOnPreferenceChangeListener().onPreferenceChange(userNamePreference, currentValue);
    } // bindUserNamePreference

    private void bindCharLimiterPreference() {

        charLimiterPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mSharedPreferences.edit()
                        .putInt(TwitchExtension.PREF_CHAR_LIMIT, (int) newValue)
                        .apply();
                preference.setSummary(newValue.toString());
                return true;
            }
        });
        int currentValue = mSharedPreferences.getInt(TwitchExtension.PREF_CHAR_LIMIT, 200);
        charLimiterPreference.getOnPreferenceChangeListener()
                .onPreferenceChange(charLimiterPreference, currentValue);
    } // bindCharLimiterPreference

    private void bindAbbreviationsPreference() {
        abbreviationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int count = (int) newValue;
                String summary = getResources()
                        .getQuantityString(R.plurals.pref_abbr_summary, count, count);
                preference.setSummary(summary);
                mSharedPreferences.edit()
                        .putInt(TwitchExtension.PREF_ABBR_COUNT, count)
                        .apply();
                new TwitchDbHelper(getApplicationContext()).updatePublishedData();
                return true;
            }
        });

        int currentValue = mSharedPreferences.getInt(TwitchExtension.PREF_ABBR_COUNT, 0);
        abbreviationPreference.getOnPreferenceChangeListener()
                .onPreferenceChange(abbreviationPreference, currentValue);
    } // bindAbbreviationsPreference

    private void bindGameDbPreference() {

        updateGameDbPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int count = (int) newValue;
                String summary = getResources()
                        .getQuantityString(R.plurals.pref_game_db_update_summary, count, count);
                mSharedPreferences.edit()
                        .putInt(TwitchExtension.PREF_GAMES_COUNT, count)
                        .apply();
                preference.setSummary(summary);
                return true;
            }
        });

        int currentValue = mSharedPreferences.getInt(TwitchExtension.PREF_GAMES_COUNT, 0);
        updateGameDbPreference.getOnPreferenceChangeListener()
                .onPreferenceChange(updateGameDbPreference, currentValue);
    } // bindGameDbPreference
} // TwitchSettingsActivity
