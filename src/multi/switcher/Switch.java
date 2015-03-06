package multi.switcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import multi.controller.ControllerDataInOneInterval;
import multi.data.FixSizeHashMap;
import multi.data.FlowKey;
import multi.data.Packet;
import multi.dropModel.PacketDropConsecutivePackets;
import multi.ecmp.ECMP;
import multi.main.GlobalData;
import multi.main.GlobalSetting;
import multi.main.TargetFlowSetting;
import multi.sampleModel.PacketSampleModel;
import multi.sampleModel.PacketSampleModelTraditional;

public class Switch implements Runnable {
	private Thread thread;
	
	SwitchData switchIntOutData;
	int ithInterval = GlobalSetting.FIRST_INTERVAL;
	
	PacketDropConsecutivePackets packetDropConsecutivePackets = new PacketDropConsecutivePackets();
	Random rand = new Random(System.currentTimeMillis());
	
	public PacketSampleModel packetSampleModel = new PacketSampleModelTraditional();
	
	public ArrayList<FixSizeHashMap> sampledFlowBuffer = new ArrayList<FixSizeHashMap>();
	public ArrayList<HashMap<FlowKey, Long>> groundTruthFlowBuffer = new ArrayList<HashMap<FlowKey, Long>>();
	public int flowBufferIdx = 0;

	/*
	 * For running monitor
	 */
	Long interval_stime = Long.MIN_VALUE;
	int seconds = 0;
	int lines = 0;
	
	public Switch(SwitchData switchIntOutData) {
		this.switchIntOutData = switchIntOutData;
		
		sampledFlowBuffer.add(new FixSizeHashMap());
		sampledFlowBuffer.add(new FixSizeHashMap());
		groundTruthFlowBuffer.add(new HashMap<FlowKey, Long>());
		groundTruthFlowBuffer.add(new HashMap<FlowKey, Long>());
	}
	
	public void switchBufferAndNotifyController() {
		flowBufferIdx = 1 - flowBufferIdx;
		System.out.println(switchIntOutData.name + ": " + ithInterval + " interval, " + 
				"mapsize:" + groundTruthFlowBuffer.get(flowBufferIdx).size());
		
		//sendDataToControllerAndClearStatus();
		if (switchIntOutData.name == "s1") {
			ControllerDataInOneInterval.Instance().S1SampleFlowVolumeMap = 
					sampledFlowBuffer.get(1-flowBufferIdx);
			ControllerDataInOneInterval.Instance().S1GroundTruthFlowVolumeMap =
					groundTruthFlowBuffer.get(1-flowBufferIdx);
		} else if (switchIntOutData.name == "s2") {
			ControllerDataInOneInterval.Instance().S2SampleFlowVolumeMap = 
					sampledFlowBuffer.get(1-flowBufferIdx);
			ControllerDataInOneInterval.Instance().S2GroundTruthFlowVolumeMap =
					groundTruthFlowBuffer.get(1-flowBufferIdx);
		} else if (switchIntOutData.name == "s3") {
			ControllerDataInOneInterval.Instance().S3SampleFlowVolumeMap = 
					sampledFlowBuffer.get(1-flowBufferIdx);
			ControllerDataInOneInterval.Instance().S3GroundTruthFlowVolumeMap =
					groundTruthFlowBuffer.get(1-flowBufferIdx);
		} else {
			ControllerDataInOneInterval.Instance().S4SampleFlowVolumeMap = 
					sampledFlowBuffer.get(1-flowBufferIdx);
			ControllerDataInOneInterval.Instance().S4GroundTruthFlowVolumeMap =
					groundTruthFlowBuffer.get(1-flowBufferIdx);
		}
		ControllerDataInOneInterval.Instance().numSwitchDataReceived++;
	}

	/*
	 * SAMPLE AND HOLD
	 */
	public void sampleAndHold(Packet pkg) {
		FixSizeHashMap sampledFlowVolumeMap = sampledFlowBuffer.get(flowBufferIdx);
		
		FlowKey flow = new FlowKey(pkg);

		Long volume = sampledFlowVolumeMap.get(flow);
		if (null == volume) {
			// ----packet not sampled yet
			boolean isHeld = packetSampleModel.isSampled(pkg);
			if (isHeld) {
				if (GlobalSetting.DEBUG && GlobalSetting.DEBUG_SRCIP == flow.srcip) {
					System.out.println("srcip:"+ flow.srcip + ", is sampled now");
				}
				
				// sample success, start hold the packet
				if (1 == GlobalSetting.IS_USE_REPLACE_MECHANISM) {
					sampledFlowVolumeMap.putWithReplaceMechanism(flow, pkg.length);
				} else {
					sampledFlowVolumeMap.put(flow, pkg.length);
				}
			}
		} else {
			// ----packet already sampled, hold it			
			//flow volume
			if (1 == GlobalSetting.IS_USE_REPLACE_MECHANISM) {
				sampledFlowVolumeMap.putWithReplaceMechanism(flow, volume += pkg.length);
			} else {
				sampledFlowVolumeMap.put(flow, volume += pkg.length);
			}
		}
	}

