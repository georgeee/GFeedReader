package ru.georgeee.android.gfeedreader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.*;
import ru.georgeee.android.gfeedreader.R;
import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.model.WebString;
import ru.georgeee.android.gfeedreader.utility.xml.FeedReaderTask;

import java.util.List;

public class EntryPageActivity extends Activity {

    Feed feed;
    Entry entry;
    Button backBtn;
    Button openFullArticleBtn;
    TextView feedTitle;
    TextView entryTitle;
    WebView contentWebView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        feedTitle = (TextView) findViewById(R.id.entryPageFeedTitle);
        entryTitle = (TextView) findViewById(R.id.entryPageEntryTitle);
        contentWebView = (WebView) findViewById(R.id.entryPageContent);
        backBtn = (Button) findViewById(R.id.backBtn);
        openFullArticleBtn = (Button) findViewById(R.id.openFullArticle);
        Bundle extras = getIntent().getExtras();
        feed = (Feed) extras.get("feeds");
        entry = (Entry) extras.get("entry");

        getWindow().setTitle(feed.getTitle().getText()+" / "+entry.getTitle().getText());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(entry.getUrl() == null) openFullArticleBtn.setEnabled(false);
        openFullArticleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getUrl()));
                startActivity(browseIntent);
            }
        });
        entryTitle.setText(entry.getTitle().getText());
        feedTitle.setText(feed.getTitle().getText());
        setWebStringToWebView(entry.getContent()==null?entry.getSummary():entry.getContent(), contentWebView);
        Log.d(EntryPageActivity.class.getName(), "entry: "+entry);
    }

    protected void setWebStringToWebView(WebString webString, WebView webView){
        if(webString != null){
            if(webString.isUrl()){
                webView.loadUrl(webString.getText());
            }else{
                webView.loadData(webString.getText(), webString.getType(), webString.isBase64() ? "base64" : null);
            }
        }
    }

}
