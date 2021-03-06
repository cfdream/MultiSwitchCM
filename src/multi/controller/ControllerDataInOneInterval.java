package multi.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import multi.data.FixSizeHashMap;
import multi.data.FlowKey;
import multi.host.Host2TargetFlowSet;
import multi.lib.library;
import multi.lib.library.AverageDeviation;
import multi.main.GlobalData;
import multi.main.GlobalSetting;
import multi.main.TargetFlowSetting;
import multi.sampleModel.PacketSampleSetting;

public class ControllerDataInOneInterval {
	public class SwitchPerformance {
		public double accracy;
		public double falseNegative;
		public int numTargetFlowCaptured;
		
		public SwitchPerformance() {
			accracy = 0;
			falseNegative = 0;
			numTargetFlowCaptured = 0;
		}
	}
	public class OneIntervalResult {
		int memorysize;
		int numTargetFlows;
		double accuracy;
		double falseNegative;
		long totalTargetFlowCapturedAtAllSwitches;
		long networkOverheadFromH2;
	}	
	
	public int numSwitchDataReceived;
	public boolean isH2DataReceived;
	
	public FixSizeHashMap S1SampleFlowVolumeMap;
	public FixSizeHashMap S2SampleFlowVolumeMap;
	public FixSizeHashMap S3SampleFlowVolumeMap;
	public FixSizeHashMap S4SampleFlowVolumeMap;
	public HashMap<FlowKey, Long> S1GroundTruthFlowVolumeMap;
	public HashMap<FlowKey, Long> S2GroundTruthFlowVolumeMap;
	public HashMap<FlowKey, Long> S3GroundTruthFlowVolumeMap;
	public HashMap<FlowKey, Long> S4GroundTruthFlowVolumeMap;
	
	public ConcurrentHashMap<FlowKey, Long> groundTruthFlowNormalVolumeMap;
	public ConcurrentHashMap<FlowKey, Long> groundTruthFlowLostVolumeMap;
	
	//overhead are measured in the number of flows sent 
	public long networkOverheadFromH2;	//singling overhead generated by H2
	public long switchToControllerOverhead;
	public long memoryOverhead;
	
	public int ithInterval;
	
	//
	public ArrayList<Double> listAccuracy = new ArrayList<Double>();
	public ArrayList<Double> listFalseNegative = new ArrayList<Double>();
	public ArrayList<Integer> listAvgTargetFlowCapturedAtEachSwitch = new ArrayList<Integer>();
	
	static ControllerDataInOneInterval singleInstance = new ControllerDataInOneInterval();
	
	//debug
	long srcipMissedInS2 = 0;
	
	private ControllerDataInOneInterval() {}
	
	public static ControllerDataInOneInterval Instance() {
		return singleInstance;
	}
	
	public void clearEveryInterval() {
		numSwitchDataReceived = 0;
		isH2DataReceived = false;
		networkOverheadFromH2 = 0;
		switchToControllerOverhead = 0;
		memoryOverhead = 0;
		
		//clear data
		S1SampleFlowVolumeMap.clear();
		S2SampleFlowVolumeMap.clear();
		S3SampleFlowVolumeMap.clear();
		S4SampleFlowVolumeMap.clear();
		S1GroundTruthFlowVolumeMap.clear();
		S2GroundTruthFlowVolumeMap.clear();
		S3GroundTruthFlowVolumeMap.clear();
		S4GroundTruthFlowVolumeMap.clear();
		
		groundTruthFlowNormalVolumeMap.clear();
		groundTruthFlowLostVolumeMap.clear();
		
		GlobalData.Instance().controllerIthInterval = ithInterval;
		
		//clear Host2TargetFlowSet
		Host2TargetFlowSet.Instance().clear();
		
		//debug 
		srcipMissedInS2 = 0;
	}
	
	public void clearEveryExperiment() {
		numSwitchDataReceived = 0;
		isH2DataReceived = false;
		networkOverheadFromH2 = 0;
		switchToControllerOverhead = 0;
		memoryOverhead = 0;
		ithInterval = GlobalSetting.FIRST_INTERVAL;
		
		listAccuracy.clear();
		listFalseNegative.clear();
		listAvgTargetFlowCapturedAtEachSwitch.clear();
	}
	
