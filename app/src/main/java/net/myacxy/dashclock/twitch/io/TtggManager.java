package net.myacxy.dashclock.twitch.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.myacxy.dashclock.twitch.database.TwitchDbHelper;
import net.myacxy.dashclock.twitch.models.TwitchGame;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TtggManager extends AsyncTask<Void, Integer, ArrayList<TwitchGame>> {

    protected ArrayList<TwitchTopGamesGetter> mTggs;
    protected WeakReference<Context> mContext;
    protected boolean mShowProgress;
    protected ProgressDialog mProgressDialog;
    protected AsyncTaskListener mListener;
    public ArrayList<TwitchGame> games;
    private int mTotal;
    private int mLimit;

    public TtggManager(Context context, boolean showProgress) {
        mContext = new WeakReference<>(context);
        mShowProgress = showProgress;
        games = new ArrayList<>();
        mTggs = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        if(mShowProgress) {
            mProgressDialog = new ProgressDialog(mContext.get());
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMax(100);
            mProgressDialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mProgressDialog != null) {
            mProgressDialog.incrementProgressBy(100 / mTggs.size());
        }
    }

    @Override
    protected void onPostExecute(ArrayList<TwitchGame> games) {
        if(games != null){
            TwitchDbHelper dbHelper = new TwitchDbHelper(mContext.get());
            dbHelper.insertOrReplaceGameEntries(games);

            if (mListener != null) mListener.handleAsyncTaskFinished();
            if (mProgressDialog != null) mProgressDialog.dismiss();
        }
    }

    @Override
    protected ArrayList<TwitchGame> doInBackground(Void... params) {

        for (int offset = 0; offset < mTotal; offset += mLimit) {
            final TwitchTopGamesGetter tgg = new TwitchTopGamesGetter(mContext.get(), mProgressDialog);
            mTggs.add(tgg);
            tgg.run(mLimit, offset);
        }

        while (true) {
            for (TwitchTopGamesGetter tgg : mTggs) {
                if (tgg.getStatus() == Status.FINISHED) {
                    if(tgg.games != null && !games.contains(tgg.games.get(0))) {
                        games.addAll((tgg.games));
                        publishProgress(1);
                    }
                }
            }
            if (mProgressDialog.getProgress() == 100) break;
            if(!mProgressDialog.isShowing()) {
                cancel(true);
                return null;
            }
        }
        Log.d("TggManager", "doInBackground finished");
        return games;
    }

    @Override
    protected void onCancelled() {
        for(TwitchTopGamesGetter tgg : mTggs) tgg.cancel(true);

        super.onCancelled();
    }

    public void run(int total, int limit) {
        mTotal = total;
        mLimit = limit;
        executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    public void setAsyncTaskListener(AsyncTaskListener asyncTaskListener) {
        mListener = asyncTaskListener;
    }
} // TtggManager