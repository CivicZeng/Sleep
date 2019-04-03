package com.example.wear;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class RecorderThread extends Thread {
	private AudioRecord audioRecord;//TODO，这里有一个申请权限的问题还没确认
	private boolean isRecording;
	private int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private int sampleRate = 44100;//采样频率
	private int frameByteSize = 1024; // for 1024 fft size (16bit sample size)

	byte[] buffer;
	byte[] totalBuf;
	int cnt;

	// showVariableThread showVariable;
	Handler showhandler;

	public RecorderThread(Handler showhandler) {
		this.showhandler = showhandler;
		int recBufSize = AudioRecord.getMinBufferSize(sampleRate,
				channelConfiguration, audioEncoding); // need to be larger than
														// size of a frame
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				sampleRate, channelConfiguration, audioEncoding, recBufSize);//音频源mic,在手表里不知道是否有差别

		buffer = new byte[frameByteSize];//读取一个frame的音频用于处理，包含512个采样，也就是11ms的数据。
		totalBuf = new byte[AlarmStaticVariables.sampleSize * 2];//712548*2,totalbuf的大小，16秒的录音
		cnt = 0;//计数器，在totalbuf中存数据用得到。
	}

	public AudioRecord getAudioRecord() {
		return audioRecord;
	}

	public boolean isRecording() {
		return this.isAlive() && isRecording;
	}

	public void startRecording() {
		try {
			Log.i("Recorder","start recording");
			audioRecord.startRecording();
			isRecording = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopRecording() {
		try {
			audioRecord.stop();
			isRecording = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] getFrameBytes() {

		int bufferReadResult = audioRecord.read(buffer, 0, frameByteSize);//读取一个frame

		// analyze sound
		int totalAbsValue = 0;
		short sample = 0;
		short[] tmp = new short[frameByteSize];
		// float averageAbsValue = 0.0f;
		AlarmStaticVariables.absValue = 0.0f;

		for (int i = 0; i < frameByteSize; i += 2) {
			sample = (short) ((buffer[i]) | buffer[i + 1] << 8);
			tmp[i] = sample;
			totalAbsValue += Math.abs(sample);
		}
		AlarmStaticVariables.absValue = totalAbsValue / frameByteSize / 2;

		Message msg = new Message();
		msg.obj = AlarmStaticVariables.absValue;
		showhandler.sendMessage(msg);//handler传出absvalue的值，有一个UI专门展示abs的值
       // Log.i("recorder","msg"+AlarmStaticVariables.absValue);
		for (int i = 0; i < buffer.length; i++) {
			totalBuf[cnt++] = buffer[i];
		}

		// ----------save into buf----------------------返回totalbuf用于分析
		short[] tmpBuf = new short[bufferReadResult
				/ AlarmStaticVariables.rateX];
		for (int i = 0, ii = 0; i < tmpBuf.length; i++, ii = i
				* AlarmStaticVariables.rateX) {
			tmpBuf[i] = tmp[ii];
		}
		synchronized (AlarmStaticVariables.inBuf) {//
			AlarmStaticVariables.inBuf.add(tmpBuf);// add data
		}
		// ----------save into buf----------------------

		// System.out.println(cnt + " vs " + AlarmStaticVariables.sampleSize);
		if (cnt > AlarmStaticVariables.sampleSize) {
			cnt = 0;
			return totalBuf;//renturn 的是totalbuf,一个8秒这么长啊
		} else
			return null;
		// return buffer;
	}

	public void run() {
		startRecording();
	}

}
