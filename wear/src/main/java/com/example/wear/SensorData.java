package com.example.wear;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.LinkedList;

public class SensorData {
    private static final String TAG = "SensorData";

    private static SensorData mSensorData;

    private SensorManager sensorManager;
    private Sensor accelSensor, gyroSensor, magnSeneor, heartRate, lightSensor;
    private SleepRecord sleepRecord;
    private DecimalFormat decimalFormat = new DecimalFormat("##.00%");
    private double[] accelValue = new double[3];
    private double[] gyroValue = new double[3];
    private double[] magnValue = new double[3];
    private double heartRateValue, lightValue;
    public double std = 0;
    public boolean recording = false;
    private String sleepState = "fws";
    public long startTime = 0;
    private long sleepTime = 0, fws = 0, fas = 0, sws = 0;

    public LinkedList<double[]> list = new LinkedList<>();

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accelValue[0] = event.values[0];
                    accelValue[1] = event.values[1];
                    accelValue[2] = event.values[2];
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    gyroValue[0] = event.values[0];
                    gyroValue[1] = event.values[1];
                    gyroValue[2] = event.values[2];
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magnValue[0] = event.values[0];
                    magnValue[1] = event.values[1];
                    magnValue[2] = event.values[2];
                    break;
                case Sensor.TYPE_LIGHT:
                    lightValue = event.values[0];
                    break;
            }
            list.add(new double[]{accelValue[0], accelValue[1], accelValue[2], gyroValue[0], gyroValue[1], gyroValue[2], magnValue[0], magnValue[1], magnValue[2], System.currentTimeMillis()});
            //TODO 替换std提高精度
            if (list.size() > 120) {
                std = Math.sqrt(Math.pow(std, 2) + (Math.pow(magnValue[0], 2) - Math.pow(list.remove()[3], 2)) / 60);
                if (std < 1.17) {
                    if (sleepState.equals("fws")) {
                        sleepState = "sws";
                        fws += System.currentTimeMillis() - sleepTime;
                        sleepTime = System.currentTimeMillis();
                    } else if (sleepState.equals("fas")) {
                        sleepState = "sws";
                        fas += System.currentTimeMillis() - sleepTime;
                        sleepTime = System.currentTimeMillis();
                    }
                } else if (std < 5.31) {
                    if (sleepState.equals("fws")) {
                        sleepState = "fas";
                        fws += System.currentTimeMillis() - sleepTime;
                        sleepTime = System.currentTimeMillis();
                    } else if (sleepState.equals("sws")) {
                        sleepState = "fas";
                        sws += System.currentTimeMillis() - sleepTime;
                        sleepTime = System.currentTimeMillis();
                    }
                } else {
                    if (sleepState.equals("sws")) {
                        sleepState = "fws";
                        sws += System.currentTimeMillis() - sleepTime;
                        sleepTime = System.currentTimeMillis();
                    } else if (sleepState.equals("fas")) {
                        sleepState = "fws";
                        fas += System.currentTimeMillis() - sleepTime;
                        sleepTime = System.currentTimeMillis();
                    }
                }
                list.removeLast();
            } else {
                std = Math.sqrt((Math.pow(std, 2) * (list.size() - 1) + Math.pow(magnValue[0], 2)) / list.size());
            }
            Log.d(TAG, "sensor std: " + String.valueOf(std));
//            Log.d(TAG, String.valueOf(lightValue));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private SensorEventListener heartRateListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                heartRateValue = event.values[0];
                Log.d("Heart", "Heart rate: " + String.valueOf(heartRateValue));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public static SensorData get(Context context) {
        if (mSensorData == null) {
            mSensorData = new SensorData(context);
        }
        return mSensorData;
    }

    public SensorData(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnSeneor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        heartRate = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(listener, gyroSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(listener, magnSeneor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public double getStd() {
        return std;
    }

    public double getHeartRateValue() {
        return heartRateValue;
    }

    public double getLightValue() {
        return lightValue;
    }

    public void startHeartReader() {
        Log.d("Heart", "startHeartReader");
        sensorManager.registerListener(heartRateListener, heartRate, SensorManager.SENSOR_DELAY_UI);
        Log.d("Heart", String.valueOf(heartRate.isWakeUpSensor()));
    }

    public void stopHeartReader() {
        Log.d("Heart", "stopHeartReader");
        sensorManager.unregisterListener(heartRateListener);
        Log.d("Heart", String.valueOf(accelSensor.isWakeUpSensor()));
    }

    public void startRecord() {
        recording = true;
        startTime = System.currentTimeMillis();
        sleepTime = startTime;
        sleepRecord = new SleepRecord(startTime);
    }

    public void endRecord(Context context) {
        switch (sleepState) {
            case "fws":
                fws += System.currentTimeMillis() - sleepTime;
                break;
            case "fas":
                fas += System.currentTimeMillis() - sleepTime;
                break;
            case "sws":
                sws += System.currentTimeMillis() - sleepTime;
                break;
        }

        sleepRecord.setFws(fws);
        sleepRecord.setFas(fas);
        sleepRecord.setSws(sws);
        sleepRecord.setHeartRate((int) heartRateValue);
        Log.d(TAG, String.valueOf(fws) + " " + String.valueOf(fas) + " " + String.valueOf(sws));

        SleepRecordManager.get(context).addRecord(sleepRecord);
        std = 0;
        list.clear();
        recording = false;
    }

    public void destroy() {
        sensorManager.unregisterListener(listener);
        sensorManager.unregisterListener(heartRateListener);
    }
}
