package com.mywings.appschedulling.locally;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class AppSchedulingDatabaseHelper extends SQLiteOpenHelper {

    //id, name,packagename,localdirectory,numoffiles,size,show,

    private final String CREATE_APP_METADATA = "CREATE TABLE IF NOT EXISTS APP_METADATA(id INTEGER PRIMARY KEY AUTOINCREMENT,name text,packagename text,localdirectory text,numoffiles INTEGER,size INTEGER,show text)";

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


}
