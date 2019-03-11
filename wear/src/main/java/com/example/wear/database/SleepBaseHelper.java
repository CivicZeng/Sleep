package com.example.wear.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wear.database.SleepDbSchema.SleepTable;

public class SleepBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "sleepBase.db";

    public SleepBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + SleepTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                SleepTable.Cols.DATE + "," +
                SleepTable.Cols.FWS + "," +
                SleepTable.Cols.FAS + "," +
                SleepTable.Cols.SWS + "," +
                SleepTable.Cols.HEARTRATE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    }
}
