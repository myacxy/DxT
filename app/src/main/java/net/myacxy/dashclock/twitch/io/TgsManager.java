package net.myacxy.dashclock.twitch.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.myacxy.dashclock.twitch.models.TwitchChannel;
import net.myacxy.dashclock.twitch.models.TwitchGame;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TgsManager extends AsyncTask<Void, Integer, ArrayList<TwitchGame>> {

    /**
     * list of all channels whose games have to be processed
     */
    protected ArrayList<TwitchChannel> mChannels;
    /**
     * list of all search tasks processing each channel's game
     */
    protected ArrayList<TwitchGameSearcher> mTgss;
    /**
     * search tasks that have finished processing
     */
    private ArrayList<TwitchGameSearcher> finishedTgss;
    protected WeakReference<Context> mContext;
    protected ProgressDialog mProgressDialog;
    private boolean mShowProgress;
    protected AsyncTaskListener mListener;

    public TgsManager(Context context, boolean showProgress) {
        mContext = new WeakReference<>(context);
        mShowProgress = showProgress;
        finishedTgss = new ArrayList<>();
        mTgss = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        // prepare progress dialog
        if(mShowProgress) {
            mProgressDialog = new ProgressDialog(mContext.get());
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMax(mChannels.size());
            mProgressDialog.show();
        }

        // search for the game each channel is playing
        for (final TwitchChannel tc : mChannels) {
            final TwitchGameSearcher tgs = new TwitchGameSearcher(mContext.get(), mShowProgress);
            mTgss.add(tgs);
            // set result as new game
            tgs.setAsyncTaskListener(new AsyncTaskListener() {
                @Override
                public void handleAsyncTaskFinished() {
                    tc.game = tgs.result;
                }
            });
            // start task
            tgs.run(tc.game.name);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mProgressDialog != null) mProgressDialog.incrementProgressBy(values[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<TwitchGame> games) {
        if (mProgressDialog != null) mProgressDialog.dismiss();
        if (mListener != null) mListener.handleAsyncTaskFinished();
    }

    @Override
    protected ArrayList<TwitchGame> doInBackground(Void... params) {

        // loop until each task finished
        while (true) {
            for (TwitchGameSearcher tgs : mTgss) {
                // new task finished
                if (tgs.getStatus() == Status.FINISHED && !finishedTgss.contains(tgs)) {
                    Log.d(tgs.toString(), String.valueOf(tgs.searchResults.size()));
                    // update progress
                    publishProgress(1);
                    finishedTgss.add(tgs);
                }
            }
            // cancel everything of progress dialog was closed by user
            if(mProgressDialog != null && !mProgressDialog.isShowing()) {
                cancel(true);
                return null;
            }
            // exit if every task has finished processing
            else if (finishedTgss.size() == mTgss.size()) break;
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
        executeOnExecutor(SERIAL_EXECUTOR);
    }

    public void setAsyncTaskListener(AsyncTaskListener asyncTaskListener) {
        mListener = asyncTaskListener;
    }
} // TtggManager