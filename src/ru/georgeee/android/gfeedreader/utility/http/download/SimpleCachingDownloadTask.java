package ru.georgeee.android.gfeedreader.utility.http.download;

import ru.georgeee.android.gfeedreader.utility.http.HttpUtility;

import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 29.09.13
 * Time: 21:47
 * To change this template use File | Settings | File Templates.
 */
public class SimpleCachingDownloadTask extends CachingHttpDownloadTask {
    protected String url;

    public SimpleCachingDownloadTask(String url) {
        this.url = url;
    }

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    protected Executor getPreferedExecutor() {
        return HttpUtility.getInstance().getFileDownloadExecutor();
    }
}