	public void analyze() {
		int numTargetFlows = getNumTargetFlows();
		
		SwitchPerformance s2Performance = getOneSwitchPerformance(S2SampleFlowVolumeMap, S2GroundTruthFlowVolumeMap, 2);
		SwitchPerformance s1Performance = getOneSwitchPerformance(S1SampleFlowVolumeMap, S1GroundTruthFlowVolumeMap, 1);
		SwitchPerformance s3Performance = getOneSwitchPerformance(S3SampleFlowVolumeMap, S3GroundTruthFlowVolumeMap, 3);
		SwitchPerformance s4Performance = getOneSwitchPerformance(S4SampleFlowVolumeMap, S4GroundTruthFlowVolumeMap, 4);
		
		double averageAccuracy = 
				(s1Performance.accracy+s2Performance.accracy+s3Performance.accracy+s4Performance.accracy)/4;
		double averageFalseNegative = (s1Performance.falseNegative+s2Performance.falseNegative
				+s3Performance.falseNegative+s4Performance.falseNegative) / 4;
		int totalTargetFlowCaptured = s1Performance.numTargetFlowCaptured + s2Performance.numTargetFlowCaptured
				+ s3Performance.numTargetFlowCaptured + s4Performance.numTargetFlowCaptured;
		
		OneIntervalResult oneIntervalResult = new OneIntervalResult();
		oneIntervalResult.numTargetFlows = numTargetFlows;
		oneIntervalResult.accuracy = averageAccuracy;
		oneIntervalResult.falseNegative = averageFalseNegative;
		oneIntervalResult.networkOverheadFromH2 = networkOverheadFromH2;
		oneIntervalResult.totalTargetFlowCapturedAtAllSwitches = totalTargetFlowCaptured;
		
		//analyze 1: #target flows vs. overhead
		BufferedWriter writer;
		try {
			//debug
			writer = new BufferedWriter(new FileWriter(
					GlobalSetting.DEBUG_RESULT_FILE_NAME, true));

			writer.write(
					  "replace:" + GlobalSetting.IS_USE_REPLACE_MECHANISM + " "
					+ "captureTarget:" + GlobalSetting.IS_CAPTURE_TARGET_FLOWS + " "
					+ "memeorysize:" + PacketSampleSetting.SH_BUCKET_SIZE + " "
					+ "targetVolume:" + TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD + " "
					+ "targetLossRate:" + TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD
					+ "\r\n"
					+ "numTargetFlows:" + numTargetFlows + " "
					+ "avgFalseNegative:" + averageFalseNegative + " "
					+ "avgAccuracy:" + averageAccuracy  + " "
					+ "totalTargetFlowCapturedAtAllSwitches:" + totalTargetFlowCaptured + " "
					+ "networkOverheadFromH2:" + networkOverheadFromH2 + " "
					+ "\r\n");
			writer.close();
			
			//draw figures
			writer = new BufferedWriter(new FileWriter(
					GlobalSetting.TARGET_FLOW_NUM_OVERHEAD_RESULT_FILE_NAME, true));

			writer.write(
					numTargetFlows + " "
					+ averageFalseNegative + " "
					+ averageAccuracy  + " "
					+ totalTargetFlowCaptured + " "
					+ networkOverheadFromH2 + " "
					+ PacketSampleSetting.SH_BUCKET_SIZE + " "
					+ GlobalSetting.NUM_PKTS_TO_SIGNAL_THE_NETWORK + " "
					+ PacketSampleSetting.BYTE_RATE_INCREASE_RATIO + " "
					+ GlobalSetting.IS_CAPTURE_TARGET_FLOWS + " "
					+ TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD + " "
					+ TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD
					+ "\r\n");
			
			writer.write(
					s1Performance.falseNegative + " "
					+ s2Performance.falseNegative + " "
					+ s3Performance.falseNegative + " "
					+ s4Performance.falseNegative + " "
					+ s1Performance.accracy + " "
					+ s2Performance.accracy + " "
					+ s3Performance.accracy + " "
					+ s4Performance.accracy + " "
					+ s1Performance.numTargetFlowCaptured + " "
					+ s2Performance.numTargetFlowCaptured + " "
					+ s3Performance.numTargetFlowCaptured + " "
					+ s4Performance.numTargetFlowCaptured
					+"\r\n");
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//analyze 2: #SH and replace mechanism vs SH
		listAccuracy.add(averageAccuracy);
		listFalseNegative.add(averageFalseNegative);
		listAvgTargetFlowCapturedAtEachSwitch.add(totalTargetFlowCaptured/4);
		if (listAccuracy.size() == GlobalSetting.SIMULATE_INVERVALS - 1) {
			 AverageDeviation averageDeviationAccuracy = library.computeAverageDeviation(listAccuracy);
			 AverageDeviation averageDeviationFN = library.computeAverageDeviation(listFalseNegative);
			 AverageDeviation averageTargetFlowAverageDeviation = library.computeAverageDeviationInt(
					 listAvgTargetFlowCapturedAtEachSwitch);
			 try {
				 writer = new BufferedWriter(new FileWriter(GlobalSetting.MEMORY_REPLACEMENT_RESULT_FILE_NAME, true));
				 writer.write(GlobalSetting.IS_USE_REPLACE_MECHANISM + " " 
						 + PacketSampleSetting.SH_BUCKET_SIZE + " "
						 + averageDeviationAccuracy.avgValue + " "
						 + averageDeviationAccuracy.deviation + " "
						 + averageDeviationFN.avgValue + " "
						 + averageDeviationFN.deviation + " "
						 + GlobalSetting.NUM_PKTS_TO_SIGNAL_THE_NETWORK + " "
						 + PacketSampleSetting.BYTE_RATE_INCREASE_RATIO + " "
						 + (int)averageTargetFlowAverageDeviation.avgValue + " "
						 + averageTargetFlowAverageDeviation.deviation+ " "
						 + GlobalSetting.IS_CAPTURE_TARGET_FLOWS + " "
						 + PacketSampleSetting.SAMPLE_AT_SWITCH_OR_HOST
						 + "\r\n");
				 writer.close();
			 } catch (IOException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
			 
			GlobalData.Instance().AllIntervalsCompleted = true;
		}
		
		System.out.println("ControllerDataInOneInterval analyze one interval completed");
	}	

	public SwitchPerformance getOneSwitchPerformance(
			FixSizeHashMap sampleFlowVolumeMap,
			HashMap<FlowKey, Long> groundTruthFlowVolumeMap,
			int ithSwitch) {
		SwitchPerformance switchPerformance = new SwitchPerformance();
		
		int numTargetFlowsItSelf = 0;
		int numTargetFlows = 0;
		int numSampledTargetFlows = 0;
		double totalAccuracy = 0;
		
		for (Map.Entry<FlowKey, Long> entry : groundTruthFlowVolumeMap.entrySet()) {
			FlowKey flowKey = entry.getKey();
			long groundTruthVolume = entry.getValue();
			double lossRate = getLossRate(flowKey);
			if (groundTruthVolume > TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD
				&& lossRate > TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD){
				numTargetFlowsItSelf++;
			}
			
			if (!isTargetFlow(flowKey)) {
				continue;
			}
			
			//one target flow: target flows that travel through the switch
			numTargetFlows++;
			
			//if this target flow is not in the signal from the host, it will not be reported to the controller.
			if (!GlobalData.Instance().gTargetFlowMapEndOfInterval.containsKey(flowKey)) {
				continue;
			}
			
			Long sampleVolume = sampleFlowVolumeMap.get(flowKey);
			if (sampleVolume == null) {
				/*/----start debug
				if(sampleFlowVolumeMap == S2SampleFlowVolumeMap) {
					if (srcipMissedInS2 == 0) {
						//pick one missed flows in switch2
						srcipMissedInS2 = flowKey.srcip;
						BufferedWriter writer;
						try {
							writer = new BufferedWriter(new FileWriter(
									GlobalSetting.TARGET_FLOW_NUM_OVERHEAD_RESULT_FILE_NAME, true));
							writer.write(
									"switch:" + ithSwitch + " "
									+ "miss srcip:" + flowKey.srcip + " "
									+ "volume through the switch:" + groundTruthVolume
									+"\r\n");
							writer.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
					}
				} else if (srcipMissedInS2 == flowKey.srcip) {
					BufferedWriter writer;
					try {
						writer = new BufferedWriter(new FileWriter(
								GlobalSetting.TARGET_FLOW_NUM_OVERHEAD_RESULT_FILE_NAME, true));
						writer.write(
								"switch:" + ithSwitch + " "
								+ "miss srcip:" + flowKey.srcip + " "
								+ "volume through the switch:" + groundTruthVolume
								+"\r\n");
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
				//---end debug*/
				
				//the target flow is not captured.
				continue;
			} 
			
			/*//----debug
			if (sampleFlowVolumeMap != S2SampleFlowVolumeMap
					&& srcipMissedInS2 == flowKey.srcip) {
				//switches rather than s2
				BufferedWriter writer;
				try {
					writer = new BufferedWriter(new FileWriter(
							GlobalSetting.TARGET_FLOW_NUM_OVERHEAD_RESULT_FILE_NAME, true));
					writer.write(
							"switch:" + ithSwitch + " "
							+ "catch srcip:" + flowKey.srcip + " "
							+ "volume through the switch:" + groundTruthVolume
							+"\r\n");
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
			//----end debug*/

			//one sampled target flow
			numSampledTargetFlows++;
			
			double accuracy = 1.0 * sampleVolume / groundTruthVolume;
			totalAccuracy += accuracy;
		}
		
		if (numSampledTargetFlows != 0) {
			switchPerformance.accracy = totalAccuracy / numSampledTargetFlows;
			switchPerformance.falseNegative = 1 - 1.0 * numSampledTargetFlows / numTargetFlows;
			switchPerformance.numTargetFlowCaptured = numSampledTargetFlows;
			
			/*
			System.out.println(numTargetFlowsItSelf);
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(
						GlobalSetting.TARGET_FLOW_NUM_OVERHEAD_RESULT_FILE_NAME, true));
				writer.write(
						numTargetFlows + " " + numTargetFlowsItSelf + " "
						+ numSampledTargetFlows + " "
						+ groundTruthFlowVolumeMap.size()
						+"\r\n");
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}
		
		return switchPerformance;
	}
	
	public boolean isTargetFlow(FlowKey flowKey) {
		double lossRate = 0;
		Long flowLostVolume = groundTruthFlowLostVolumeMap.get(flowKey);
		if (null == flowLostVolume) {
			flowLostVolume = 0L;
		}
		Long normalVolume = groundTruthFlowNormalVolumeMap.get(flowKey);
		if (null == normalVolume) {
			normalVolume = 0L;
		}
		Long totalVolume = flowLostVolume + normalVolume;
		lossRate = 1.0 * flowLostVolume / totalVolume;

		return lossRate >= TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD
				&& totalVolume >= TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD;
	}
	
	public double getLossRate(FlowKey flowKey) {
		double lossRate = 0;
		Long flowLostVolume = groundTruthFlowLostVolumeMap.get(flowKey);
		if (null == flowLostVolume) {
			flowLostVolume = 0L;
		}
		Long normalVolume = groundTruthFlowNormalVolumeMap.get(flowKey);
		if (null == normalVolume) {
			normalVolume = 0L;
		}
		Long totalVolume = flowLostVolume + normalVolume;
		lossRate = 1.0 * flowLostVolume / totalVolume;

		return lossRate;
	}
	
	public int getNumTargetFlows() {
		int numTargetFlows = 0;
		for (Map.Entry<FlowKey, Long> entry :groundTruthFlowNormalVolumeMap.entrySet()) {
			FlowKey flowKey = entry.getKey();
			Long normalVolume = entry.getValue();
			Long flowLostVolume = groundTruthFlowLostVolumeMap.get(flowKey);
			if (null == flowLostVolume) {
				flowLostVolume = 0L;
			}
			Long totalVolume = flowLostVolume + normalVolume;
			double lossRate = 1.0 * flowLostVolume / totalVolume;
			if (totalVolume < TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD
					|| lossRate < TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD) {
				continue;
			}
			numTargetFlows++;
		}
		return numTargetFlows;
	}
	
	public void writeResultToFile() {
		
	}
}
