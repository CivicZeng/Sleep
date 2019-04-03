package com.example.wear;

//import com.musicg.*;

import android.util.Log;

import com.musicg.api.DetectionApi;
import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class SnoringApi extends DetectionApi {

	private WaveHeader waveHear;
	private byte[] data;
	private double[] amplitudes;
	private double threshold_E;
	private double threshold_ZCR;
	private double[] E = null;
	private double[] ZCR = null;
	private double MAX_ZCR;
	private double MIN_ZCR;
	private double AVER_ZCR;
	private double MAX_E;
	private double MIN_E;
	private double AVER_E;

	private int sampleRange = 7;

	public SnoringApi(WaveHeader waveHeader) {
		super(waveHeader);
		this.waveHeader = waveHeader;
	}

	protected void init() {
		// settings for detecting a whistle
		minFrequency = 0.0f;
		maxFrequency = 7500.0f;// Double.MAX_VALUE;

		minIntensity = 100.0f;
		maxIntensity = 100000.0f;

		minStandardDeviation = 0.01f;
		maxStandardDeviation = 29.98f;
		// 4238740267052 2599952140684
		highPass = 100;
		lowPass = 10000;

		minNumZeroCross = 0;
		maxNumZeroCross = 1267;

		numRobust = 10;
	}

    private void getTime1() {
        long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = new Date(time);
        String t1 = format.format(d1);
        Log.i("snoring time  ", t1);

    }

	public int isSnoring(byte[] audioBytes) {
	   // Log.i("snoringAPI","INTO API");
		// return isSpecificSound(audioBytes);
		int cnt = 0;
		this.data = audioBytes;//1425096bytes
		Wave wave = new Wave(waveHeader, audioBytes); // audio bytes of this
														// totalbuf
		// this.amplitudes = wave.getSampleAmplitudes();
		this.amplitudes = wave.getNormalizedAmplitudes();

		setE_ZCRArray(100, 50);
		cal_threshold();
		float[] res = getSnoring();
        getTime1();
		System.out.println(Arrays.toString(res));//回到算法层面理解一下把
		int num = res.length / 2;

		if (AlarmStaticVariables.snoringCount > 0) {
			boolean ctn = true;
			for (int i = 0; i < res.length; i++) {
				if (ctn && res[i] >= sampleRange) {
					AlarmStaticVariables.snoringCount++;
					if (AlarmStaticVariables.snoringCount >= AlarmStaticVariables.sampleCount) {
						System.out.println("return here");
                        Log.i("snoringAPI","LEAVE API 1");
						return 4;//return 4 就是监测到打鼾，return 0就是没有事情
					}
				} else if (!ctn && res[i] >= sampleRange) {
					cnt++;
					if (cnt >= AlarmStaticVariables.sampleCount) {
						System.out.println("return 5 here");
                        Log.i("snoringAPI","LEAVE API 2");
						return 4;
					}
				} else if (res[i] < sampleRange) {
					ctn = false;
					AlarmStaticVariables.snoringCount = 0;
					cnt = 0;
				}
			}
		} else {
			for (int i = 0; i < res.length; i++) {
				if (res[i] >= sampleRange) {
					cnt++;
					if (cnt >= AlarmStaticVariables.sampleCount) {
						System.out.println("return 5 here");
                        Log.i("snoringAPI","LEAVE API 3");
						return 4;
					}
				} else
					cnt = 0;
			}
		}
        Log.i("snoringAPI","LEAVE API 4");
		System.out.println("cnt=" + cnt);
		return cnt;
	}

	private int getMax(int[] tmpMax) {
		int m = 0;
		for (int i = 0; i < tmpMax.length; i++)
			if (tmpMax[i] > m)
				m = tmpMax[i];
		return m;
	}

	public void setThreshold_E(double value) {
		this.threshold_E = value;
	}

	public double getThreshold_E() {
		return this.threshold_E;
	}

	public void setThreshold_ZCR(double value) {
		this.threshold_ZCR = value;
	}

	public double getThreshold_ZCR() {
		return this.threshold_ZCR;
	}

	public void setMIN_ZCR(double value) {
		this.MIN_ZCR = value;
	}

	public void setMAX_ZCR(double value) {
		this.MAX_ZCR = value;
	}

	public void setAVER_ZCR() {
		double sum = 0;
		for (int i = 0; i < this.ZCR.length; i++)
			sum += this.ZCR[i];
		this.AVER_ZCR = sum / this.ZCR.length;
       // Log.i("snoring","AVER_ZCR"+this.AVER_ZCR);
	}

	public double getMIN_ZCR() {
		return this.MIN_ZCR;
	}

	public double getMAX_ZCR() {
		return this.MAX_ZCR;
	}

	public double getAVER_ZCR() {
		return this.AVER_ZCR;
	}

	public void setMAX_E(double value) {
		this.MAX_E = value;
	}

	public void setMIN_E(double value) {
		this.MIN_E = value;
	}

	public void setAVER_E() {
		double sum = 0;
		for (int i = 0; i < this.E.length; i++)
			sum += this.E[i];
		this.AVER_E = sum / this.E.length;
		//Log.i("snoring","AVER_E"+this.AVER_E);
	}

	public double getMAX_E() {
		return this.MAX_E;
	}

	public double getMIN_E() {
		return this.MIN_E;
	}

	public double getAVER_E() {
		return this.AVER_E;
	}

	public void setE_ZCRArray(int length_time, int overlap_time) {// ms
		// int test = this.waveHear.getSampleRate();
		int sampleRate;
		if (this.waveHeader == null) {
			sampleRate = 0;// never happened
		} else {
			sampleRate = this.waveHeader.getSampleRate();
		}
		//System.out.println("sampleRate=" + sampleRate);
		int length = (sampleRate / 1000) * length_time;
		int overlap = (sampleRate / 1000) * overlap_time;
		int count_e = 0;
		int num_E = (this.data.length + (length - overlap) - overlap)
				/ (length - overlap) + 1;
		double tmp_energy[] = new double[num_E];
		double tmp_ZCR[] = new double[num_E];
		for (int i = 4; i < this.amplitudes.length - length; i += length
				- overlap) {
			double sum_slice = 0;
			double sum_ZCR = 0;
			for (int j = 0; j < length; j++) {
				sum_slice += Math.pow(this.amplitudes[i + j], 2);
				if ((this.amplitudes[i + j] > 0) != (this.amplitudes[i + j + 1] > 0))
					sum_ZCR++;
			}
			if (sum_slice == 0 && sum_ZCR == 0)
				continue;
			if (count_e == 0) {
				this.setMAX_E(sum_slice);
				this.setMIN_E(sum_slice);
				this.setMAX_ZCR(sum_ZCR);
				this.setMIN_E(sum_ZCR);
			} else {
				if (sum_slice > this.getMAX_E())
					this.setMAX_E(sum_slice);
				if (sum_slice < this.getMIN_E())
					this.setMIN_E(sum_slice);
				if (sum_ZCR > this.getMAX_ZCR())
					this.setMAX_ZCR(sum_ZCR);
				if (sum_ZCR < this.getMIN_ZCR())
					this.setMIN_ZCR(sum_ZCR);
			}
			tmp_energy[count_e] = sum_slice;
			tmp_ZCR[count_e] = sum_ZCR;
			count_e++;
		}
		this.E = new double[count_e];
		this.ZCR = new double[count_e];
		for (int i = 0; i < count_e; i++) {
			this.E[i] = tmp_energy[i];
			this.ZCR[i] = tmp_ZCR[i];
		}
		this.setAVER_E();
		this.setAVER_ZCR();
		// for(int i=0; i< this.E.length; i++)
		// System.out.println(this.E[i]);
	}

	public void cal_threshold() {

		float a = (float) 0.02;
		float b = (float) 8000;
		float c = (float) 1;
		double I_1 = a * (this.getMAX_E() - this.getMIN_E()) + this.getMIN_E();
		double I_2 = b * this.getMIN_E();
		/*
		 * if(I_1<I_2) this.threshold_E = I_1; else this.threshold_E = I_2;
		 */
		this.setThreshold_E(I_1);
		this.setThreshold_ZCR(c * this.getAVER_ZCR());

		// for(int i=0; i<this.ZCR.length; i++)
		// System.out.println(this.ZCR[i]);
		// System.out.println(this.getThreshold_E());
		// System.out.println(this.getThreshold_ZCR());
	}

	public float[] getSnoring() { //获取打鼾时间，但是这里只有一个时间段，没有具体时间点的信息。
		ArrayList<Float> snoring_time = new ArrayList<Float>();//
		boolean flag = false;
		int count = 0;
		for (int i = 0; i < this.E.length; i++) {
			if (this.E[i] > this.getThreshold_E()
					&& this.ZCR[i] < this.getThreshold_ZCR()) {
				if (flag == false) {
					// snoring_time.add((float) (i / 20.0));
					flag = true;
					// System.out.println(i);
				} else {
					count++;
				}
			} else {
				if (flag == true) {
					flag = false;
					snoring_time.add((float) (count));
					count = 0;
				}
			}
		}
		float[] res = new float[snoring_time.size()];
		for (int i = 0; i < snoring_time.size(); i++)
			res[i] = snoring_time.get(i);
		return res;
	}

}
