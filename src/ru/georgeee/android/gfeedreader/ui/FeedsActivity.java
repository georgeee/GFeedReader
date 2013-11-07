package ru.georgeee.android.gfeedreader.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import ru.georgeee.android.gfeedreader.R;
import ru.georgeee.android.gfeedreader.SFBaseActivity;
import ru.georgeee.android.gfeedreader.handlers.SFBaseCommand;
import ru.georgeee.android.gfeedreader.handlers.impl.LoadFeedEntriesCommand;
import ru.georgeee.android.gfeedreader.handlers.impl.ObtainFeedCommand;
import ru.georgeee.android.gfeedreader.utility.db.EntryTable;
import ru.georgeee.android.gfeedreader.utility.db.FeedTable;
import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;

import java.io.IOException;
import java.util.ArrayList;

public class FeedsActivity extends SFBaseActivity {
    private static final String PROGRESS_DIALOG = "progress-dialog";
    SearchView searchView;
    ListView feedList;
    FeedListAdapter feedListAdapter;
    int requestId = -1;

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
        FeedTable feedTable = FeedTable.getInstance(this);
        EntryTable entryTable = EntryTable.getInstance(this);
        try {
            feedListAdapter.addAll(feedTable.loadFeeds());
        } catch (IOException e) {
            Log.e(FeedsActivity.class.getCanonicalName(), e.toString());
        } catch (ClassNotFoundException e) {
            Log.e(FeedsActivity.class.getCanonicalName(), e.toString());
        }
        feedListAdapter.notifyDataSetInvalidated();

    }

    private void processQuery() {
        String query = searchView.getQuery().toString();
        ProgressDialogFragment progress = new ProgressDialogFragment();
        progress.show(getSupportFragmentManager(), PROGRESS_DIALOG);
        requestId = getServiceHelper().obtainFeedAction(query);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing");
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getServiceHelper().cancelCommand(requestId);
            }
        });

        return progressDialog;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (requestId != -1 && !getServiceHelper().isPending(requestId)) {
            dismissProgressDialog();
        }
    }

    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
        super.onServiceCallback(requestId, requestIntent, resultCode, resultData);

        if (getServiceHelper().check(requestIntent, ObtainFeedCommand.class)) {
            if (resultCode == ObtainFeedCommand.RESPONSE_SUCCESS) {
                dismissProgressDialog();
                Feed feed = (Feed) resultData.get("feed");
                feedListAdapter.add(feed);
                feedListAdapter.notifyDataSetInvalidated();
            } else if (resultCode == ObtainFeedCommand.RESPONSE_PROGRESS) {
                updateProgressDialog(resultData.getInt(SFBaseCommand.EXTRA_PROGRESS, -1));
            } else {
                dismissProgressDialog();
            }
        } else if (getServiceHelper().check(requestIntent, LoadFeedEntriesCommand.class)) {
            if (resultCode == LoadFeedEntriesCommand.RESPONSE_SUCCESS) {
                dismissProgressDialog();
                Feed feed = (Feed) resultData.get("feed");
                ArrayList<Entry>  entries = (ArrayList<Entry>) resultData.get("entries");
                Intent intent = new Intent(this, EntriesActivity.class);
                intent.putExtra("feed", feed);
                intent.putExtra("entries", entries);
                startActivity(intent);
            } else if (resultCode == LoadFeedEntriesCommand.RESPONSE_PROGRESS) {
                updateProgressDialog(resultData.getInt(SFBaseCommand.EXTRA_PROGRESS, -1));
            } else {
                dismissProgressDialog();
            }
        }
    }

    public void cancelCommand() {
        getServiceHelper().cancelCommand(requestId);
    }

    private void dismissProgressDialog() {
        ProgressDialogFragment progress = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(
                PROGRESS_DIALOG);
        if (progress != null) {
            progress.dismiss();
        }
    }

    private void updateProgressDialog(int progress) {
        ProgressDialogFragment progressDialog = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(
                PROGRESS_DIALOG);
        if (progressDialog != null) {
            progressDialog.setProgress(progress);
        }
    }

    public static class ProgressDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Processing...");

            return progressDialog;
        }

        public void setProgress(int progress) {
            ((ProgressDialog) getDialog()).setMessage("Processing... " + progress + "%");
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            ((FeedsActivity) getActivity()).cancelCommand();
        }

    }

    protected class FeedListAdapter extends ArrayAdapter<Feed> {


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
//                    ProgressDialogFragment progress = new ProgressDialogFragment();
//                    progress.show(getSupportFragmentManager(), PROGRESS_DIALOG);
                    getServiceHelper().loadFeedEntriesAction(feed);
                }
            });
            return rowView;
        }
    }
}
