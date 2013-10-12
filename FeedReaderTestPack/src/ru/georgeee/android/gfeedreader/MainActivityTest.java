package ru.georgeee.android.gfeedreader;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.xml.FeedReaderTask;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class ru.georgeee.android.gfeedreader.MainActivityTest \
 * ru.georgeee.android.gfeedreader.tests/android.test.InstrumentationTestRunner
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super("ru.georgeee.android.gfeedreader", MainActivity.class);
    }

    public void testFeadReader() throws Exception {
        for (final String url : new String[]{
                "http://feeds.feedburner.com/time/topstories?format=xml",
                "http://georgeee.podfm.ru/rss/rss.xml",
                "http://blog.case.edu/news/feed.atom",
                "http://bblfish.net/blog/blog.atom",
        }){
            FeedReaderTask task = new FeedReaderTask(url){
                @Override
                protected void onPostExecute(Feed feed) {
                    Log.d(MainActivityTest.class.getName(), "(onPostExecute) Feed "+url+" loaded: " + feed);
                }
            };
            Feed feed = task.executeOnHttpTaskExecutor().get();
            Log.d(MainActivityTest.class.getName(), "Feed "+url+" loaded: " + feed);
        }

    }
}
