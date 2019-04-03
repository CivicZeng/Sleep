package com.example.wear;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivitySnoring extends WearableActivity {

    private TextView mTextView;

    public static final int DETECT_NONE = 0;
    public static final int DETECT_SNORE = 1;
    public static int selectedDetection = DETECT_NONE;

    private DetectorThread detectorThread;
    private RecorderThread recorderThread;

    public static int snoreValue = 0;
    private Button mSleepRecordBtn, mAlarmBtn, mRecordBtn, mTestBtn;
    private TextView txtAbs;
/*



    */
    private View mainView;
    private Toast mToast;
    private Handler rhandler = new Handler();
    private Handler showhandler = null;
    private Handler alarmhandler = null;

    private Intent intent;
    private PendingIntent pendingIntent;
    private AlarmManager am;

/*    private SurfaceView sfv;
    private Paint mPaint;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        txtAbs = (TextView) findViewById(R.id.text);
        // Enables Always-on
        setAmbientEnabled();
        Log.i("main","before per");
        if (ActivityCompat.checkSelfPermission(MainActivitySnoring.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.i("main","ask per");
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.RECORD_AUDIO}, 123);

        }
        Log.i("main","after per");
//        mSleepRecordBtn = (Button) this.findViewById(R.id.btnSleepRecord);
        //mAlarmBtn = (Button) findViewById(R.id.btnSelectAlarm);
        //mRecordBtn = (Button) findViewById(R.id.btnRecordAlarm);
        //mTestBtn = (Button) findViewById(R.id.btnAlarmTest);
        //txtAbs = (TextView) findViewById(R.id.txtaverageAbsValue);
    //    intent = new Intent(MainActivity.this, AlarmReceiverActivity.class);//从main到alarmservice的Intent
    //    pendingIntent = PendingIntent.getActivity(MainActivity.this, 2, intent,
    //            PendingIntent.FLAG_CANCEL_CURRENT);//实际上使用了一种延时的intent,适用于设定闹钟的场景
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        /**
         * show variable handler
         */
        showhandler = new Handler() {
            public void handleMessage(Message msg) {
                txtAbs.setText(msg.obj.toString());
            }
        };

        /**
         * Output alarm handler
         */
/*        alarmhandler = new Handler() {
            public void handleMessage(Message msg) {//TODO 这个alarm我完全不需要把
                int interval = 1;
                int i = msg.arg1;//TODO 这个i是干嘛的，可能是控制alarm的音量的
                setLevel(i);
                AlarmStaticVariables.level = AlarmStaticVariables.level1;
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + (interval * 1000), pendingIntent);//am.set()设置一次性闹钟，start_time是一秒之后，转移到alarmreciver_class
            }
        };*/

        /**
         * Sleep Record Button
         */

        mSleepRecordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                selectedDetection = DETECT_SNORE;
              //  Log.i("main","before permission");
/*                if (!voicePermission()){//没有权限,自己写一段假的录音代码,这段录音代码其实无效.

                    MediaRecorder recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                    recorder.setAudioChannels(1);
                    recorder.setAudioSamplingRate(8000);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                    //recorder.setOutputFile(DataURI.CreateTempFileName(""));//这里给个假的地址,因为这段录音是无效的.
                    try {
                        recorder.prepare();
                        recorder.start();//要开始录音时,这里就会弹出提示框了,如果不给权限.我们有异常处理,而且下次想录音时 还是会有此提示.
                        recorder.stop();
                        recorder.release();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                   // return false;//这里是因为外层层是一个长按事件,返回false表示处理过了(OnLongClickListener).
                }*/

              //  Log.i("main","after permission");
                // alarmThread = new AlarmThread(pendingIntent, am);
                recorderThread = new RecorderThread(showhandler);
                recorderThread.start();
                Log.i("main","record start");
                detectorThread = new DetectorThread(recorderThread,
                        alarmhandler);
                detectorThread.start();
                Log.i("main","detect start");
                //drawThread = new DrawThread(sfv.getHeight() / 2, sfv, mPaint);
                //drawThread.start();
                // clsOscilloscope.baseLine = sfv.getHeight() / 2;
                // clsOscilloscope.Start(audioRecord, recBufSize, sfv, mPaint);

                mToast = Toast.makeText(getApplicationContext(),
                        "Recording & Detecting start", Toast.LENGTH_LONG);
                mToast.show();
                // goListeningView();
            }
        });




        /**
         * Select alarm Button
         */
/*        mAlarmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {//TODO alarmbtm好像也是不需要的
                Intent intent = new Intent(MainActivity.this,
                        AlarmSelectActivity.class);
                startActivity(intent);
            }
        });*/

        /**
         * Record name Button
         */
/*        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                rhandler.removeCallbacks(recordActivity);
                rhandler.postDelayed(recordActivity, 1000);
            }
        });*/

        /**
         * Test
         */
/*        mTestBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int level = 1;
                setLevel(level);
                startOneShoot();
            }
        });*/

    }

    private  boolean voicePermission(){
        if (PackageManager.PERMISSION_GRANTED ==   ContextCompat.checkSelfPermission(MainActivitySnoring.this, android.Manifest.permission.RECORD_AUDIO))
            return 1==1;
        else
            return 1==0;
    }

/*    private Runnable recordActivity = new Runnable() {
        public void run() {
            Intent intent = new Intent(MainActivity.this,
                    AlarmRecordActivity.class);
            startActivity(intent);
        }
    };*/

/*    public void startOneShoot() {
        int i = 5;
        am.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (i * 1000), pendingIntent);
    }*/

/*    public void setLevel(int l) {
        switch (l) {
            case 0:
                AlarmStaticVariables.level = AlarmStaticVariables.level0;
                break;
            case 1:
                AlarmStaticVariables.level = AlarmStaticVariables.level1;
                break;
            case 2:
                AlarmStaticVariables.level = AlarmStaticVariables.level2;
                break;
            case 3:
                AlarmStaticVariables.level = AlarmStaticVariables.level3;
                break;
            default:
                AlarmStaticVariables.level = AlarmStaticVariables.level1;
                break;
        }
    }*/

/*    private void goHomeView() {
        setContentView(mainView);
        if (recorderThread != null) {
            recorderThread.stopRecording();
            recorderThread = null;
        }
        if (detectorThread != null) {
            detectorThread.stopDetection();
            detectorThread = null;
        }
        selectedDetection = DETECT_NONE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Quit demo");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                am.cancel(pendingIntent);
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            goHomeView();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }*/


}