	public void sendDataToControllerAndClearStatus() {
		//the rest buffer
		FixSizeHashMap sampledFlowVolumeMap = sampledFlowBuffer.get(1-flowBufferIdx);
		
		// send sampledFlowVolumeMap to Controller
		for (Iterator<FixSizeHashMap.Record> iterator = sampledFlowVolumeMap
				.getAllEntries().iterator(); iterator.hasNext();) {
			FixSizeHashMap.Record entry = iterator.next();
			FlowKey flowKey = entry.flowKey;
			Long sampledVolume = entry.value;

			Long totalVolume = GlobalData.Instance().getTotalVolumeForOneFlow(flowKey);
			double lossRate = GlobalData.Instance().getLossRateForOneFlow(flowKey);
			
			if (1 == TargetFlowSetting.OBJECT_VOLUME_OR_RATE) {
				// 1: loss rate > threshold
				if (totalVolume < TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD
						|| lossRate < TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD) {
					continue;
				}
			}

			// send the volume to the controller
			//Controller.addSampledNormalVolumeOfFlow(flowKey, sampledVolume);
		}
		
		sampledFlowVolumeMap.clear();
	}
	
	public int handleOneIncomingPacket(Packet pkg) {
		//record the ground truth at this switch
		HashMap<FlowKey, Long> groundTruthFlowVolumeMap = groundTruthFlowBuffer.get(flowBufferIdx);
		FlowKey flowKey = new FlowKey(pkg);
		if (groundTruthFlowVolumeMap.containsKey(flowKey)) {
			groundTruthFlowVolumeMap.put(flowKey, groundTruthFlowVolumeMap.get(flowKey) + pkg.length); 
		} else {
			groundTruthFlowVolumeMap.put(flowKey, pkg.length);
		}
		
		//if the packet is dropped, return
		if (switchIntOutData.switchDropPackets && packetDropConsecutivePackets.drop(pkg)) {
			return 0;
		}
		
		//if a new interval, switch buffer, and send data to controller
		if (pkg.getIthInterval() > ithInterval) {
			//if pkg.interval < current interval, it is the disorder of packets happening at S4
			ithInterval = pkg.getIthInterval();
			switchBufferAndNotifyController();
		}
		
		//sample and hold
		sampleAndHold(pkg);

		//output the packet to outQueueS
		try {
			if (switchIntOutData.outputQueueS != null) {
				//switch 1, 2, 3
				if (switchIntOutData.outputQueueS.size() == 1) {
					switchIntOutData.outputQueueS.get(0).put(pkg);
				} else {
					int idx = ECMP.getOutIndex(pkg, switchIntOutData.outputQueueS.size());
					switchIntOutData.outputQueueS.get(idx).put(pkg);
				}	
			} else {
				//switch 4
				switchIntOutData.H2InputSet.put(pkg, 1);
				//update currentMaxPktTimestamp, will be used in Host2MainThread
				if (pkg.microsec > GlobalData.currentMaxPktTimestamp) {
					GlobalData.currentMaxPktTimestamp = pkg.microsec;
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* start debug */
		lines++;
		if (interval_stime == Long.MIN_VALUE) {
			interval_stime = pkg.microsec;
		}
		if (lines % 1000000 == 0) {
			System.out.println(switchIntOutData.name + " received " + lines + " packets");
		}
		/* end debug */
		return 0;
	}

	public void run() {
		// TODO Auto-generated method stub
		// go through all packets, sample&hold
		while (true) {
			//get one Packet
			Packet pkg = null;
			while ((pkg = switchIntOutData.inputQueue.poll()) == null) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//handle the packet
			handleOneIncomingPacket(pkg);
		}
	}

	public void start()
	{
		System.out.println("Starting " +  switchIntOutData.name );
		if (thread == null)
		{
			thread = new Thread (this, switchIntOutData.name);
			thread.start();
		}
	}
}
