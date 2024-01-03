package com.example.myapplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.Nullable;

/**
 * author : 王星星
 * date : 2023/8/3 14:59
 * email : 1099420259@qq.com
 * description :
 */
public class SharedDataProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared_data", Context.MODE_PRIVATE);
        String sharedData = sharedPreferences.getString("shared_key", "");

        // 将共享数据包装成Cursor并返回
        MatrixCursor cursor = new MatrixCursor(new String[]{"data"});
        cursor.addRow(new Object[]{sharedData});
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

