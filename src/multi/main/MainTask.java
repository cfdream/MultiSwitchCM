package multi.main;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import multi.controller.Controller;
import multi.controller.ControllerDataInOneInterval;
import multi.data.Packet;
import multi.host.Host1;
import multi.host.Host2GenereateTargetSet;
import multi.host.Host2MainThread;
import multi.host.Host2ReadGroundTruth;
import multi.host.Host2TargetFlowSet;
import multi.sampleModel.PacketSampleSetting;
import multi.switcher.Switch;
import multi.switcher.SwitchData;

public class MainTask {
	public static void main(String[] args) {
//		double startLossRate = 0.02;
//		double endlossRate = 0.03;
//		tryDiffTargetFlowNumVsMemory(startLossRate, endlossRate);
		
		tryDiffMemoryDiffByteIncreaseRatio();
	}
	
	public static void tryDiffTargetFlowNumVsMemory(double startLossRate, double endlossRate) {
		GlobalSetting.IS_USE_REPLACE_MECHANISM = 1;
		GlobalSetting.IS_CAPTURE_TARGET_FLOWS = 1;
		GlobalSetting.SIMULATE_INVERVALS = 5;
		PacketSampleSetting.BYTE_RATE_INCREASE_RATIO = 2;
		
		//only change volume
		GlobalSetting.TARGET_FLOW_NUM_OVERHEAD_RESULT_FILE_NAME = 
				"data/diffTargetFlowNumChangeVolume_VS_BucketsUsageToAchieve1FN.txt";
		for (double lossRate=startLossRate; lossRate <= endlossRate; lossRate+=0.001) {
			TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD = lossRate;
			for (int volume = 10000; volume <= 200000; volume+=10000) {
				TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD = volume;
				PacketSampleSetting.DEAFULT_BYTE_SAMPLE_RATE = 
						PacketSampleSetting.OVER_SAMPLING_RATIO	
						/ TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD;
				for (double memRatio = 0.01; memRatio <= 10; memRatio+=0.01) {
					PacketSampleSetting.SHRINK_RATIO = memRatio;
					PacketSampleSetting.SH_BUCKET_SIZE = (int)(
							PacketSampleSetting.SHRINK_RATIO * 
							PacketSampleSetting.DEAFULT_BYTE_SAMPLE_RATE * 
							PacketSampleSetting.TOTAL_VOLUME_IN_ONE_TIME_INTERVAL);
					for (int ratio = 1; ratio <= 16; ratio*=2){
						PacketSampleSetting.BYTE_RATE_INCREASE_RATIO = ratio;
						runOneExperiment();
						while (!GlobalData.Instance().AllThreadExit()) {
							//wait till the current experiment is over.
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}//for
			}//for
		}//for
	}
	
	public static void tryDiffTargetFlowNumVSOverhead() {
		GlobalSetting.IS_USE_REPLACE_MECHANISM = 1;
		GlobalSetting.SIMULATE_INVERVALS = 10;
		PacketSampleSetting.BYTE_RATE_INCREASE_RATIO = 2;
		
		//only change loss rate
		GlobalSetting.TARGET_FLOW_NUM_OVERHEAD_RESULT_FILE_NAME = 
				"data/diffTargetFlowNumChangeLossRate_VS_Overhead.txt";
		for (double lossRate=0.002; lossRate <= 0.04; lossRate+=0.002) {
			TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD = lossRate;
			runOneExperiment();
			while (!GlobalData.Instance().AllThreadExit()) {
				//wait till the current experiment is over.
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		//only change volume
		TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD = 0.005;
		GlobalSetting.TARGET_FLOW_NUM_OVERHEAD_RESULT_FILE_NAME = 
				"data/diffTargetFlowNumChangeVolume_VS_Overhead.txt";
		for (int volume = 10000; volume <= 200000; volume+=10000) {
			TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD = volume;
			runOneExperiment();
			while (!GlobalData.Instance().AllThreadExit()) {
				//wait till the current experiment is over.
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void tryDiffNumPktsToSendSignal() {
		GlobalSetting.MEMORY_REPLACEMENT_RESULT_FILE_NAME = "data/diffNumPktsToSendSignal_vs_AccuracyFN"
				+ "_" + TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD
				+ "_" + TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD
				+ ".txt" ;
		GlobalSetting.IS_USE_REPLACE_MECHANISM = 1;
		GlobalSetting.IS_CAPTURE_TARGET_FLOWS = 1;
		GlobalSetting.SIMULATE_INVERVALS = 21;
		PacketSampleSetting.BYTE_RATE_INCREASE_RATIO = 2;
		
		double[] memRatioList = {0.01, 0.05, 0.1, 0.25, 0.5, 0.75, 1 }; 
		for (int numPkts = 1000; numPkts <= 1000000; numPkts*=10) {
			GlobalSetting.NUM_PKTS_TO_SIGNAL_THE_NETWORK = numPkts;
			for (int ithMemRatio = 0; ithMemRatio < memRatioList.length; ithMemRatio++) {
				PacketSampleSetting.SHRINK_RATIO = memRatioList[ithMemRatio];
				PacketSampleSetting.SH_BUCKET_SIZE = (int)(
						PacketSampleSetting.SHRINK_RATIO * 
						PacketSampleSetting.DEAFULT_BYTE_SAMPLE_RATE * 
						PacketSampleSetting.TOTAL_VOLUME_IN_ONE_TIME_INTERVAL);
				runOneExperiment();
				while (!GlobalData.Instance().AllThreadExit()) {
					//wait till the current experiment is over.
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void tryDiffMemoryDiffByteIncreaseRatio() {
		GlobalSetting.MEMORY_REPLACEMENT_RESULT_FILE_NAME = "data/diffMemory_vs_samplehold_replacement"
				+ "_" + TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD
				+ "_" + TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD
				+ ".txt" ;
		//TODO: this can be calculated from 
		GlobalSetting.NUM_PKTS_TO_SIGNAL_THE_NETWORK = 100000;		//now setting
		PacketSampleSetting.SAMPLE_AT_SWITCH_OR_HOST = 2;
		
		GlobalSetting.SIMULATE_INVERVALS = 3;
		
		//double[] memRatioList = {0.01, 0.05, 0.1, 0.25, 0.5, 0.75, 1 };
		//double[] memRatioList = {0.01, 0.05, 0.1, 0.15, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 1 };
		double[] memRatioList = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 1 };
		for (int isUse = 0; isUse <= 1; isUse++) {
			GlobalSetting.IS_USE_REPLACE_MECHANISM = isUse;
			for (int isCapture = 0; isCapture <= 1; isCapture++) {
				GlobalSetting.IS_CAPTURE_TARGET_FLOWS = isCapture;
				//SH, SH+replace, SH+select, SH+replace+select
				for (int ithMemRatio = 0; ithMemRatio < memRatioList.length; ithMemRatio++) {
					PacketSampleSetting.SHRINK_RATIO = memRatioList[ithMemRatio];
					PacketSampleSetting.SH_BUCKET_SIZE = (int)(
							PacketSampleSetting.SHRINK_RATIO * 
							PacketSampleSetting.DEAFULT_BYTE_SAMPLE_RATE * 
							PacketSampleSetting.TOTAL_VOLUME_IN_ONE_TIME_INTERVAL);
					for (int ratio = 2; ratio <= 2; ratio*=2){
						PacketSampleSetting.BYTE_RATE_INCREASE_RATIO = ratio;
						runOneExperiment();
						while (!GlobalData.Instance().AllThreadExit()) {
							//wait till the current experiment is over.
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	public static void runOneExperiment() {
		System.out.println("-------------------------NEW EXPERIMENT------------------------");
		System.out.println("targetFlowLossRate:" + TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD);
		
		//clear data.
		ControllerDataInOneInterval.Instance().clearEveryExperiment();
		Host2TargetFlowSet.Instance().clear();
		GlobalData.Instance().clear();		
		
		//init 4 switches
		SwitchData s1Data = new SwitchData();
		SwitchData s2Data = new SwitchData();
		SwitchData s3Data = new SwitchData();
		SwitchData s4Data = new SwitchData();
		
		ArrayList<LinkedBlockingQueue<Packet>> s1OutQueueList = new ArrayList<LinkedBlockingQueue<Packet>>();
		s1OutQueueList.add(GlobalData.Instance().S2InputQueue);
		s1OutQueueList.add(GlobalData.Instance().S3InputQueue);
		s1Data.name = "s1";
		s1Data.inputQueue = GlobalData.Instance().S1InputQueue;
		s1Data.outputQueueS = s1OutQueueList;
		
		ArrayList<LinkedBlockingQueue<Packet>> s2OutQueueList = new ArrayList<LinkedBlockingQueue<Packet>>();
		s2OutQueueList.add(GlobalData.Instance().S4InputQueue);
		s2Data.name = "s2";
		s2Data.inputQueue = GlobalData.Instance().S2InputQueue;
		s2Data.outputQueueS = s2OutQueueList;
		s2Data.switchDropPackets = true;			//only S2 drops packets
		
		ArrayList<LinkedBlockingQueue<Packet>> s3OutQueueList = new ArrayList<LinkedBlockingQueue<Packet>>();
		s3OutQueueList.add(GlobalData.Instance().S4InputQueue);
		s3Data.name = "s3";
		s3Data.inputQueue = GlobalData.Instance().S3InputQueue;
		s3Data.outputQueueS = s3OutQueueList;
		
		s4Data.name = "s4";
		s4Data.inputQueue = GlobalData.Instance().S4InputQueue;
		s4Data.H2InputSet = GlobalData.Instance().H2InputSet;
		
		/*4 switches*/
		Switch switch1 = new Switch(s1Data);
		Switch switch2 = new Switch(s2Data);
		Switch switch3 = new Switch(s3Data);
		Switch switch4 = new Switch(s4Data);
		switch1.start();
		switch2.start();
		switch3.start();
		switch4.start();
		
		/*host 1*/
		Host1 host1 = new Host1();
		host1.start();
		
		/*host 2 reading ground truth thread*/
		Host2ReadGroundTruth host2 = new Host2ReadGroundTruth();
		host2.start();
		
		/*host 2 main thread*/
		Host2MainThread host2MainThread = new Host2MainThread();
		host2MainThread.start();
		
		/*Host2GenereateTargetSet*/
		Host2GenereateTargetSet host2GenereateTargetSet = new Host2GenereateTargetSet();
		host2GenereateTargetSet.start();
		
		/*controller*/
		Controller controller = new Controller();
		controller.start();
	}
}
