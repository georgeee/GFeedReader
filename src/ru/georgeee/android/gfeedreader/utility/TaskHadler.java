package ru.georgeee.android.gfeedreader.utility;

import android.os.AsyncTask;

import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 24.10.13
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
public abstract class TaskHadler <Result>{
    protected abstract Result doInBackground();
    boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    protected abstract Executor getPreferedExecutor();


    public AsyncTask<Void, Integer, Result> executeAsAsyncTask() {
        final TaskHadler<Result> taskHadler = this;
        return new AsyncTask<Void, Integer, Result>() {
            @Override
            protected void onPreExecute() {
                taskHadler.onPreExecute();
            }

            @Override
            protected void onPostExecute(Result result) {
                taskHadler.onPostExecute(result);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                taskHadler.onProgressUpdate(values);
            }

            @Override
            protected void onCancelled() {
                setCancelled(true);
                taskHadler.onCancelled();
            }

            @Override
            protected Result doInBackground(Void... voids) {
                return taskHadler.doInBackground();
            }
        }.executeOnExecutor(getPreferedExecutor());
    }

    public Result executeInCurrentThread(){
        onPreExecute();
        Result result = doInBackground();
        onPostExecute(result);
        return result;
    }

    protected void onProgressUpdate(Integer[] values){}

    protected void onPostExecute(Result result){}

    protected void onPreExecute(){}

    protected void onCancelled(){}

}
