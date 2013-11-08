package ru.georgeee.android.gfeedreader.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.georgeee.android.gfeedreader.R;
import ru.georgeee.android.gfeedreader.SFBaseActivity;
import ru.georgeee.android.gfeedreader.handlers.SFBaseCommand;
import ru.georgeee.android.gfeedreader.handlers.impl.LoadFeedEntriesCommand;
import ru.georgeee.android.gfeedreader.handlers.impl.UpdateFeedCommand;
import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;

import java.util.ArrayList;
import java.util.Collections;

public class EntriesActivity extends SFBaseActivity {
    Feed feed;
    ListView entryList;
    TextView feedTitle;
    EntryListAdapter entryListAdapter;
    Button backBtn;
    Button updateBtn;
    ArrayList<Entry> entries = new ArrayList<Entry>();

    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
        super.onServiceCallback(requestId, requestIntent, resultCode, resultData);
        if (getServiceHelper().check(requestIntent, UpdateFeedCommand.class)) {
            if (resultCode == UpdateFeedCommand.RESPONSE_SUCCESS) {
                getServiceHelper().loadFeedEntriesAction(feed);
            } else if (resultCode == UpdateFeedCommand.RESPONSE_PROGRESS) {
                updateProgressDialog(resultData.getInt(SFBaseCommand.EXTRA_PROGRESS, -1));
            } else {
                dismissProgressDialog();
            }
        } else if (getServiceHelper().check(requestIntent, LoadFeedEntriesCommand.class)) {
            if (resultCode == LoadFeedEntriesCommand.RESPONSE_SUCCESS) {
                dismissProgressDialog();
                feed = (Feed) resultData.get("feed");
                ArrayList<Entry> entries = (ArrayList<Entry>) resultData.get("entries");
                reloadFeed(entries);
            } else if (resultCode == LoadFeedEntriesCommand.RESPONSE_PROGRESS) {
                updateProgressDialog(resultData.getInt(SFBaseCommand.EXTRA_PROGRESS, -1));
            } else {
                dismissProgressDialog();
            }
        }
    }

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
        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = entryListAdapter.getItem(position);
                Intent intent = new Intent(view.getContext(), EntryPageActivity.class);
                intent.putExtra("feed", feed);
                intent.putExtra("entry", entry);
                startActivity(intent);
            }
        });

        Bundle extras = getIntent().getExtras();
        feed = (Feed) extras.get("feed");

        updateBtn = (Button) findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogFragment progress = new ProgressDialogFragment();
                progress.show(getSupportFragmentManager(), PROGRESS_DIALOG);
                requestId = getServiceHelper().updateFeedAction(feed);
            }
        });
        ArrayList<Entry> entries = (ArrayList<Entry>) extras.get("entries");
        reloadFeed(entries);
        onResume();
    }

    @Override
    protected void onDestroy() {
        onPause();
    }

    public void reloadFeed(ArrayList<Entry> _entries) {
        if (feed == null) {
            feedTitle.setText("Null");
            return;
        }
        feedTitle.setText(feed.getTitle().getText());
        Collections.sort(_entries);
        entries = _entries;
        entryListAdapter.notifyDataSetInvalidated();
    }

    protected class EntryListAdapter extends ArrayAdapter<Entry> {


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
            entryRowTitle.setText(entry.getTitle() == null ? "" : entry.getTitle().getText());
            entryRowTime.setText(entry.getPubDate() == null ? "" : entry.getPubDate().toString());
            return rowView;
        }
    }
}
