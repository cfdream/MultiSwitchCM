package multi.host;

import java.util.ArrayList;

import multi.data.FixSizeHashMap;
import multi.data.FlowKey;
import multi.data.Packet;
import multi.main.GlobalSetting;
import multi.sampleModel.PacketSampleModel;
import multi.sampleModel.PacketSampleModelTraditional;

public class Host1SampleAndHold {	
	public PacketSampleModel packetSampleModel = new PacketSampleModelTraditional();

	public ArrayList<FixSizeHashMap> sampledFlowBuffer = new ArrayList<FixSizeHashMap>();
	public int flowBufferIdx = 0;
	
	private int ithInterval;
	
	public Host1SampleAndHold() {
		sampledFlowBuffer.add(new FixSizeHashMap());
		sampledFlowBuffer.add(new FixSizeHashMap());
		
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
		
		
		FixSizeHashMap sampledFlowVolumeMap = sampledFlowBuffer.get(flowBufferIdx);
		
		FlowKey flow = new FlowKey(pkg);

		Long volume = sampledFlowVolumeMap.get(flow);
		if (null == volume) {
			// ----packet not sampled yet
			boolean isHeld = packetSampleModel.isSampled(pkg);
			if (isHeld) {
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
		
		return sampledFlowVolumeMap.get(flow) != null;
	}
}
