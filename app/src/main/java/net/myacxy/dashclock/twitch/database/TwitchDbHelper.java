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

package net.myacxy.dashclock.twitch.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import net.myacxy.dashclock.twitch.TwitchExtension;
import net.myacxy.dashclock.twitch.models.TwitchChannel;
import net.myacxy.dashclock.twitch.models.TwitchGame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @see <a href="http://goo.gl/vk1g8o">Android Developer</a>
 */
public class TwitchDbHelper extends SQLiteOpenHelper
{
    private Context mContext;
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String foreignKeyGame = String.format("FOREIGN KEY(%s) REFERENCES %s(%s)",
            TwitchContract.ChannelEntry.COLUMN_NAME_GAME_ID,
            TwitchContract.GameEntry.TABLE_NAME,
            TwitchContract.GameEntry._ID);

    private static final String SQL_CREATE_CHANNEL_ENTRIES =
            "CREATE TABLE " + TwitchContract.ChannelEntry.TABLE_NAME + " (" +
                    TwitchContract.ChannelEntry._ID + " INTEGER PRIMARY KEY," +
                    TwitchContract.ChannelEntry.COLUMN_NAME_ENTRY_ID + INTEGER_TYPE + COMMA_SEP +
                    TwitchContract.ChannelEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
                    TwitchContract.ChannelEntry.COLUMN_NAME_STATUS + TEXT_TYPE + COMMA_SEP +
                    TwitchContract.ChannelEntry.COLUMN_NAME_GAME_ID + INTEGER_TYPE + COMMA_SEP +
                    TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE + TEXT_TYPE + COMMA_SEP +
                    TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED + TEXT_TYPE + COMMA_SEP +
                    TwitchContract.ChannelEntry.COLUMN_NAME_VIEWERS + TEXT_TYPE +COMMA_SEP +
                    TwitchContract.ChannelEntry.COLUMN_NAME_FOLLOWERS + TEXT_TYPE +COMMA_SEP +
                    TwitchContract.ChannelEntry.COLUMN_NAME_UPDATED_AT + TEXT_TYPE +COMMA_SEP +
                    TwitchContract.ChannelEntry.COLUMN_NAME_LOGO + TEXT_TYPE + COMMA_SEP +
                    TwitchContract.ChannelEntry.COLUMN_NAME_PREVIEW + TEXT_TYPE + COMMA_SEP +
                    foreignKeyGame +
                    " )";

    private static final String SQL_CREATE_GAME_ENTRIES =
            "CREATE TABLE " + TwitchContract.GameEntry.TABLE_NAME + " (" +
                    TwitchContract.GameEntry._ID + " INTEGER PRIMARY KEY," +
                    TwitchContract.GameEntry.COLUMN_NAME_ENTRY_ID + INTEGER_TYPE + COMMA_SEP +
                    TwitchContract.GameEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    TwitchContract.GameEntry.COLUMN_NAME_ABBREVIATION + TEXT_TYPE + COMMA_SEP +
                    TwitchContract.GameEntry.COLUMN_NAME_CHANNELS + TEXT_TYPE + COMMA_SEP +
                    TwitchContract.GameEntry.COLUMN_NAME_VIEWERS + TEXT_TYPE + COMMA_SEP +
                    TwitchContract.GameEntry.COLUMN_NAME_LOGO + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_CHANNEL_ENTRIES =
            "DROP TABLE IF EXISTS " + TwitchContract.ChannelEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Twitch.db";

