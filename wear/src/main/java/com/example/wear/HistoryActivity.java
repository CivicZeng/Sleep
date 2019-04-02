package com.example.wear;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.txusballesteros.widgets.FitChart;
import com.txusballesteros.widgets.FitChartValue;

import java.util.ArrayList;
import java.util.Collection;

public class HistoryActivity extends WearableActivity {
    private static final String TAG = "HistoryActivity";

    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_list);

        setAmbientEnabled();

        recordAdapter = new RecordAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.history_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recordAdapter);
    }

    private class RecordHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        private FitChart fitChart;
        private TextView briefRecord;

        public RecordHolder(View view) {
            super(view);
            fitChart = (FitChart) view.findViewById(R.id.fit_chart);
            fitChart.setMinValue(0f);
            fitChart.setMaxValue(100f);
            briefRecord = (TextView) view.findViewById(R.id.brief_record);
        }

        private void bindCircle(int i) {
            Collection<FitChartValue> values = new ArrayList<>();
            values.add(new FitChartValue(30f + i, R.color.white));
            values.add(new FitChartValue(20f, R.color.blue));
            values.add(new FitChartValue(15f, R.color.green));
            values.add(new FitChartValue(10f, R.color.orange));
            fitChart.setValues(values);
            String msg = "日期: " + "2019/03/" + String.valueOf(i + 1) + "\n"
                    + "深度睡眠: " + String.valueOf(30f) + "\n"
                    + "浅度睡眠: " + String.valueOf(20f) + "\n"
                    + "REM: " + String.valueOf(15f) + "\n"
                    + "心率: " + String.valueOf(64);
            briefRecord.setText(msg);
        }

        @Override
        public void onClick(View view) {
            //TODO detailed record
//            Intent intent = new Intent(HistoryActivity.this, RecordActivity.class);
//            startActivity(intent);
        }
    }

    private class RecordAdapter extends RecyclerView.Adapter<RecordHolder> {
        public RecordAdapter() {
            //TODO get sleep records form database
        }

        @NonNull
        @Override
        public RecordHolder onCreateViewHolder(@NonNull ViewGroup container, int i) {
            Log.d(TAG, "create view holder");
            LayoutInflater inflater = LayoutInflater.from(HistoryActivity.this);
            View view = inflater.inflate(R.layout.list_item_record, container, false);
            return new RecordHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordHolder recordViewHolder, int i) {
            recordViewHolder.bindCircle(i);
        }

        @Override
        public int getItemCount() {
//            Log.d(TAG, getParent().toString());
//            return SleepRecordManager.get(getParent()).getRecordCount();
            return 5;
        }
    }
}
