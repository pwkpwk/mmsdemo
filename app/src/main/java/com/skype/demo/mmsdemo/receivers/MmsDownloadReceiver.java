package com.skype.demo.mmsdemo.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class MmsDownloadReceiver extends BroadcastReceiver {

    private static final String TAG = MmsDownloadReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        int resultCode = getResultCode();

        try {
            String uriString = intent.getStringExtra("location");
            Uri uri = Uri.parse(uriString);
            ContentResolver resolver = context.getContentResolver();
            InputStream stream = resolver.openInputStream(uri);

            handleDownloadedMessage(resultCode, stream);
            resolver.delete(uri, null, null);
        } catch (FileNotFoundException e) {
        }
    }

    private final void handleDownloadedMessage(final int resultCode, @NonNull final InputStream stream) {
        if (Activity.RESULT_OK == resultCode) {
            //
            // Parse the downloaded message
            //
            byte[] data = new byte[1024];
            int readBytes;

            try {
                while ((readBytes = stream.read(data)) == data.length) {
                    Log.v("PLOP!", "Read bytes=" + readBytes);
                }
            } catch (IOException e) {
            }
        }
    }
}
