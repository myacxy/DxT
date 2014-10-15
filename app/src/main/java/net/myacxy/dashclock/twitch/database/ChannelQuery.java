package net.myacxy.dashclock.twitch.database;

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
    public int id = 0;
    public int entryId = 1;
    public int name = 2;
    public int displayName = 3;
    public int status = 4;
    public int game = 5;
    public int online = 6;
    public int selected = 7;
    public int viewers = 8;
    public int followers = 9;
    public int updatedAt = 10;
    public int logo = 11;
    public int preview = 12;

} // ChannelQuery
