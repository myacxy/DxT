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

package net.myacxy.dashclock.twitch.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.myacxy.dashclock.twitch.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Network Access cannot be made on the UI / Main Thread and therefore an AsyncTask needs to be
 * executed in order to retrieve a JSON Object via HTTP. Upon creating a JSONGetter a Progress
 * Dialog will be shown until the task has finished working in the background.
 */
public class JsonGetter extends AsyncTask<String, String, JSONObject>
{
    // Main Activity's context
    protected Context mContext;
    // Dialog displaying the progress for the async task getting json from http
    protected ProgressDialog mProgressDialog;
    protected String mProgressDialogMessage;

    protected AsyncTaskListener mListener;

    public JsonGetter(){
        // default constructor
    }

    public void setAsyncTaskListener(AsyncTaskListener listener)
    {
        mListener = listener;
    }


    protected void asyncTaskFinished()
    {
        if(mListener != null) {
            mListener.handleAsyncTaskFinished();
        }
    }
    /**
     * The activity's context is necessary in order to display the progress dialog.
     *
     * @param context activity from which the class has been called
     */
    public JsonGetter(Context context, ProgressDialog progressDialog)
    {
        mContext = context;
        mProgressDialog = progressDialog;
        mProgressDialogMessage = mContext.getResources().getString(
                R.string.pref_following_selection_progress_title);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // initialize dialog before trying to fetch the data
        if(mProgressDialog != null) {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage(mProgressDialogMessage);
            mProgressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        // close dialog after having fetched the data
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            // display Toast on error
            if (jsonObject == null) {
                Toast.makeText(mContext, mContext.getResources().getString(
                        R.string.pref_following_selection_progress_fail), Toast.LENGTH_LONG).show();
            }
        }
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        asyncTaskFinished();
    }

    @Override
    protected void onCancelled() {
        Log.d("JsonGetter", "Cancelled.");
        super.onCancelled();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return getJsonFromURL(params[0]);
    }

    /**
     * Retrieves JSON data from an URL via HTTP
     *
     * @see <a href="http://goo.gl/opGukX">Antoine Rivière</a>
     * @param url String representing the full URL
     * @return JSONObject of url's target location
     */
    protected JSONObject getJsonFromURL(String url)
    {
        //initialize
        JSONObject jsonObject = null;
        InputStream is = null;
        String jsonString = "";

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Accept", "application/json");
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch(Exception e) {
            e.printStackTrace();
        }

        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString=sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * Retrieves the value of a String from given JSON Data
     *
     * @param channelObject JSON Data of a Twitch Channel
     * @param name Name of the String that needs to be retrieved
     * @return Value of the sought Object
     */
    public String getString(JSONObject channelObject, String name)
    {
        try {
            if(channelObject.get(name) != null)
            {
                return channelObject.getString(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Retrieves the value of an Integer from given JSON Data
     *
     * @param channelObject JSON Data of a Twitch Channel
     * @param name Name of the Integer that needs to be retrieved
     * @return Value of the sought Object
     */
    public int getInt(JSONObject channelObject, String name)
    {
        try {
            if(channelObject.get(name) != null)
            {
                return channelObject.getInt(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
} //JSONGetter