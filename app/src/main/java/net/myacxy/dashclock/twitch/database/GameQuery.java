package net.myacxy.dashclock.twitch.database;

public interface GameQuery {
    public String[] projection = new String[] {
            TwitchContract.GameEntry._ID,
            TwitchContract.GameEntry.COLUMN_NAME_ENTRY_ID,
            TwitchContract.GameEntry.COLUMN_NAME_NAME,
            TwitchContract.GameEntry.COLUMN_NAME_ABBREVIATION,
            TwitchContract.GameEntry.COLUMN_NAME_CHANNELS,
            TwitchContract.GameEntry.COLUMN_NAME_VIEWERS,
            TwitchContract.GameEntry.COLUMN_NAME_LOGO
    };

    public int id = 0;
    public int entryId = 1;
    public int name = 2;
    public int abbreviation = 3;
    public int channels = 4;
    public int viewers = 5;
    public int logo = 6;
}
