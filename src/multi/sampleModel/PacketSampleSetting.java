package multi.sampleModel;

import multi.main.GlobalSetting;
import multi.main.TargetFlowSetting;

public class PacketSampleSetting {
	public static long TOTAL_VOLUME_IN_ONE_TIME_INTERVAL_IN_30S = 6181204282L;
	public static long TOTAL_VOLUME_IN_ONE_TIME_INTERVAL = 
			TOTAL_VOLUME_IN_ONE_TIME_INTERVAL_IN_30S 
			/ 30 
			* GlobalSetting.INTERVAL_SECONDS;
	public static int OVER_SAMPLING_RATIO = 4;
	
	public static int SAMPLE_AT_SWITCH_OR_HOST = 1;	//1: switch; 2:host
	
	//initial FLOW sampling rate
	
	//initial byte sampling rate for S&H
	public static double DEAFULT_BYTE_SAMPLE_RATE = 
			OVER_SAMPLING_RATIO	/ TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD;
	public static int BYTE_RATE_INCREASE_RATIO = 10;
	
	//buckets in hashmap
	public static double SHRINK_RATIO = 1;	//0.1, 0.25, 0.5, 0.75, 1 
	public static int SH_BUCKET_SIZE = (int)( SHRINK_RATIO * 
			DEAFULT_BYTE_SAMPLE_RATE * TOTAL_VOLUME_IN_ONE_TIME_INTERVAL);
	
	// 10007 105943, 1000003,
	// 1200007(1.2M) 13567(13k)
	// 240007(240k)
}
