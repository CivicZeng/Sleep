package com.example.wear;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
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
            Manifest.permission.RECORD_AUDIO
    };

    private Button startButton;
    private TextView text;
    private Button historyButton;
    private WearableNavigationDrawer mWearableNavigationDrawer;
    private HomeFragment homeFragment;
    private AlarmFragment alarmFragment;
    private HistoryFragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(permissions, REQUEST_CODE);

        // Enables Always-on
        setAmbientEnabled();

        mWearableNavigationDrawer = (WearableNavigationDrawer) findViewById(R.id.top_navigation_drawer);
        mWearableNavigationDrawer.setAdapter(new NavigationAdapter());

        homeFragment = new HomeFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

//        startButton = (Button) findViewById(R.id.start_button);
//        text = (TextView) findViewById(R.id.text);
//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO start recording
//                if (sensorData.recording) {
//                    startButton.setBackground(getResources().getDrawable(R.drawable.start));
//                    text.setText(R.string.sleep);
//                    sensorData.endRecord(MainActivity.this);
//                } else {
//                    startButton.setBackground(getResources().getDrawable(R.drawable.pause));
//                    text.setText(R.string.wake_up);
//                    sensorData.startRecord();
//                }
//            }
//        });
//
//        historyButton = (Button) findViewById(R.id.history_button);
//        historyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO start history activity
//                Intent intent = new Intent(MainActivity.this, HistoryFragment.class);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SensorData.get(this).destroy();
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

    private final class NavigationAdapter extends WearableNavigationDrawer.WearableNavigationDrawerAdapter {
        @Override
        public String getItemText(int i) {
            Log.d(TAG, "getItemTextId: " + String.valueOf(i));
            switch (i) {
                case 0:
                    return "SLEEP";
                case 1:
                    return "HISTORY";
                case 2:
                    return "ALARM";
                default:
                    return null;
            }
        }

        @Override
        public Drawable getItemDrawable(int i) {
            Log.d(TAG, "getItemDrawable: " + String.valueOf(i));
            switch (i) {
                case 0:
                    return getDrawable(R.drawable.sleep_icon);
                case 1:
                    return getDrawable(R.drawable.history_icon);
                case 2:
                    return getDrawable(R.drawable.alarm_icon);
                default:
                    return null;
            }
        }

        @Override
        public void onItemSelected(int i) {
            Log.d(TAG, "onItemSelected: " + String.valueOf(i));
            switch (i) {
                case 0:
                    if (homeFragment == null)
                        homeFragment = new HomeFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment, "HOMEFRAGMENT").commit();
                    break;
                case 1:
                    if (historyFragment == null)
                        historyFragment = new HistoryFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, historyFragment, "HISTORYFRAGMENT").commit();
                    break;
                case 2:
                    if (alarmFragment == null)
                        alarmFragment = new AlarmFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, alarmFragment, "ALARMFRAGMENT").commit();
                    break;
            }
        }

        @Override
        public int getCount() {
//            return 3;
            return 2;
        }
    }
}
