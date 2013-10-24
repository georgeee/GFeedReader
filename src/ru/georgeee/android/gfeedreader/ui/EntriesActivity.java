package ru.georgeee.android.gfeedreader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import ru.georgeee.android.gfeedreader.R;
import ru.georgeee.android.gfeedreader.SFBaseActivity;
import ru.georgeee.android.gfeedreader.handlers.SFBaseCommand;
import ru.georgeee.android.gfeedreader.handlers.impl.LoadFeedMetaCommand;
import ru.georgeee.android.gfeedreader.handlers.impl.UpdateFeedCommand;
import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.xml.FeedReaderTask;

import java.util.ArrayList;
import java.util.Collections;

public class EntriesActivity extends SFBaseActivity {
    Feed feed;

    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
        super.onServiceCallback(requestId, requestIntent, resultCode, resultData);
        if (getServiceHelper().check(requestIntent, UpdateFeedCommand.class)) {
            if (resultCode == UpdateFeedCommand.RESPONSE_SUCCESS) {
                feed = (Feed) resultData.get("feed");
                ArrayList<Entry> entries = (ArrayList<Entry>) resultData.get("entries");
                reloadFeed(entries);
            }
        }
    }

    ListView entryList;
    TextView feedTitle;
    EntryListAdapter entryListAdapter;
    Button backBtn;
    ArrayList<Entry> entries = new ArrayList<Entry>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entries);
        entryList = (ListView) findViewById(R.id.entryList);
        feedTitle = (TextView) findViewById(R.id.feedTitle);
        backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        entryListAdapter = new EntryListAdapter(this);
        entryList.setAdapter(entryListAdapter);
        Bundle extras = getIntent().getExtras();
        feed = (Feed) extras.get("feed");
        ArrayList<Entry> entries = (ArrayList<Entry>)extras.get("entries");
        reloadFeed(entries);
        onResume();
    }

    @Override
    protected void onDestroy() {
        onPause();
    }

    public void reloadFeed(ArrayList<Entry> _entries){
        if(feed == null){
            feedTitle.setText("Null");
            return;
        }
        feedTitle.setText(feed.getTitle().getText());
        Collections.sort(_entries);
        boolean reinvalidate = false;
        for(int i=0; i<_entries.size(); i++){
            Entry entry = _entries.get(i);
            if(Collections.binarySearch(entries, entry) < 0){
                entryListAdapter.insert(entry, 0);
                reinvalidate = true;
            }
        }
        entries = _entries;
        if(reinvalidate) entryListAdapter.notifyDataSetInvalidated();
    }

    protected class EntryListAdapter extends ArrayAdapter<Entry>{


        public EntryListAdapter(Context context) {
            super(context, R.layout.entry_row);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflator = getLayoutInflater();
            View rowView = inflator.inflate(R.layout.entry_row, null, true);
            TextView entryRowTitle = (TextView) rowView.findViewById(R.id.entryRowTitle);
            TextView entryRowTime = (TextView) rowView.findViewById(R.id.entryRowTime);
            final Entry entry = getItem(position);
            entryRowTitle.setText(entry.getTitle()==null?"":entry.getTitle().getText());
            entryRowTime.setText(entry.getPubDate()==null?"":entry.getPubDate().toString());
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), EntryPageActivity.class);
                    intent.putExtra("feeds", feed);
                    intent.putExtra("entry", entry);
                    startActivity(intent);
                }
            });
            return rowView;
        }
    }
}
