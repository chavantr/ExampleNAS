package com.mywings.appschedulling.locally;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import com.mywings.appschedulling.stats.model.AppMetadata;

public class AppSchedulingDatabaseHelper extends SQLiteOpenHelper {

    //id,name,icon,packagename,localdirectory,numoffiles,size,show,synced,upload

    private final String CREATE_APP_METADATA = "CREATE TABLE IF NOT EXISTS APP_METADATA(id INTEGER PRIMARY KEY AUTOINCREMENT,name text,packagename text,localdirectory text,numoffiles INTEGER,size INTEGER,show text,synced text,upload text,serverurl text)";

    //private final String CREATE_APP_MASTER = "CREATE TABLE IF NOT EXISTS APP_MASTER(";

    public AppSchedulingDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_APP_METADATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public long saveInformationOfApp(AppMetadata appMetadata) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues _contentValues = new ContentValues();
        _contentValues.put("name", appMetadata.getName());
        _contentValues.put("packagename", appMetadata.getPackageName());
        _contentValues.put("localdirectory", appMetadata.getLocalDirectory());
        _contentValues.put("numoffiles", appMetadata.getNumOfFiles());
        _contentValues.put("size", appMetadata.getSize());
        _contentValues.put("show", appMetadata.getShow());
        _contentValues.put("synced", appMetadata.getSynced());
        _contentValues.put("upload", appMetadata.getUpload());
        _contentValues.put("serverurl", appMetadata.getServerUrl());
        return db.insert("APP_METADATA", null, _contentValues);
    }


}
