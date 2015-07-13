package net.myacxy.dxt.models;


public class TwitchChannelListViewEntry
{
    public String rowKey;
    public int rowId;
    public int queryId;
    public int textId;

    public TwitchChannelListViewEntry(String rowKey, int rowId, int queryId, int textId)
    {
        this.rowKey = rowKey;
        this.rowId = rowId;
        this.queryId = queryId;
        this.textId = textId;
    }
} // TwitchChannelListViewEntry
