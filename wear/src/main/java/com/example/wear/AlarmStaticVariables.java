package com.example.wear;

import java.util.ArrayList;

public class AlarmStaticVariables {
	public static int level;
	public static float absValue;
	public static boolean inProcess;
	public static int level0 = 0;// vibration
	public static int level1 = 5000;
	public static int level2 = 10000;
	public static int level3 = 15000;
	public static int level4 = 20000;
	public static float lvolumn = (float) 0.5;
	public static float rvolumn = (float) 0.5;
	public static long[] partten = { 1000, 2000, 1000, 2000 };
	public static long[] pattern1 = { 1000, 2000, 1000, 2000 };
	public static long[] pattern2 = { 1000, 2000, 1000, 2000 };
	public static long[] pattern3 = { 1000, 2000, 1000, 2000 };
	public static int recordingTime = 5000;
	//public static int sampleSize = 712548;//8s
	public static int sampleSize = 220500;//5s
	// public static int sampleSize = 7125488;

	public static ArrayList<short[]> inBuf = new ArrayList<short[]>();
	public static int rateX = 8;
	public static int rateY = 10;
	// public static boolean draw_isRecording = false;

	public static int snoringCount = 0;
	public static int sampleCount = 2;//here
}
