package multi.main;

public class TargetFlowSetting {
	// 1:loss rate > threshold && volume > threshold
	public static int OBJECT_VOLUME_OR_RATE = 1; 
	
	//target flow loss rate
	public static double TARGET_FLOW_LOST_RATE_THRESHOLD = 0.02;
	public static double TARGET_FLOW_TOTAL_VOLUME_THRESHOLD = 20000; // only care flows larger than 100k in 30s
}
