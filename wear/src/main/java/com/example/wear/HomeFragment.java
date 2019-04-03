package com.example.wear;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class HomeFragment extends Fragment {
    public static final int DETECT_NONE = 0;
    public static final int DETECT_SNORE = 1;
    public static int selectedDetection = DETECT_NONE;

    private Button startButton;
    private DetectorThread detectorThread;
    private RecorderThread recorderThread;
    private Toast mToast;
    private Handler rhandler = new Handler();
    private Handler showhandler = null;
    private Handler alarmhandler = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        startButton = (Button) view.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SensorData.get(getContext()).recording) {
                    SensorData.get(getContext()).startRecord();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecordingFragment()).commit();

                    // 鼾声检测
                    selectedDetection = DETECT_SNORE;
                    recorderThread = new RecorderThread(showhandler);
                    recorderThread.start();
                    Log.i("main", "record start");
                    detectorThread = new DetectorThread(recorderThread,
                            alarmhandler);
                    detectorThread.start();
                    Log.i("main", "detect start");
                    //drawThread = new DrawThread(sfv.getHeight() / 2, sfv, mPaint);
                    //drawThread.start();
                    // clsOscilloscope.baseLine = sfv.getHeight() / 2;
                    // clsOscilloscope.Start(audioRecord, recBufSize, sfv, mPaint);

                    mToast = Toast.makeText(getContext(),
                            "Recording & Detecting start", Toast.LENGTH_LONG);
                    mToast.show();
                }
            }
        });

        return view;
    }
}
