package net.myacxy.dxt.io;

import android.app.ProgressDialog;
import android.content.Context;

import net.myacxy.dxt.database.TwitchDbHelper;
import net.myacxy.dxt.models.TwitchGame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TwitchGameSearcher extends JsonGetter {

    public ArrayList<TwitchGame> searchResults;
    public TwitchGame result;
    protected String searchQuery;

    public TwitchGameSearcher(Context context, ProgressDialog progressDialog)
    {
        super(context, progressDialog);
    }

    @Override
    protected void onPreExecute() {
        searchResults = new ArrayList<>();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return super.doInBackground(params);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject)
    {
        JSONArray gamesJson = null;

        if(searchQuery.equals("null"))
        {
            searchResults.add(new TwitchGame("null", null));
        }
        else if(jsonObject != null)
        {
            try {
                gamesJson = jsonObject.getJSONArray("games");
                searchResults = parseJson(gamesJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        TwitchDbHelper dbHelper = new TwitchDbHelper(mContext.get());

        // could not find a game by the given name
        if(searchResults.size() == 0)
        {
            // add the new game
            TwitchGame tg = new TwitchGame(searchQuery, null);
            tg.id = dbHelper.insertOrReplaceGameEntry(tg);
            result = tg;
        }
        // retrieve game in question from results
        else
        {
            for (TwitchGame game : searchResults)
            {
                // insert game into database
                game.id = dbHelper.insertOrReplaceGameEntry(game);
                // exact match
                if (game.name.equals(searchQuery))
                {
                    result = game;
                }
                // no exact match found
                else if (searchResults.indexOf(game) == searchResults.size() - 1)
                {
                    // add the new game
                    TwitchGame tg = new TwitchGame(searchQuery, null);
                    tg.id = dbHelper.insertOrReplaceGameEntry(tg);
                    result = tg;
                }
            } // for
        }

        if(mListener != null) mListener.handleAsyncTaskFinished();
    } // onPostExecute

    public void run(String searchQuery)
    {
        this.searchQuery = searchQuery;
        String url = String.format("https://api.twitch.tv/kraken/search/games?q=%s&type=suggest",
                searchQuery);
        executeOnExecutor(THREAD_POOL_EXECUTOR, url);
    }

    @Override
    public String toString() {
        return "TwitchGameSearcher " + searchQuery;
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
