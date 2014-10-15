package net.myacxy.dashclock.twitch.io;

import android.content.Context;

import net.myacxy.dashclock.twitch.database.TwitchDbHelper;
import net.myacxy.dashclock.twitch.models.TwitchGame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TwitchGameSearcher extends JsonGetter {

    public ArrayList<TwitchGame> games;
    public TwitchGame game;
    private String searchQuery;
    public TwitchGameSearcher(Context context) {
        super(context, null);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return super.doInBackground(params);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        JSONArray gamesJson = null;
        try {
            gamesJson = jsonObject.getJSONArray("games");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(gamesJson == null) cancel(true);
        else this.games = parseJson(gamesJson);

        TwitchDbHelper dbHelper = new TwitchDbHelper(mContext);

        if(games.size() == 0) {
            TwitchGame tg = new TwitchGame(searchQuery, null);
            tg.id = dbHelper.insertOrReplaceGameEntry(tg);
            games.add(tg);
        } else {
            // retrieve game in question from results
            for(TwitchGame tg : games) {
                tg.id = dbHelper.insertOrReplaceGameEntry(tg);
            }
        }


        if(mListener != null) mListener.handleAsyncTaskFinished();
    }

    public void run(String searchQuery) {
        this.searchQuery = searchQuery;
        String url = String.format("https://api.twitch.tv/kraken/search/games?q=%s&type=suggest",
                searchQuery);
        executeOnExecutor(THREAD_POOL_EXECUTOR, url);
    }

    @Override
    public String toString() {
        return super.toString() + " " + searchQuery;
    }

    protected ArrayList<TwitchGame> parseJson(JSONArray jsonGames)
    {
        // initialize
        ArrayList<TwitchGame> games = new ArrayList<TwitchGame>();

        for (int i = 0; i < jsonGames.length(); i++)
        {
            JSONObject game = null;
            // get channel from array
            try {
                game = jsonGames.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // parse json to TwitchGame
            TwitchGame tg = new TwitchGame(game, 0, 0);
            // add game to list
            games.add(tg);
        }
        return games;
    } // parseJsonObject
} // TwitchGameSearcher
