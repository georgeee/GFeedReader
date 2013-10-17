package ru.georgeee.android.gfeedreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.xml.FeedReaderTask;

import java.util.List;

public class MainActivity extends Activity {
    Feed feed;
    SearchView searchView;
    ListView entryList;
    TextView feedTitle;
    EntryListAdapter entryListAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        searchView = (SearchView) findViewById(R.id.searchView);
        entryList = (ListView) findViewById(R.id.entryList);
        feedTitle = (TextView) findViewById(R.id.feedTitle);
        entryListAdapter = new EntryListAdapter(this);
        entryList.setAdapter(entryListAdapter);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                processQuery();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        if(searchView.getQuery().toString().isEmpty())
            searchView.setQuery("http://stackoverflow.com/feeds/tag/android", true);
    }

    private void processQuery() {
        String query = searchView.getQuery().toString();
        new FeedReaderTask(query){
            @Override
            protected void onPostExecute(Feed feed) {
                reloadFeed(feed);
            }
        }.executeOnHttpTaskExecutor();
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
                    intent.putExtra("feed", feed);
                    intent.putExtra("entry", entry);
                    startActivity(intent);
                }
            });
            return rowView;
        }
    }
}
