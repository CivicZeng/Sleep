package com.example.wear.database;

public class SleepDbSchema {
    public static final class SleepTable {
        public static final String NAME = "sleep";

        public static final class Cols {
            public static final String DATE = "date";
            public static final String FWS = "fws";
            public static final String FAS = "fas";
            public static final String SWS = "sws";
            public static final String HEARTRATE = "heart_rate";
        }
    }
}
