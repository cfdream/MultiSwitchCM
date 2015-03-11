package multi.host;

import java.util.ArrayList;
import java.util.HashMap;

import multi.data.FlowKey;
import multi.data.Packet;
import multi.main.GlobalSetting;
import multi.sampleModel.PacketSampleModel;
import multi.sampleModel.PacketSampleModelTraditional;

public class Host1SampleAndHold {	
	public PacketSampleModel packetSampleModel = new PacketSampleModelTraditional();

	public ArrayList<HashMap<FlowKey, Long>> sampledFlowBuffer = new ArrayList<HashMap<FlowKey, Long>>();
	public int flowBufferIdx = 0;
	
	private int ithInterval;
	
	private static Host1SampleAndHold singleInstanceAndHold = new Host1SampleAndHold();
	
	public static Host1SampleAndHold Instance() {
		return singleInstanceAndHold;
	}
	
	private Host1SampleAndHold() {
		sampledFlowBuffer.add(new HashMap<FlowKey, Long>());
		sampledFlowBuffer.add(new HashMap<FlowKey, Long>());
		
		ithInterval = GlobalSetting.FIRST_INTERVAL;
	}
	
	public void switchBuffer() {
		//switch the buffer
		flowBufferIdx = 1 - flowBufferIdx;
		//clear rest buffer
		sampledFlowBuffer.get(1-flowBufferIdx).clear();
	}
	
	/*
	 * SAMPLE AND HOLD
	 */
	public boolean isFLowSampled(Packet pkg) {
		//check the interval, switch buffer if needed
		if (pkg.getIthInterval() > ithInterval) {
			ithInterval = pkg.getIthInterval();
			
			//switch the buffer.
			switchBuffer();
		}
		
		
		HashMap<FlowKey, Long> sampledFlowVolumeMap = sampledFlowBuffer.get(flowBufferIdx);
		
		FlowKey flow = new FlowKey(pkg);

		Long volume = sampledFlowVolumeMap.get(flow);
		if (null == volume) {
			// ----packet not sampled yet
			boolean isHeld = packetSampleModel.isSampled(pkg);
			if (isHeld) {
				sampledFlowVolumeMap.put(flow, pkg.length);
			}
		} else {
			// ----packet already sampled, hold it			
			//flow volume
			sampledFlowVolumeMap.put(flow, volume += pkg.length);
		}
		
		return sampledFlowVolumeMap.containsKey(flow);
	}
}
