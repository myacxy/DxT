package net.myacxy.dashclock.twitch.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.myacxy.dashclock.twitch.database.TwitchDbHelper;
import net.myacxy.dashclock.twitch.models.TwitchChannel;
import net.myacxy.dashclock.twitch.models.TwitchGame;

import java.util.ArrayList;

public class TgsManager extends AsyncTask<Void, Integer, ArrayList<TwitchGame>> {

    protected ArrayList<TwitchChannel> mChannels;
    protected ArrayList<TwitchGameSearcher> mTgss;
    protected Context mContext;
    protected ProgressDialog mProgressDialog;
    protected AsyncTaskListener mListener;
    private ArrayList<TwitchGameSearcher> finishedTgss;

    public TgsManager(Context context) {
        mContext = context;
        finishedTgss = new ArrayList<>();
        mTgss = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        if(mProgressDialog != null) {
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMax(mChannels.size());
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mProgressDialog != null) mProgressDialog.incrementSecondaryProgressBy(values[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<TwitchGame> games) {
        if (mProgressDialog != null) mProgressDialog.dismiss();
        if (mListener != null) mListener.handleAsyncTaskFinished();
    }

    @Override
    protected ArrayList<TwitchGame> doInBackground(Void... params) {

        // search for the game each channel is playing
        for (final TwitchChannel tc : mChannels) {
            final TwitchGameSearcher tgs = new TwitchGameSearcher(mContext);
            // set result as new game
            tgs.setAsyncTaskListener(new AsyncTaskListener() {
                @Override
                public void handleAsyncTaskFinished() {
                    // retrieve game in question from results
                    for(TwitchGame game : tgs.games) {
                        if (game.name.equals(tc.game.name)) {
                            tc.game = game; break;
                        }
                    }
                }
            });
            mTgss.add(tgs);
            // start task
            tgs.run(tc.game.name);
        }

        while (true) {
            for (TwitchGameSearcher tgs : mTgss) {
                if (tgs.getStatus() == Status.FINISHED && !finishedTgss.contains(tgs)) {
                    if(tgs.games != null) {
                        Log.d(tgs.toString(), String.valueOf(tgs.games.size()));

                        TwitchDbHelper dbHelper = new TwitchDbHelper(mContext);
                        dbHelper.updateOrReplaceGameEntries(tgs.games);

                        publishProgress(1);
                        finishedTgss.add(tgs);
                    }
                }
            }
            if(mProgressDialog != null && !mProgressDialog.isShowing()) {
                    cancel(true);
                    return null;
            } else if (finishedTgss.size() == mTgss.size()) break;
        }
        Log.d("TgsManager", "doInBackground finished");
        return null;
    }

    @Override
    protected void onCancelled() {
        for(TwitchGameSearcher tgs : mTgss) tgs.cancel(true);

        super.onCancelled();
    }

    public void run(ArrayList<TwitchChannel> channels) {
        mChannels = channels;
        executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    public void setAsyncTaskListener(AsyncTaskListener asyncTaskListener) {
        mListener = asyncTaskListener;
    }
} // TtggManager