package ru.georgeee.android.gfeedreader.utility;

import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 24.10.13
 * Time: 7:40
 * To change this template use File | Settings | File Templates.
 */
public class Storage {

    private Storage(){

    }

    private static final Storage instance = new Storage();
    public static Storage getInstance(){
        return instance;
    }

    HashMap<String, Feed> feeds = new HashMap<String, Feed>();

    public Feed getFeedMeta(String feedUrl){
        return feeds.get(feedUrl);
    }

    public Feed[] getFeeds(){
        Feed[] _feeds = new Feed[feeds.size()];
        int i = 0;
        for(HashMap.Entry entry : feeds.entrySet()) _feeds[i++] = (Feed)entry.getValue();
        return _feeds;
    }

    HashMap<String, Entry> entries = new HashMap<String, Entry>();
    HashMap<String, ArrayList<String>> feedEntryMap = new HashMap<String, ArrayList<String>>();


    public void saveEntry(Entry entry){
        String entryUid = entry.getUniqueIdentifier();
        String feedUrl = entry.getFeedUrl();
        if(!entries.containsKey(entryUid)){
            if(!feedEntryMap.containsKey(feedUrl)){
                feedEntryMap.put(feedUrl, new ArrayList<String>());
            }
            feedEntryMap.get(feedUrl).add(entryUid);
        }
        entries.put(entryUid, entry);
    }

    public void saveFeed(Feed feed){
        if(feed != null)
            feeds.put(feed.getFeedUrl(), feed);
    }

    public Entry getEntry(String entryUid){
        return entries.get(entryUid);
    }

    public Entry[] getFeedEntries(String feedUrl){
        ArrayList<String> _entryUids = feedEntryMap.get(feedUrl);
        if(_entryUids == null) return null;
        Entry[] entriesArray = new Entry[_entryUids.size()];
        for(int i=0; i<entriesArray.length; ++i){
            entriesArray[i] = getEntry(_entryUids.get(i));
        }
        return entriesArray;
    }
}
