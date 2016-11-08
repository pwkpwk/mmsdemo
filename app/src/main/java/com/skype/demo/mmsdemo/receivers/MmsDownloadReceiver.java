package com.skype.demo.mmsdemo.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public final class MmsDownloadReceiver extends BroadcastReceiver {

    private static final String TAG = MmsDownloadReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            final URI uri = new URI(intent.getStringExtra("location"));
            final File contentFile = new File(uri);

            handleDownloadedMessage(getResultCode(), contentFile);

            contentFile.delete();
        } catch (URISyntaxException e) {
        }
    }

    private final void handleDownloadedMessage(final int resultCode, @NonNull final File messageContent) {
        Log.i(TAG, "Message length=" + messageContent.length());

        if (Activity.RESULT_OK == resultCode) {
            //
            // Parse the downloaded message
            //
        }
    }
}
