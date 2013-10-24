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

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import ru.georgeee.android.gfeedreader.service.SFCommandExecutorService;

public class SFApplication extends Application {

    public static final String PACKAGE = "ru.georgeee.android.gfeedreader";

    private SFServiceHelper serviceHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceHelper = SFServiceHelper.getInstance(getApplicationContext());
        AlarmManager alarmManager =  (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), GAlarmBroadcastReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30 * 1000L, alarmIntent);
    }

    public SFServiceHelper getServiceHelper() {
        return serviceHelper;
    }

    public static SFApplication getApplication(Context context) {
        if (context instanceof SFApplication) {
            return (SFApplication) context;
        }
        return (SFApplication) context.getApplicationContext();
    }

}
