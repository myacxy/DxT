package de.htw_berlin.imi.s0527535.dashclocktwitch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Set;

/**
 * @see <a href="http://goo.gl/vk1g8o">Android Developer</a>
 */
public class TwitchDbHelper extends SQLiteOpenHelper
{
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TwitchContract.ChannelEntry.TABLE_NAME + " (" +
            TwitchContract.ChannelEntry._ID + " INTEGER PRIMARY KEY," +
            TwitchContract.ChannelEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
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

    public void updateOnlineStatus(TwitchChannel twitchChannel)
    {
        SQLiteDatabase db = getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE, twitchChannel.online);

        // Which row to update, based on the ID
        String selection = TwitchContract.ChannelEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(twitchChannel.entryId) };

        int count = db.update(
                TwitchContract.ChannelEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        db.close();
    }

    /**
     * TODO: javadoc / comments
     *
     * @param selectedChannels
     */
    public void updateSelectionStatus(Set<String> selectedChannels)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.enableWriteAheadLogging();

        String sortOrder = TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME;

        // get all entries of the table from the database
        Cursor cursor = db.query(
                TwitchContract.ChannelEntry.TABLE_NAME,         // the table to query
                ChannelQuery.projection,         // the columns to return
                null, // the columns for the WHERE clause
                null,                                  // the values for the WHERE clause
                null,                                           // don't group the rows
                null,                                           // don't filter by row groups
                sortOrder                                       // the sort order
        );

        while (cursor.moveToNext())
        {
            // New value for one column
            ContentValues values = new ContentValues();
            values.put(TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED,
                    selectedChannels.contains(cursor.getString(ChannelQuery.displayName)));

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
    }

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
    }

    /**
     * Helps a cursor querying the database by providing a projection of the entries and their ids
     */
    public interface ChannelQuery
    {
        // Defines a projection that specifies which columns from the database
        // you will actually use for querying
        public String[] projection = new String[] {
                TwitchContract.ChannelEntry._ID,
                TwitchContract.ChannelEntry.COLUMN_NAME_ENTRY_ID,
                TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME,
                TwitchContract.ChannelEntry.COLUMN_NAME_STATUS,
                TwitchContract.ChannelEntry.COLUMN_NAME_GAME,
                TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE,
                TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED,
        };

        public int id = 0;
        public int entryId = 1;
        public int displayName = 2;
        public int status = 3;
        public int game = 4;
        public int online = 5;
        public int selected = 6;
    }
}
