package ru.georgeee.android.gfeedreader.utility.http.download;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import ru.georgeee.android.gfeedreader.utility.cacher.FileCacher;
import ru.georgeee.android.gfeedreader.utility.http.HttpTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 29.09.13
 * Time: 21:29
 * To change this template use File | Settings | File Templates.
 */
public abstract class CachingHttpDownloadTask extends HttpTask<File> {

    protected abstract String getUrl();


    @Override
    protected HttpRequestBase getHttpRequestBase() {
        return new HttpGet(getUrl());
    }

    @Override
    protected File doInBackground() {
        String url = getUrl();
        FileCacher fileCacher = FileCacher.getInstance();
        File file = fileCacher.registerFile(url);
        if (!file.exists()) {
            file = super.doInBackground();
        }
        fileCacher.launchCacheCleaner();
        return file;
    }

    @Override
    protected File getResult(HttpResponse httpResponse) throws IOException, CanceledException {
        String url = getUrl();
        FileCacher fileCacher = FileCacher.getInstance();
        File file = fileCacher.registerFile(url);
        if (file.exists()) {
            return file;
        }
        readInputStreamIntoFile(httpResponse.getEntity().getContent(), file);
        fileCacher.updateSize(url, file.length());
        checkCancell();
        fileCacher.launchCacheCleaner();
        return file;
    }

    protected void readInputStreamIntoFile(InputStream inputStream, File file) throws IOException, CanceledException {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            if (!file.exists()) file.createNewFile();
            int read = 0;
            byte[] bytes = new byte[1024 * 1024]; //1MB

            while ((read = inputStream.read(bytes)) != -1) {
                checkCancell();
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw e;
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    throw e;
                }

            }
        }
    }

}
