package com.example.wear.database;

import android.database.Cursor;
import android.database.CursorWrapper;


import com.example.wear.SleepRecord;
import com.example.wear.database.SleepDbSchema.SleepTable;

public class SleepCursorWrapper extends CursorWrapper {
    public SleepCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public SleepRecord getRecord() {
        long date = getLong(getColumnIndex(SleepTable.Cols.DATE));
        double fws = getDouble(getColumnIndex(SleepTable.Cols.FWS));
        double fas = getDouble(getColumnIndex(SleepTable.Cols.FAS));
        double sws = getDouble(getColumnIndex(SleepTable.Cols.SWS));
        int heartRate = getInt(getColumnIndex(SleepTable.Cols.HEARTRATE));

        SleepRecord sleepRecord = new SleepRecord(date);
        sleepRecord.setFws(fws);
        sleepRecord.setFas(fas);
        sleepRecord.setSws(sws);
        sleepRecord.setHeartRate(heartRate);

        return sleepRecord;
    }
}
