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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public abstract class SFBaseActivity extends FragmentActivity implements SFServiceCallbackListener {
    protected static final String PROGRESS_DIALOG = "progress-dialog";
    protected int requestId = -1;

    private SFServiceHelper serviceHelper;

    protected SFApplication getApp() {
        return (SFApplication) getApplication();
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
        serviceHelper.addListener(this);

        if (requestId != -1 && !getServiceHelper().isPending(requestId)) {
            dismissProgressDialog();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceHelper = getApp().getServiceHelper();
    }


    public void cancelCommand() {
        getServiceHelper().cancelCommand(requestId);
    }

    protected void dismissProgressDialog() {
        ProgressDialogFragment progress = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(
                PROGRESS_DIALOG);
        if (progress != null) {
            progress.dismiss();
        }
    }

    protected void updateProgressDialog(int progress) {
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
            ((SFBaseActivity) getActivity()).cancelCommand();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        serviceHelper.removeListener(this);
    }

    public SFServiceHelper getServiceHelper() {
        return serviceHelper;
    }

    /**
     * Called when a service request finishes executing.
     *
     * @param requestId  original request id
     * @param requestIntent   request data
     * @param resultCode result of execution code
     * @param resultData result data
     */
    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
    }

}
