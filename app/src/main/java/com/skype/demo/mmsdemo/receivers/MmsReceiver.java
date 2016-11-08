package com.skype.demo.mmsdemo.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;

import java.io.File;
import java.io.IOException;
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
            final File outputDir = context.getCacheDir();
            try {
                //
                // Create a temporary file for the downloaded message and put its URI to the "location"
                // exra value of the download result intent.
                //
                final File outputFile = File.createTempFile("mms", ".dat", outputDir);
                final Uri contentUri = Uri.fromFile(outputFile);
                final Intent downloadIntent = new Intent(MMS_RECEIVED_ACTION);
                downloadIntent.putExtra("location", contentUri.toString());
                final PendingIntent pi = PendingIntent.getBroadcast(context, 0, downloadIntent, 0);
                //
                // Invoke the SMS manager and tell it to broadcast the intent with the location of the downloaded message.
                //
                SmsManager.getDefault().downloadMultimediaMessage(context, location, contentUri, null, pi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
