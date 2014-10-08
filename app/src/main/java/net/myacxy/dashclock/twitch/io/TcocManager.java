package net.myacxy.dashclock.twitch.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import net.myacxy.dashclock.twitch.TwitchExtension;
import net.myacxy.dashclock.twitch.models.TwitchChannel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

public class TcocManager extends AsyncTask<Void, Integer, ArrayList<TwitchChannel>>
{
    protected ArrayList<TwitchChannel> mChannels;
    protected ArrayList<TwitchChannelOnlineChecker> mTcocs;
    protected Context mContext;
    protected ProgressDialog mProgressDialog;
    protected AsyncTaskListener mListener;
    protected ArrayList<TwitchChannel> mResults;

    public TcocManager(ArrayList<TwitchChannel> channels, Context context, ProgressDialog progressDialog, AsyncTaskListener listener)
    {
        mChannels = channels;
        mContext = context;
        mProgressDialog = progressDialog;
        mListener = listener;
        mResults = new ArrayList<TwitchChannel>();
        mTcocs = new ArrayList<TwitchChannelOnlineChecker>();
    }

    @Override
    protected void onPreExecute() {
        for(TwitchChannel tc : mChannels)
        {
            final TwitchChannelOnlineChecker onlineChecker =
                    new TwitchChannelOnlineChecker(mContext, mProgressDialog);
            mTcocs.add(onlineChecker);
            onlineChecker.run(tc);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(mProgressDialog != null) mProgressDialog.setMessage(values[0].toString());
    }

    @Override
    protected void onPostExecute(ArrayList<TwitchChannel> channels) {
        if(mListener != null) mListener.handleAsyncTaskFinished();
        if(mProgressDialog != null) mProgressDialog.dismiss();
        // save all followed channels to shared preferences
        saveTwitchChannelsToPreferences(mChannels, TwitchExtension.PREF_ALL_FOLLOWED_CHANNELS);
        // save all followed channels to database
        new TwitchDbHelper(mContext).saveChannels(mChannels);
        // save the time of this update
        saveCurrentTime();
    }

    @Override
    protected ArrayList<TwitchChannel> doInBackground(Void... params) {

        while(true) {
            for(TwitchChannelOnlineChecker tcoc : mTcocs) {
                if(tcoc.getStatus() == Status.FINISHED && !mResults.contains(tcoc.mTwitchChannel))
                    mResults.add(tcoc.mTwitchChannel);
            }
            if(mResults.size() == mChannels.size()) break;
        }
        Log.d("OnlineCheckerManager", "doInBackground finished");
        return mResults;
    }

    @Override
    protected void onCancelled() {
        for(AsyncTask task : mTcocs) task.cancel(true);
        super.onCancelled();
    }

    /**
     * Saves the display names of the provided TwitchChannels to the Shared Preferences as a Set
     * of Strings.
     *
     * @param twitchChannels List of TwitchChannels being saved
     * @param key The name of the preference to modify.
     */
    public void saveTwitchChannelsToPreferences(ArrayList<TwitchChannel> twitchChannels, String key)
    {
        // initialize
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        // retrieve display names
        HashSet<String> values = new HashSet<String>();
        for(TwitchChannel tc : twitchChannels) {
            values.add(tc.displayName);
        }
        // save
        editor.putStringSet(key, values);
        editor.apply();
    } // saveTwitchChannelsToPreferences

    /**
     * Saves the current system time in milliseconds to the shared preferences so that it can later
     * be used to limit the updates in a adjustable time interval.
     */
    public void saveCurrentTime()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        long currentMillis = Calendar.getInstance().getTimeInMillis();
        editor.putLong(TwitchExtension.PREF_LAST_UPDATE, currentMillis);
        editor.apply();
    } // saveCurrentTime

    /**
     * Checks the Shared Preferences for the time of the last update.
     *
     * @return  returns true if channels were updated within the update interval
     *          return false if channels were updated later than the update interval
     */
    public static boolean checkRecentlyUpdated(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long lastUpdate = sp.getLong(TwitchExtension.PREF_LAST_UPDATE, 0);
        // calculate difference in minutes
        double difference = (Calendar.getInstance().getTimeInMillis() - lastUpdate) / 60000f;
        int updateInterval = sp.getInt(TwitchExtension.PREF_UPDATE_INTERVAL, 5);
        Log.d("TwitchUserFollowsGetter", "Difference=" + difference);
        return difference < updateInterval;
    }
}