package net.myacxy.dxt.database;

/**
 * Helps a cursor querying the database by providing
 * a projection of the entries and their ids
 */
public interface ChannelQuery
{
    // Defines a projection that specifies which columns from the database
    // you will actually use for querying
    String[] projection = new String[] {
            TwitchContract.ChannelEntry._ID,
            TwitchContract.ChannelEntry.COLUMN_NAME_ENTRY_ID,
            TwitchContract.ChannelEntry.COLUMN_NAME_NAME,
            TwitchContract.ChannelEntry.COLUMN_NAME_DISPLAY_NAME,
            TwitchContract.ChannelEntry.COLUMN_NAME_STATUS,
            TwitchContract.ChannelEntry.COLUMN_NAME_GAME_ID,
            TwitchContract.ChannelEntry.COLUMN_NAME_ONLINE,
            TwitchContract.ChannelEntry.COLUMN_NAME_SELECTED,
            TwitchContract.ChannelEntry.COLUMN_NAME_VIEWERS,
            TwitchContract.ChannelEntry.COLUMN_NAME_FOLLOWERS,
            TwitchContract.ChannelEntry.COLUMN_NAME_UPDATED_AT,
            TwitchContract.ChannelEntry.COLUMN_NAME_LOGO,
            TwitchContract.ChannelEntry.COLUMN_NAME_PREVIEW
    };

    // id of each column
    int id = 0;
    int entryId = 1;
    int name = 2;
    int displayName = 3;
    int status = 4;
    int gameId = 5;
    int online = 6;
    int selected = 7;
    int viewers = 8;
    int followers = 9;
    int updatedAt = 10;
    int logo = 11;
    int preview = 12;

} // ChannelQuery
