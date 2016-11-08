package com.skype.demo.mmsdemo.services;

import android.app.IntentService;
import android.content.Intent;

public final class SmsService extends IntentService {

    public SmsService() {
        super(Thread.currentThread().getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
