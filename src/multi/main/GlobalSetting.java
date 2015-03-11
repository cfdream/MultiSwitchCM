package multi.main;
public class GlobalSetting {
	public static boolean DEBUG = false; // debug model
	public static boolean DEBUG2 = true; // debug model
	public static Long DEBUG_SRCIP = 2967874518L;
	
	public static boolean USE_GROUND_TRUTH_FLOW_VOLUME = true;
	public static String DEBUG_RESULT_FILE_NAME = "data/intervalResultsExponential.txt";
	public static String TARGET_FLOW_NUM_OVERHEAD_RESULT_FILE_NAME = "data/targetFlowNum_vs_Overhead.txt";
	public static String MEMORY_REPLACEMENT_RESULT_FILE_NAME = "data/diffMemory_vs_samplehold_replacement.txt";

	/* experiment setup */
	public static int SECOND_2_USECOND = 1000000;
	public static int RTT = 1; // millisecond
	public static int MAX_MINISECONDS_TO_WAIT_FOR_ONE_PKT = 50 * 1000;
	public static int NUM_PKTS_TO_SIGNAL_THE_NETWORK = 100000;	//1000, 10000, 100000, 1000000
	
	public static double VOLUME_DROP_RATE = 0.02;
	
	public static int INTERVAL_SECONDS = 5; // the number of seconds in one interval
	public static int SIMULATE_INVERVALS = 3; // how many intervals to test
	
	public static int IS_USE_REPLACE_MECHANISM = 1; // 1: yes, 0: no
	public static int IS_CAPTURE_TARGET_FLOWS = 1;
	
	public static int FIRST_INTERVAL =1;
	public static  long FIRST_INTERVAL_START_USECOND = 21600000000L;
	public static long USECONDS_IN_ONE_INTERVAL = GlobalSetting.INTERVAL_SECONDS * GlobalSetting.SECOND_2_USECOND;
}
