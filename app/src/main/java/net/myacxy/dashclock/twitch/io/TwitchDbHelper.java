package net.myacxy.dashclock.twitch.io;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import net.myacxy.dashclock.twitch.TwitchExtension;
import net.myacxy.dashclock.twitch.models.TwitchChannel;

import java.util.ArrayList;
import java.util.Set;

/**
 * @see <a href="http://goo.gl/vk1g8o">Android Developer</a>
 */
public class TwitchDbHelper extends SQLiteOpenHelper
{
    private Context mContext;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TwitchContract.ChannelEntry.TABLE_NAME + " (" +
            TwitchContract.ChannelEntry._ID + " INTEGER PRIMARY KEY," +
            TwitchContract.ChannelEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
            TwitchContract.ChannelEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
            TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
            TwitchContract.ChannelEntry.COLUMN_NAME_STATUS + TEXT_TYPE + COMMA_SEP +
            TwitchContract.ChannelEntry.COLUMN_NAME_GAME + TEXT_TYPE + COMMA_SEP +
            TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE + TEXT_TYPE + COMMA_SEP +
            TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TwitchContract.ChannelEntry.TABLE_NAME;
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Twitch.db";

    public TwitchDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Retrieves data from the database and returns it as a Cursor.
     *
     * @param selected only retrieve channels that were selected
     * @param online only retrieves TwitchChannels that are online
     * @return Cursor of the retrieved data
     */
    public Cursor getChannelsCursor(boolean selected, boolean online)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);

        String sortOrder = TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME;

        String selection = null;
        String[] selectionArgs = null;
        // select 'online' column
        if(online) {
            selection = TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE + " LIKE ?";
            selectionArgs = new String[]{ "1" };
        }
        // select 'selected' column
        if (selected && sp.getBoolean(TwitchExtension.PREF_CUSTOM_VISIBILITY, false)) {
            if(online) {
                selection += " AND " + TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED + " LIKE ?";
                selectionArgs = new String[]{ "1", "1" };
            } else {
                selection = TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED + " LIKE ?";
                selectionArgs = new String[]{ "1" };
            }
        }
        // get all entries of the table from the database
        Cursor cursor = getReadableDatabase().query(
                TwitchContract.ChannelEntry.TABLE_NAME, // the table to query
                TwitchDbHelper.ChannelQuery.projection, // the columns to return
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
        Cursor cursor = getChannelsCursor(selected, false);
        // parse each data element to a TwitchChannel
        while(cursor.moveToNext()) {
            TwitchChannel twitchChannel = new TwitchChannel();
            twitchChannel.displayName = cursor.getString(TwitchDbHelper.ChannelQuery.displayName);
            twitchChannel.game = cursor.getString((TwitchDbHelper.ChannelQuery.game));
            twitchChannel.status = cursor.getString(TwitchDbHelper.ChannelQuery.status);
            twitchChannel.online = cursor.getInt(TwitchDbHelper.ChannelQuery.online) == 1;
            twitchChannels.add(twitchChannel);
        }
        cursor.close();
        close();
        return twitchChannels;
    } // getAllChannels

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
        for (TwitchChannel tc : allChannels)
        {
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
        // initialize
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        TwitchDbHelper dbHelper = new TwitchDbHelper(mContext);
        // retrieve data
        ArrayList<TwitchChannel> allChannels = dbHelper.getAllChannels(true);
        ArrayList<TwitchChannel> onlineChannels = dbHelper.filterOnlineChannels(allChannels);
        dbHelper.close();
        // initialize data
        int onlineCount = onlineChannels.size();
        String status = String.format("%d Live", onlineCount);
        String expandedTitle = String.format("%s Channel%s", status, onlineCount > 1 ? "s" : "");
        String expandedBody = "";
        // build body
        for (TwitchChannel tc : onlineChannels)
        {
            expandedBody += String.format("%s playing %s: %s", tc.displayName, tc.game, tc.status);
            if(onlineChannels.indexOf(tc) < onlineChannels.size() - 1) expandedBody += "\n";
        }
        // save data to preferences
        editor.putInt(TwitchExtension.PREF_ONLINE_COUNT, onlineCount);
        editor.putString(TwitchExtension.PREF_STATUS, status);
        editor.putString(TwitchExtension.PREF_EXPANDED_TITLE, expandedTitle);
        editor.putString(TwitchExtension.PREF_EXPANDED_BODY, expandedBody);
        editor.apply();
    } // updateSharedPreferencesData

    /**
     * Updates the online status of a TwitchChannel inside the database.
     *
     * @param twitchChannel TwitchChannel to be updated
     */
    public void updateOnlineStatus(TwitchChannel twitchChannel)
    {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE, twitchChannel.online);

        // Which row to update, based on the ID
        String selection = TwitchContract.ChannelEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(twitchChannel.entryId) };

        int count = getReadableDatabase().update(
                TwitchContract.ChannelEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        close();
    } // updateOnlineStatus

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
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME, tc.displayName);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_NAME, tc.name);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_STATUS, tc.status);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_GAME, tc.game);
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE, 0);
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

    /**
     * Helps a cursor querying the database by providing
     * a projection of the entries and their ids
     */
    public interface ChannelQuery
    {
        // Defines a projection that specifies which columns from the database
        // you will actually use for querying
        public String[] projection = new String[] {
                TwitchContract.ChannelEntry._ID,
                TwitchContract.ChannelEntry.COLUMN_NAME_ENTRY_ID,
                TwitchContract.ChannelEntry.COLUMN_NAME_NAME,
                TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME,
                TwitchContract.ChannelEntry.COLUMN_NAME_STATUS,
                TwitchContract.ChannelEntry.COLUMN_NAME_GAME,
                TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE,
                TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED,
        };

        // id of each column
        public int id = 0;
        public int entryId = 1;
        public int name = 2;
        public int displayName = 3;
        public int status = 4;
        public int game = 5;
        public int online = 6;
        public int selected = 7;
    } // ChannelQuery
} // TwitchDbHelper