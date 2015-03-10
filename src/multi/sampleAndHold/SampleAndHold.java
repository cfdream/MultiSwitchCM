package multi.sampleAndHold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import multi.data.FlowKey;
import multi.data.Packet;
import multi.sampleModel.PacketSampleModel;
import multi.sampleModel.PacketSampleModelTraditional;

public class SampleAndHold {

	Random rand = new Random(System.currentTimeMillis());
	
	public PacketSampleModel packetSampleModel = new PacketSampleModelTraditional();

	public ArrayList<HashMap<FlowKey, Long>> sampledFlowBuffer = new ArrayList<HashMap<FlowKey, Long>>();
	public int flowBufferIdx = 0;
	
	public SampleAndHold() {
		sampledFlowBuffer.add(new HashMap<FlowKey, Long>());
		sampledFlowBuffer.add(new HashMap<FlowKey, Long>());
	}
	
	public void switchBufferAndNotifyController() {
		//switch the buffer
		flowBufferIdx = 1 - flowBufferIdx;
		//clear rest buffer
		sampledFlowBuffer.get(1-flowBufferIdx).clear();
	}
	
	/*
	 * SAMPLE AND HOLD
	 */
	public void sampleAndHold(Packet pkg) {
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
	}
}
