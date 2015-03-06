package multi.host;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import multi.data.FlowKey;

public class Host2TargetFlowSet {
	public ArrayList<ConcurrentHashMap<FlowKey, Integer>> targetFlowSetBuffer = 
			new ArrayList<ConcurrentHashMap<FlowKey, Integer>>();
	private int bufferIdx = 0;
	public boolean toSwitchBuffer;
	
	private static Host2TargetFlowSet singleInstanceFlowSet = new Host2TargetFlowSet();
	
	private Host2TargetFlowSet() {
		targetFlowSetBuffer.add(new ConcurrentHashMap<FlowKey, Integer>());
		targetFlowSetBuffer.add(new ConcurrentHashMap<FlowKey, Integer>());
		bufferIdx = 0;
		toSwitchBuffer = false;
	}
	
	public static Host2TargetFlowSet Instance() {
		return singleInstanceFlowSet;
	}
	
	public void copyAndSwitchBuffer(ConcurrentHashMap<FlowKey, Integer> sourceMap) {
		//add into the rest buffer
		ConcurrentHashMap<FlowKey, Integer> restBufferMap = targetFlowSetBuffer.get(1-bufferIdx);
		
		for (Map.Entry<FlowKey, Integer> entry : sourceMap.entrySet()) {
			FlowKey flowKey = entry.getKey();
			restBufferMap.put(flowKey, 1);
		}
		
		//switch buffer
		bufferIdx = 1 - bufferIdx;
		
		//clear switched rest buffer for future usage
		targetFlowSetBuffer.get(1-bufferIdx).clear();
		
		//reset toSwitchBuffer
		toSwitchBuffer = false;
		
		System.out.println("Host2TargetFlowSet switch buffer completed");
	}
	
	public boolean isTargetFlow(FlowKey flowKey) {
		if (targetFlowSetBuffer.get(bufferIdx).containsKey(flowKey)) {
			return true;
		}
		return false;
	}
}
