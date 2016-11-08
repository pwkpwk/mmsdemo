package com.skype.demo.mmsdemo.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.provider.Telephony;
import android.support.annotation.NonNull;

import com.skype.demo.mmsdemo.BR;

public final class MainViewModel extends BaseObservable {
    private final Context context;

    public MainViewModel(@NonNull final Context context) {
        this.context = context;
    }

    @Bindable
    public final boolean getIsDefault() {
        final String packageName = this.context.getPackageName();
        return Telephony.Sms.getDefaultSmsPackage(this.context).equals(packageName);
    }

    public final void becomeDefault() {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        final String packageName = this.context.getPackageName();
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
        this.context.startActivity(intent);
    }

    public final void updateBindings() {
        this.notifyPropertyChanged(BR.isDefault);
    }
}
