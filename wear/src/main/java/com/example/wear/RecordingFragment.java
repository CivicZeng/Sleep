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

public class RecordingFragment extends Fragment {
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
                }
            }
        });

        sleepTime = view.findViewById(R.id.sleep_time);
        heartRate = view.findViewById(R.id.heart_rate);
        snoring = view.findViewById(R.id.snoring);
        accelerator = view.findViewById(R.id.accelerator);
        light = view.findViewById(R.id.light);

        return view;
    }

    private void updateUI() {
    }
}
