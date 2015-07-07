package net.myacxy.dxt.database;

public interface GameQuery {
    String[] projection = new String[] {
            TwitchContract.GameEntry._ID,
            TwitchContract.GameEntry.COLUMN_NAME_ENTRY_ID,
            TwitchContract.GameEntry.COLUMN_NAME_NAME,
            TwitchContract.GameEntry.COLUMN_NAME_ABBREVIATION,
            TwitchContract.GameEntry.COLUMN_NAME_CHANNELS,
            TwitchContract.GameEntry.COLUMN_NAME_VIEWERS,
            TwitchContract.GameEntry.COLUMN_NAME_LOGO
    };

    int id = 0;
    int entryId = 1;
    int name = 2;
    int abbreviation = 3;
    int channels = 4;
    int viewers = 5;
    int logo = 6;
}
