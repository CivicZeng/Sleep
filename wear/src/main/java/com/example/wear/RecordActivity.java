package com.example.wear;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

public class RecordActivity extends WearableActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_pager_activity);

        setAmbientEnabled();

//        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecordFragment()).commit();
    }
}
