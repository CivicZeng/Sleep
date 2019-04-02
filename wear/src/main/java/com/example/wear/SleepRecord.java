package com.example.wear;

public class SleepRecord {
    private long date;
    private double fws, fas, sws;
    private int heartRate;

    public SleepRecord(long date) {
        this.date = date;
        fws = 0;
        fas = 0;
        sws = 0;
        heartRate = 0;
    }

    public long getDate() {
        return date;
    }

    public void setFws(double fws) {
        this.fws = fws;
    }

    public double getFws() {
        return fws;
    }

    public void setFas(double fas) {
        this.fas = fas;
    }

    public double getFas() {
        return fas;
    }

    public void setSws(double sws) {
        this.sws = sws;
    }

    public double getSws() {
        return sws;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getHeartRate() {
        return heartRate;
    }
}
