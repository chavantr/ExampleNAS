package com.mywings.appschedulling.locally;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import com.mywings.appschedulling.stats.model.AppMetadata;

public class AppSchedulingDatabaseHelper extends SQLiteOpenHelper {


    private final String CREATE_APP_METADATA = "CREATE TABLE IF NOT EXISTS APP_METADATA(id INTEGER PRIMARY KEY AUTOINCREMENT,name text,packagename text,localdirectory text,numoffiles INTEGER,size INTEGER,show text,synced text,upload text,serverurl text,imageicon text)";
    private final String CREATE_UNINSTALL_STATE = "CREATE TABLE IF NOT EXISTS UNINSTALL_STATE(id INTEGER PRIMARY KEY AUTOINCREMENT,packagename TEXT,status TEXT)";

    public AppSchedulingDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_APP_METADATA);
        db.execSQL(CREATE_UNINSTALL_STATE);
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
        _contentValues.put("imageicon", "");
        return db.insert("APP_METADATA", null, _contentValues);
    }

    public int checkSynced(String packageName) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {packageName};
        Cursor cursor = db.query("APP_METADATA", null, "packagename=?", args, null, null, null);
        if (null != cursor && cursor.getCount() > 0) return 1;
        return 0;
    }

    public int deleteState(String packageName) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {packageName};
        long deleted = db.delete("UNINSTALL_STATE", "packagename=?", args);
        return (int) deleted;
    }

    public int setState(String packageName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues _contentValues = new ContentValues();
        _contentValues.put("packagename", packageName);
        _contentValues.put("status", "true");
        return (int) db.insert("UNINSTALL_STATE", null, _contentValues);
    }


    public int checkState(String packageName) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {packageName};
        Cursor cursor = db.query("UNINSTALL_STATE", null, "packagename=?", args, null, null, null);
        if (null != cursor && cursor.getCount() > 0) return 1;
        return 0;
    }

}
