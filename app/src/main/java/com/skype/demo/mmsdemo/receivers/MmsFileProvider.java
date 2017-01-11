package com.skype.demo.mmsdemo.receivers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by pakarpen on 1/10/17.
 */

public class MmsFileProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        File file = new File(getContext().getCacheDir(), uri.getPath());
        file.delete();
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String fileMode) throws FileNotFoundException {
        File file = new File(getContext().getCacheDir(), uri.getPath());
        int mode = (TextUtils.equals(fileMode, "r") ? ParcelFileDescriptor.MODE_READ_ONLY :
                ParcelFileDescriptor.MODE_WRITE_ONLY
                        |ParcelFileDescriptor.MODE_TRUNCATE
                        |ParcelFileDescriptor.MODE_CREATE);
        return ParcelFileDescriptor.open(file, mode);    }
}
