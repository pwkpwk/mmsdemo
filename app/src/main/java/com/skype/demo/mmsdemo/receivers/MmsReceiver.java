package com.skype.demo.mmsdemo.receivers;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class MmsReceiver extends BroadcastReceiver {

    public static final String MMS_RECEIVED_ACTION = "com.skype.demo.mmsdemo.MMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.WAP_PUSH_DELIVER_ACTION.equals(intent.getAction())) {
            //
            // Received an MMS WAP push as the default app
            //
            final byte[] payload = intent.getByteArrayExtra("data"); // is there a constant for "data"?

            if (payload != null) {
                handleReceivedMessage(context, payload);
            }
        }
    }

    private final void handleReceivedMessage(final Context context, final byte[] payload) {
        final String location = extractLocation(payload);

        if (location != null) {
            //
            // Download the message
            //
            try {
                final Context appContext = context.getApplicationContext();
                //
                // Create a temporary file for the downloaded message and put its URI to the "location"
                // exra value of the download result intent.
                //
                final File outputFile = File.createTempFile("mms", ".dat");
                Uri.Builder builder = new Uri.Builder().authority("com.skype.demo.mmsdemo.receivers.FileSystemContentProvider").scheme(ContentResolver.SCHEME_CONTENT);
                final Uri contentUri = builder.path(outputFile.getPath()).build();

                String intentAction = "com.skype.demo.mmsdemo.MMS_RECEIVED." + outputFile.getName();
                Intent intent = new Intent(intentAction).putExtra("location", contentUri.toString());
                final PendingIntent pending = PendingIntent.getBroadcast(appContext, 0, intent, 0);

                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(final Context context, final Intent intent) {
                        final BroadcastReceiver thisReceiver = this;

                        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                            @Override
                            public void run() {
                                onReceiveDownload(getResultCode(), context, intent);
                                appContext.unregisterReceiver(thisReceiver);
                                Log.v("PLOP!", "Unregistered receiver");
                            }
                        });
                    }
                };

                context.getApplicationContext().registerReceiver(receiver, new IntentFilter(intentAction));

                AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        //
                        // Invoke the SMS manager and tell it to broadcast the intent with the location of the downloaded message.
                        //
                        try {
                            SmsManager.getDefault().downloadMultimediaMessage(
                                    appContext,
                                    location,
                                    contentUri,
                                    null,
                                    pending);
                        } catch (Throwable ex) {
                            appContext.unregisterReceiver(receiver);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final void onReceiveDownload(int resultCode, Context context, Intent intent) {
        if (Activity.RESULT_OK == resultCode) {
            ContentResolver resolver = context.getContentResolver();
            String uriString = intent.getStringExtra("location");
            Uri uri = Uri.parse(uriString);
            try {
                InputStream stream = resolver.openInputStream(uri);
                handleDownloadedMessage(stream);
            } catch (FileNotFoundException ex) {
                // Should not happen
            } finally {
                resolver.delete(uri, null, null);
            }
        }
    }

    private final void handleDownloadedMessage(InputStream messageBody) {
        byte[] buffer = new byte[1024];
        int readBytes;
        int totalLength = 0;

        try {
            do {
                readBytes = messageBody.read(buffer);
                totalLength += readBytes;
            } while (readBytes == buffer.length);
        } catch (IOException ex) {
        }

        Log.i("PLOP", "Read bytes=" + totalLength);
    }

    private final String extractLocation(@NonNull final byte[] payload) {
        String location = null;
        int i = 0;

        while (location == null && i < payload.length) {
            int code = payload[i];

            if (code < 0) {
                int j;
                code = 256 + code;

                switch (code) {
                    case 0x83:
                        //
                        // The location header followed by the zero-terminated URI string.
                        //
                        for (j = i + 1; j < payload.length && payload[j] != 0; ++j) {}
                        try {
                            location = new String(payload, i + 1, j - i - 1, "US-ASCII");
                        } catch (UnsupportedEncodingException ex) {
                            i = j + 1; // crud happens; skip the location, let the loop run
                        }
                        break;

                    default:
                        ++i; // not interested in other headers now
                        break;
                }
            } else {
                ++i;
            }
        }

        return location;
    }
}
