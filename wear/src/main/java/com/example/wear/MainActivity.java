package com.example.wear;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends WearableActivity {
    private static final String TAG = "MainActivity";
    private static int REQUEST_CODE = 1;
    private static final String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BODY_SENSORS,
    };

    private SensorData sensorData;
    private Button startButton;
    private TextView text;
    private Button historyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(permissions, REQUEST_CODE);

        // Enables Always-on
        setAmbientEnabled();

        sensorData = new SensorData(this);

        startButton = (Button) findViewById(R.id.start_button);
        text = (TextView) findViewById(R.id.text);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO start recording
                if (sensorData.recording) {
                    startButton.setBackground(getResources().getDrawable(R.drawable.start));
                    text.setText(R.string.sleep);
                    sensorData.endRecord(MainActivity.this);
                } else {
                    startButton.setBackground(getResources().getDrawable(R.drawable.pause));
                    text.setText(R.string.wake_up);
                    sensorData.startRecord();
                }
            }
        });

        historyButton = (Button) findViewById(R.id.history_button);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO start history activity
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
        Log.d(TAG, this.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            }
        }
    }
}