    public TwitchDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_CHANNEL_ENTRIES);
        db.execSQL(SQL_CREATE_GAME_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply discard the data and start over
        db.execSQL(SQL_DELETE_CHANNEL_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    public enum State {
        ALL,
        ONLINE,
        OFFLINE
    }

    /**
     * Retrieves data from the database and returns it as a Cursor.
     *
     * @param selected only retrieve channels that were selected
     * @param state TODO
     * @return Cursor of the retrieved data
     */
    public Cursor getChannelsCursor(boolean selected, State state, String sortOrder)
    {
        String selection = null;
        String[] selectionArgs = null;

        switch (state) {
            case ALL:
                if(selected) {
                    selection = TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED + " LIKE ?";
                    selectionArgs = new String[]{ "1" };
                }
                break;
            case ONLINE:
                selection = TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE + " LIKE ?";
                selectionArgs = new String[]{ "1" };
                if (selected) {
                    selection += " AND " + TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED + " LIKE ?";
                    selectionArgs = new String[]{ "1", "1" };
                }
                break;
            case OFFLINE:
                selection = TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE + " LIKE ?";
                selectionArgs = new String[]{ "0" };
                if (selected) {
                    selection += " AND " + TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED + " LIKE ?";
                    selectionArgs = new String[]{ "0", "1" };
                }
                break;
        }

        // get all entries of the table from the database
        Cursor cursor = getReadableDatabase().query(
                TwitchContract.ChannelEntry.TABLE_NAME, // the table to query
                ChannelQuery.projection, // the columns to return
                selection,                              // the columns for the WHERE clause
                selectionArgs,                          // the values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                sortOrder                               // the sort order
        );
        return cursor;
    } // getChannelsCursor

    /**
     * Retrieves all or only the selected TwitchChannels from the database.
     *
     * @param selected only retrieve channels that were selected
     * @return ArrayList of TwitchChannels
     */
    public ArrayList<TwitchChannel> getAllChannels(boolean selected)
    {
        ArrayList<TwitchChannel> twitchChannels = new ArrayList<TwitchChannel>();
        // retrieve data cursor
        Cursor cursor = getChannelsCursor(selected, State.ALL, TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME);
        // parse each data element to a TwitchChannel
        while(cursor.moveToNext()) {
            int gameEntryId = cursor.getInt((ChannelQuery.gameId));
            twitchChannels.add(new TwitchChannel(cursor, getGame(gameEntryId)));
        }
        cursor.close();
        close();
        return twitchChannels;
    } // getAllChannels

    /**
     * Retrieves a TwitchChannel from the database based on its row id
     *
     * @param id unique row identifier of the channel
     * @return a single TwitchChannel
     */
    public TwitchChannel getChannel(int id) {
        String selection = TwitchContract.ChannelEntry._ID + " LIKE ?";
        String[] selectionArgs = new String[]{ String.valueOf(id) };

        // get all entries of the table from the database
        Cursor cursor = getReadableDatabase().query(
                TwitchContract.GameEntry.TABLE_NAME,    // the table to query
                GameQuery.projection,                   // the columns to return
                selection,                              // the columns for the WHERE clause
                selectionArgs,                          // the values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // the sort order
        );

        int gameName = cursor.getInt((ChannelQuery.gameId));
        TwitchChannel channel = new TwitchChannel(cursor, getGame(gameName));

        cursor.close();
        close();
        return channel;
    }

    /**
     * Checks a list of TwitchChannels and returns
     * a list of TwitchChannels that are online.
     *
     * @param allChannels List of TwitchChannels to be checked
     * @return List of TwitchChannels that are online
     */
    public ArrayList<TwitchChannel> filterOnlineChannels(ArrayList<TwitchChannel> allChannels)
    {
        ArrayList<TwitchChannel> onlineChannels = new ArrayList<TwitchChannel>();
        for (TwitchChannel tc : allChannels) {
            if(tc.online) onlineChannels.add(tc);
        }
        return onlineChannels;
    } // filterOnlineChannels

    /**
     * Retrieves current data from the database and saves the information
     * using SharedPreferences in a publishable format for DashClock.
     */
    public void updatePublishedData()
    {
        // initialize helpers
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        TwitchDbHelper dbHelper = new TwitchDbHelper(mContext);
        // retrieve data
        boolean selected = sp.getBoolean(TwitchExtension.PREF_CUSTOM_VISIBILITY, false);
        ArrayList<TwitchChannel> allChannels = dbHelper.getAllChannels(selected);
        // initialize data
        String longestBody = null;
        Set<String> expandedBody = new HashSet<>();
        // build body
        for (TwitchChannel tc : allChannels) {
            String body = "";
            // game name has abbreviation
            if(tc.game == null) {
                body = String.format("%s playing %s: %s",
                        tc.displayName,
                        "nothing",
                        tc.status);
            }
            if(tc.game.abbreviation != null) {
                body = String.format("%s playing %s: %s",
                        tc.displayName,
                        tc.game.abbreviation,
                        tc.status);
            } else {
                body = String.format("%s playing %s: %s",
                        tc.displayName,
                        tc.game.name,
                        tc.status);
            }
            // channel is online
            if(tc.online) expandedBody.add(body);
            // determine longest body
            if(body.length() > (longestBody == null ? 0 : longestBody.length())) longestBody = body;
        }

        // initialize published data
        int onlineCount = expandedBody.size();
        String status = String.format("%d Live", onlineCount);
        String expandedTitle = String.format("%s Channel%s", status, onlineCount != 1 ? "s" : "");

        // save data to preferences
        editor.putInt(TwitchExtension.PREF_ONLINE_COUNT, onlineCount);
        editor.putString(TwitchExtension.PREF_STATUS, status);
        editor.putString(TwitchExtension.PREF_EXPANDED_TITLE, expandedTitle);
        editor.putStringSet(TwitchExtension.PREF_EXPANDED_BODY, expandedBody);
        editor.putString(TwitchExtension.PREF_LONGEST_BODY, longestBody);
        editor.apply();
    } // updateSharedPreferencesData

    public Cursor getGamesCursor(boolean abbreviated) {
        String sortOrder = TwitchContract.GameEntry.COLUMN_NAME_NAME;
        String selection = null;
        String[] selectionArgs = null;

        if(abbreviated) {
            selection = TwitchContract.GameEntry.COLUMN_NAME_ABBREVIATION + " NOT LIKE ?";
            selectionArgs = new String[]{ "null" };
        }

        // get all entries of the table from the database
        Cursor cursor = getReadableDatabase().query(
                TwitchContract.GameEntry.TABLE_NAME,    // the table to query
                GameQuery.projection,                   // the columns to return
                selection,                                   // the columns for the WHERE clause
                selectionArgs,                                   // the values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                sortOrder                               // the sort order
        );
        return cursor;
    }

    public TwitchGame getGame(int id) {

        String selection = TwitchContract.GameEntry._ID + " LIKE ?";
        String[] selectionArgs = new String[]{ String.valueOf(id) };

        // get all entries of the table from the database
        Cursor cursor = getReadableDatabase().query(
                TwitchContract.GameEntry.TABLE_NAME,    // the table to query
                GameQuery.projection,                   // the columns to return
                selection,                              // the columns for the WHERE clause
                selectionArgs,                          // the values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // the sort order
        );

        TwitchGame game = null;
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            game = new TwitchGame(cursor);
        }
        cursor.close();
        close();
        return game;
    }

    public ArrayList<TwitchGame> getGames(boolean abbreviated) {
        ArrayList<TwitchGame> games = new ArrayList<>();
        Cursor cursor = getGamesCursor(abbreviated);
        while (cursor.moveToNext()) {
            games.add(new TwitchGame(cursor));
        }
        cursor.close();
        close();
        return games;
    }

    public void insertOrReplaceGameEntries(ArrayList<TwitchGame> games) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        for(TwitchGame game : games) {
            insertOrReplaceGameEntry(game, db);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public int insertOrReplaceGameEntry(TwitchGame game) {
        SQLiteDatabase db = getWritableDatabase();
        int id = insertOrReplaceGameEntry(game, db);
        db.close();
        return id;
    }

    public void deleteAbbreviation(String game) {

        SQLiteDatabase db = getWritableDatabase();

        String sql = String.format("INSERT OR REPLACE INTO %1$s ( %2$s, %3$s, %4$s )"
                        + "VALUES ((SELECT %2$s FROM %1$s WHERE %3$s = ?),"
                        + " ?,"
                        + " ?)",
                TwitchContract.GameEntry.TABLE_NAME,
                TwitchContract.GameEntry._ID,
                TwitchContract.GameEntry.COLUMN_NAME_NAME,
                TwitchContract.GameEntry.COLUMN_NAME_ABBREVIATION);

//        String sql = "INSERT OR REPLACE INTO " + TwitchContract.GameEntry.TABLE_NAME
//                + " ( "
//                + TwitchContract.GameEntry._ID + COMMA_SEP
//                + TwitchContract.GameEntry.COLUMN_NAME_NAME + COMMA_SEP
//                + TwitchContract.GameEntry.COLUMN_NAME_ABBREVIATION
//                + " ) "
//                + "VALUES ( "
//                + "(SELECT "    + TwitchContract.GameEntry._ID
//                + " FROM "      + TwitchContract.GameEntry.TABLE_NAME
//                + " WHERE "     + TwitchContract.GameEntry.COLUMN_NAME_NAME + " = ?)" + COMMA_SEP
//                + " ?"          + COMMA_SEP
//                + " ?"
//                + " )";

        db.execSQL(sql, new Object[]{ game, game, null });
        db.close();
    }

    private int insertOrReplaceGameEntry(TwitchGame game, SQLiteDatabase db)
    {
        if(game.abbreviation == null) game.abbreviation = checkExampleAbbr(game);

        String sql = "INSERT OR REPLACE INTO " + TwitchContract.GameEntry.TABLE_NAME
                + " ( "
                + TwitchContract.GameEntry._ID + COMMA_SEP
                + TwitchContract.GameEntry.COLUMN_NAME_NAME + COMMA_SEP
                + TwitchContract.GameEntry.COLUMN_NAME_ABBREVIATION
                + " ) "
                + "VALUES ( "
                + "(SELECT "    + TwitchContract.GameEntry._ID
                + " FROM "      + TwitchContract.GameEntry.TABLE_NAME
                + " WHERE "     + TwitchContract.GameEntry.COLUMN_NAME_NAME + " = ?)" + COMMA_SEP
                + " ?"           + COMMA_SEP
                + " CASE WHEN ? IS NULL THEN "
                + "(SELECT "    + TwitchContract.GameEntry.COLUMN_NAME_ABBREVIATION
                + " FROM "      + TwitchContract.GameEntry.TABLE_NAME
                + " WHERE "     + TwitchContract.GameEntry.COLUMN_NAME_NAME + " = ?)"
                + " ELSE ? END"
                + " )";

        db.execSQL(sql, new Object[]{game.name, game.name, game.abbreviation, game.name, game.abbreviation});

        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);

        return id;
    } // updateOnlineStatus

    private String checkExampleAbbr(TwitchGame game) {

        switch (game.name) {
            case "League of Legends":
                return "LoL";
            case "Hearthstone: Heroes of Warcraft":
                return "HS";
            case "Counter-Strike: Global Offensive":
                return "CSGO";
            case "World of Warcraft: Mists of Pandaria":
                return "WoW";
            case "World of Warcraft: Warlords of Draenor":
                return "WoW";
            case "StarCraft II: Heart of the Swarm":
                return "SC2";
            case "Diablo III: Reaper of Souls":
                return "D3";
            case "Final Fantasy XIV Online: A Realm Reborn":
                return "FFXIV";
            case "Path of Exile":
                return "PoE";
            case "Ultra Street Fighter IV":
                return "SFIV";
            default:
                return null;
        }
    }

    /**
     * Updates the selection status of each TwitchChannel inside the database.
     *
     * @param selectedChannels Set of Strings where each String represents
     *                         a TwitchChannel using only its displayName
     */
    public void updateSelectionStatus(Set<String> selectedChannels)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.enableWriteAheadLogging();

        String sortOrder = TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME;

        // get all entries of the table from the database
        Cursor cursor = db.query(
                TwitchContract.ChannelEntry.TABLE_NAME, // the table to query
                ChannelQuery.projection,                // the columns to return
                null,                                   // the columns for the WHERE clause
                null,                                   // the values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                sortOrder                               // the sort order
        );

        while (cursor.moveToNext())
        {
            // New value for one column
            ContentValues values = new ContentValues();
            boolean selected = selectedChannels.contains(cursor.getString(ChannelQuery.displayName));
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED, selected);

            // Which row to update, based on the ID
            String selection = TwitchContract.ChannelEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
            String[] selectionArgs = {String.valueOf(cursor.getString(ChannelQuery.entryId))};

            int count = db.update(
                    TwitchContract.ChannelEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
        cursor.close();
        db.close();
    } // updateSelectionStatus

    /**
     * Saves the provided TwitchChannels to the database. The corresponding table will be
     * deleted beforehand in order to avoid duplicates and keep the database up-to-date because a
     * channel possesses too many properties that would have to be checked one by one.
     *
     * @param followedChannels List of followed TwitchChannels
     */
    public void saveChannels(ArrayList<TwitchChannel> followedChannels)
    {
        // delete old data
        getWritableDatabase().delete(TwitchContract.ChannelEntry.TABLE_NAME, null, null);

        for(TwitchChannel tc : followedChannels)
        {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_ENTRY_ID, tc.entryId);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_VIEWERS, tc.viewers);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_FOLLOWERS, tc.followers);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE, tc.online);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_STATUS, tc.status);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME, tc.displayName);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_NAME, tc.name);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_LOGO, tc.logo);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_PREVIEW, tc.preview);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_UPDATED_AT, tc.updatedAt);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_GAME_ID, tc.game.id);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED, 0);

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = getWritableDatabase().insert(
                    TwitchContract.ChannelEntry.TABLE_NAME,
                    null,
                    values);
        }
        close();
    } // saveChannels
} // TwitchDbHelper