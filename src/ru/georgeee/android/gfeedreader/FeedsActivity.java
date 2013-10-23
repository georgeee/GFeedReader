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
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.xml.FeedReaderTask;

public class FeedsActivity extends Activity {
    SearchView searchView;
    ListView feedList;
    FeedListAdapter feedListAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds);
        searchView = (SearchView) findViewById(R.id.searchView);
        feedList = (ListView) findViewById(R.id.feedList);
        feedListAdapter = new FeedListAdapter(this);
        feedList.setAdapter(feedListAdapter);
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
                if(feed == null) return;
                feedListAdapter.add(feed);
                feedListAdapter.notifyDataSetInvalidated();
            }
        }.executeOnHttpTaskExecutor();
    }

    protected class FeedListAdapter extends ArrayAdapter<Feed>{


        public FeedListAdapter(Context context) {
            super(context, R.layout.entry_row);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflator = getLayoutInflater();
            View rowView = inflator.inflate(R.layout.feed_row, null, true);
            TextView rowTitle = (TextView) rowView.findViewById(R.id.feedRowTitle);
            TextView rowTime = (TextView) rowView.findViewById(R.id.feedRowTime);
            final Feed feed = getItem(position);
            rowTitle.setText(feed.getTitle() == null ? "" : feed.getTitle().getText());
            rowTime.setText(feed.getLastUpdated() == null ? "" : feed.getLastUpdated().toString());
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), EntriesActivity.class);
                    intent.putExtra("feeds", feed);
                    startActivity(intent);
                }
            });
            return rowView;
        }
    }
}
