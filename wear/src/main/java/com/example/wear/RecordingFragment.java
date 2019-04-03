package com.example.wear;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class RecordingFragment extends Fragment {
    private Timer timer;
    private Handler handler;
    private Button pause;
    private TextView sleepTime;
    private TextView heartRate;
    private TextView snoring;
    private TextView accelerator;
    private TextView light;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recording_fragment, container, false);

        pause = view.findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SensorData.get(getContext()).recording) {
                    SensorData.get(getContext()).endRecord(getContext());
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                }
            }
        });

        sleepTime = view.findViewById(R.id.sleep_time);
        heartRate = view.findViewById(R.id.heart_rate);
        snoring = view.findViewById(R.id.snoring);
        accelerator = view.findViewById(R.id.accelerator);
        light = view.findViewById(R.id.light);

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SensorData.get(getContext()).startHeartReader();
            }
        }, 0, 2 * 30 * 1000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SensorData.get(getContext()).stopHeartReader();
            }
        }, 2 * 2 * 1000, 2 * 30 * 1000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        }, 0, 1000);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void updateUI() {
        int second = (int) (System.currentTimeMillis() - SensorData.get(getContext()).startTime) / 1000;
        int hour = second / 3600;
        int minute = (second - hour * 3600) / 60;
        second = second - hour * 3600 - minute * 60;
        sleepTime.setText(String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second));
        heartRate.setText(String.valueOf(SensorData.get(getContext()).getHeartRateValue()));
        snoring.setText(String.valueOf(0));
        accelerator.setText(String.valueOf((int) SensorData.get(getContext()).getStd()));
        light.setText(String.valueOf(SensorData.get(getContext()).getLightValue()));
    }
}
