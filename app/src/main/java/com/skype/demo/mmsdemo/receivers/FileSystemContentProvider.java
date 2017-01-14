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
 * Content provider that wraps the File API to open and delete files in the file system.
 */

public class FileSystemContentProvider extends ContentProvider {

    private static final int READ_MODE = ParcelFileDescriptor.MODE_READ_ONLY;
    private static final int WRITE_MODE = ParcelFileDescriptor.MODE_WRITE_ONLY|ParcelFileDescriptor.MODE_TRUNCATE|ParcelFileDescriptor.MODE_CREATE;

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
        int affected = 0;
        File file = new File(uri.getPath());

        if (file.exists() && file.delete()) {
            affected = 1;
        }
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String fileMode) throws FileNotFoundException {
        int mode = TextUtils.equals(fileMode, "r") ? READ_MODE : WRITE_MODE;
        return ParcelFileDescriptor.open(new File(uri.getPath()), mode);
    }
}
