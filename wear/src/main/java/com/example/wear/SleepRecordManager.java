package com.example.wear;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.wear.database.SleepBaseHelper;
import com.example.wear.database.SleepCursorWrapper;
import com.example.wear.database.SleepDbSchema;

public class SleepRecordManager {
    private static SleepRecordManager sSleepRecordManager;
    private static final String TAG = "CrimeLab";

    private SQLiteDatabase mSleepRecordDB;

    private SleepRecordManager(Context context) {
        mSleepRecordDB = new SleepBaseHelper(context.getApplicationContext()).getWritableDatabase();
    }

    public static SleepRecordManager get(Context context) {
        if (sSleepRecordManager == null) {
            Log.d(TAG, "SleepRecordManager is null");
            sSleepRecordManager = new SleepRecordManager(context);
        }
        Log.d(TAG, "SleepRecordManager is not null");
        return sSleepRecordManager;
    }

    public void addRecord(SleepRecord sleepRecord) {
        ContentValues values = getContentValues(sleepRecord);
        mSleepRecordDB.insert(SleepDbSchema.SleepTable.NAME, null, values);
        Log.d(TAG, "insert complete");
    }

    public void deleteRecord(SleepRecord sleepRecord) {
        mSleepRecordDB.delete(SleepDbSchema.SleepTable.NAME, SleepDbSchema.SleepTable.Cols.DATE + " = ?", new String[]{String.valueOf(sleepRecord.getDate())});
        Log.d(TAG, "delete complete");
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public int getRecordCount() {
        int count = 0;
        SleepCursorWrapper cursorWrapper = query(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                cursorWrapper.moveToNext();
                count += 1;
            }
        } finally {
            cursorWrapper.close();
        }
        if (count == 0) {
            addDefaultRecord();
        }
        return count;
    }

    private ContentValues getContentValues(SleepRecord sleepRecord) {
        ContentValues values = new ContentValues();
        values.put(SleepDbSchema.SleepTable.Cols.DATE, sleepRecord.getDate());
        values.put(SleepDbSchema.SleepTable.Cols.FWS, sleepRecord.getFws());
        values.put(SleepDbSchema.SleepTable.Cols.FAS, sleepRecord.getFas());
        values.put(SleepDbSchema.SleepTable.Cols.SWS, sleepRecord.getSws());
        values.put(SleepDbSchema.SleepTable.Cols.HEARTRATE, sleepRecord.getHeartRate());
        return values;
    }

    private SleepCursorWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mSleepRecordDB.query(
                SleepDbSchema.SleepTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new SleepCursorWrapper(cursor);
    }

    private void addDefaultRecord() {
    }
}
