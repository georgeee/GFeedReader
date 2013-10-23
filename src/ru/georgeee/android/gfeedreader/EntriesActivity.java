package ru.georgeee.android.gfeedreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.xml.FeedReaderTask;

public class EntriesActivity extends Activity {
    Feed feed;
    ListView entryList;
    TextView feedTitle;
    EntryListAdapter entryListAdapter;
    Button backBtn;

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
        feed = (Feed) extras.get("feeds");
        reloadFeed(feed);
    }

    public void reloadFeed(Feed feed){
        this.feed = feed;
        entryListAdapter.clear();
        if(feed == null){
            feedTitle.setText("Null");
            return;
        }
        feedTitle.setText(feed.getTitle().getText());
        entryListAdapter.addAll(feed.getAllEntries());
        entryListAdapter.notifyDataSetInvalidated();
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
