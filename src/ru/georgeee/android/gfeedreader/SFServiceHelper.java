/*
 * Copyright (C) 2013 Alexander Osmanov (http://perfectear.educkapps.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ru.georgeee.android.gfeedreader;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.SparseArray;
import ru.georgeee.android.gfeedreader.handlers.SFBaseCommand;
import ru.georgeee.android.gfeedreader.handlers.impl.LoadFeedEntriesCommand;
import ru.georgeee.android.gfeedreader.handlers.impl.LoadFeedMetaCommand;
import ru.georgeee.android.gfeedreader.handlers.impl.UpdateFeedCommand;
import ru.georgeee.android.gfeedreader.service.SFCommandExecutorService;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SFServiceHelper {

    private static SFServiceHelper instance;

    private ArrayList<SFServiceCallbackListener> currentListeners = new ArrayList<SFServiceCallbackListener>();

    private AtomicInteger idCounter = new AtomicInteger();

    private SparseArray<Intent> pendingActivities = new SparseArray<Intent>();

    private Context context;

    public static SFServiceHelper getInstance(Context app){
        if(instance == null) instance = new SFServiceHelper(app);
        return instance;
    }

    public static SFServiceHelper getInstance(){
        return instance;
    }

    private SFServiceHelper(Context app) {
        this.context = app;
    }

    public void addListener(SFServiceCallbackListener currentListener) {
        currentListeners.add(currentListener);
    }

    public void removeListener(SFServiceCallbackListener currentListener) {
        currentListeners.remove(currentListener);
    }


    public int loadFeedMetaAction(String feedUrl) {
        final int requestId = createId();
        Intent i = createIntent(context, new LoadFeedMetaCommand(feedUrl), requestId);
        return runRequest(requestId, i);
    }
    public int loadFeedEntriesAction(String feedUrl) {
        final int requestId = createId();
        Intent i = createIntent(context, new LoadFeedEntriesCommand(feedUrl), requestId);
        return runRequest(requestId, i);
    }
    public int updateFeedAction(String feedUrl) {
        final int requestId = createId();
        Intent i = createIntent(context, new UpdateFeedCommand(feedUrl), requestId);
        return runRequest(requestId, i);
    }

    public void cancelCommand(int requestId) {
        Intent i = new Intent(context, SFCommandExecutorService.class);
        i.setAction(SFCommandExecutorService.ACTION_CANCEL_COMMAND);
        i.putExtra(SFCommandExecutorService.EXTRA_REQUEST_ID, requestId);

        context.startService(i);
        pendingActivities.remove(requestId);
    }

    public boolean isPending(int requestId) {
        return pendingActivities.get(requestId) != null;
    }

    public boolean check(Intent intent, Class<? extends SFBaseCommand> _class) {
        Parcelable commandExtra = intent.getParcelableExtra(SFCommandExecutorService.EXTRA_COMMAND);
        return commandExtra != null && commandExtra.getClass().equals(_class);
    }

    private int createId() {
        return idCounter.getAndIncrement();
    }

    private int runRequest(final int requestId, Intent i) {
        pendingActivities.append(requestId, i);
        context.startService(i);
        return requestId;
    }

    private Intent createIntent(final Context context, SFBaseCommand command, final int requestId) {
        Intent i = new Intent(context, SFCommandExecutorService.class);
        i.setAction(SFCommandExecutorService.ACTION_EXECUTE_COMMAND);

        i.putExtra(SFCommandExecutorService.EXTRA_COMMAND, command);
        i.putExtra(SFCommandExecutorService.EXTRA_REQUEST_ID, requestId);
        i.putExtra(SFCommandExecutorService.EXTRA_STATUS_RECEIVER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                Intent originalIntent = pendingActivities.get(requestId);
                if (isPending(requestId)) {
                    if (resultCode != SFBaseCommand.RESPONSE_PROGRESS) {
                        pendingActivities.remove(requestId);
                    }

                    for (SFServiceCallbackListener currentListener : currentListeners) {
                        if (currentListener != null) {
                            currentListener.onServiceCallback(requestId, originalIntent, resultCode, resultData);
                        }
                    }
                }
            }
        });

        return i;
    }

}
