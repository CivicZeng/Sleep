package com.example.wear;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends WearableActivity {
    private static int REQUEST_CODE = 1;
    private static final String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BODY_SENSORS,
    };

    private Button mButton;
    private SensorData mSensorData;
    private Timer timer;

    public static long start_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_time = System.currentTimeMillis();

        mButton = (Button) findViewById(R.id.button);

        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(permissions, REQUEST_CODE);

        mSensorData = new SensorData(this);

        // Enables Always-on
        setAmbientEnabled();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSensorData.recording) {
                    mButton.setText(R.string.sleep);
                    mSensorData.endRecord();
                    mButton.setText(mSensorData.sleepResult());
                } else {
                    mButton.setText(R.string.wake_up);
                    mSensorData.startRecord();
                }
            }
        });

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mSensorData.startHeartReader();
            }
        }, 0, 60 * 60 * 1000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mSensorData.stopHeartReader();
            }
        }, 1 * 30 * 1000, 60 * 60 * 1000);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                updateUI();
//            }
//        }, 0, 60 * 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorData.destroy();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateUI();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        updateUI();
    }

    private void updateUI() {
        if (mSensorData.recording) {
            int second = (int) (System.currentTimeMillis() - mSensorData.startTime) / 1000;
            int hour = second / 3600;
            int minute = (second - hour * 3600) / 60;
            second = second - hour * 3600 - minute * 60;
            mButton.setText("WAKE UP" + "\n睡眠时长： " + String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second));
            Log.d("Main", String.valueOf(System.currentTimeMillis() - mSensorData.startTime));
        }
    }
}
