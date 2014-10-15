package net.myacxy.dashclock.twitch.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.myacxy.dashclock.twitch.database.TwitchDbHelper;
import net.myacxy.dashclock.twitch.models.TwitchGame;

import java.util.ArrayList;

public class TtggManager extends AsyncTask<Void, Integer, ArrayList<TwitchGame>> {

    protected ArrayList<TwitchTopGamesGetter> mTggs;
    protected Context mContext;
    protected ProgressDialog mProgressDialog;
    protected AsyncTaskListener mListener;
    public ArrayList<TwitchGame> games;
    private int mTotal;
    private int mLimit;

    public TtggManager(Context context, boolean showProgress) {
        mContext = context;
        if(showProgress) mProgressDialog = new ProgressDialog(context);
        games = new ArrayList<>();
        mTggs = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        if(mProgressDialog != null) {
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMax(mTotal / mLimit);
            mProgressDialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mProgressDialog != null) {
            mProgressDialog.incrementProgressBy(values[0]);
        }
    }

    @Override
    protected void onPostExecute(ArrayList<TwitchGame> games) {
        if(games != null){
            TwitchDbHelper dbHelper = new TwitchDbHelper(mContext);
            dbHelper.insertOrReplaceGameEntries(games);

            if (mProgressDialog != null) mProgressDialog.dismiss();
            if (mListener != null) mListener.handleAsyncTaskFinished();
        }
    }

    @Override
    protected ArrayList<TwitchGame> doInBackground(Void... params) {

        for (int offset = 0; offset < mTotal; offset += mLimit) {
            final TwitchTopGamesGetter tgg = new TwitchTopGamesGetter(mContext);
            mTggs.add(tgg);
            tgg.run(mLimit, offset);
        }

        while (true) {
            for (TwitchTopGamesGetter tgg : mTggs) {
                if (tgg.getStatus() == Status.FINISHED && !games.contains(tgg.games.get(0))) {
                    games.addAll((tgg.games));
                    publishProgress(1);
                }
            }
            if (games.size() == mTotal) break;
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