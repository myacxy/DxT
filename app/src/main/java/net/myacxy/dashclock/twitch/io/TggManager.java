package net.myacxy.dashclock.twitch.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

public class TggManager extends AsyncTask<Void, Integer, ArrayList<String>> {

    protected ArrayList<TwitchGameGetter> mTggs;
    protected Context mContext;
    protected ProgressDialog mProgressDialog;
    protected AsyncTaskListener mListener;
    protected ArrayList<String> mResults;

    public TggManager(Context context, ProgressDialog progressDialog, AsyncTaskListener listener) {
        mContext = context;
        mProgressDialog = progressDialog;
        mListener = listener;
        mResults = new ArrayList<String>();
        mTggs = new ArrayList<TwitchGameGetter>();
    }

    @Override
    protected void onPreExecute() {
        for (int offset = 0; offset < 500; offset += 100) {
            final TwitchGameGetter tgg =
                    new TwitchGameGetter(mContext, mProgressDialog);
            mTggs.add(tgg);
            tgg.run(100, offset);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mProgressDialog != null) mProgressDialog.setMessage(values[0].toString());
    }

    @Override
    protected void onPostExecute(ArrayList<String> games) {
        if (mProgressDialog != null) mProgressDialog.dismiss();
        // save all followed channels to database
//        new TwitchDbHelper(mContext).updateOrReplaceGameEntry();
        if (mListener != null) mListener.handleAsyncTaskFinished();
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        while (true) {
            for (TwitchGameGetter tgg : mTggs) {
                if (tgg.getStatus() == Status.FINISHED && !mResults.contains(tgg))
                    mResults.add("");
            }
            if (mResults.size() == mTggs.size()) break;
        }
        Log.d("OnlineCheckerManager", "doInBackground finished");
        return mResults;
    }
}