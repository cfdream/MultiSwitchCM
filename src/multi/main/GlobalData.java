package multi.main;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import multi.data.FlowKey;
import multi.data.Packet;

public class GlobalData {
	//global data generated by H2, and shared by all switches
	public ArrayList<ConcurrentHashMap<FlowKey, Long>> gNormalFlowMapBuffer = new ArrayList<ConcurrentHashMap<FlowKey, Long>>();
	public ArrayList<ConcurrentHashMap<FlowKey, Long>> gLostFlowMapBuffer = new ArrayList<ConcurrentHashMap<FlowKey, Long>>();
	public int gFlowMapBufferIdx;
	
	//used by Host2MainThread and Host2GenerateTargetSet
	public ConcurrentHashMap<FlowKey, Integer> gTargetFlowMap = new ConcurrentHashMap<FlowKey, Integer>();
	public ConcurrentHashMap<FlowKey, Integer> gTargetFlowMapEndOfInterval = new ConcurrentHashMap<FlowKey, Integer>();
	
	public LinkedBlockingQueue<Packet> S1InputQueue= new LinkedBlockingQueue<Packet>(10000); // 10M
	public LinkedBlockingQueue<Packet> S2InputQueue = new LinkedBlockingQueue<Packet>(10000); // 10M
	public LinkedBlockingQueue<Packet> S3InputQueue = new LinkedBlockingQueue<Packet>(10000); // 10M
	public LinkedBlockingQueue<Packet> S4InputQueue = new LinkedBlockingQueue<Packet>(10000); // 10M
	public ConcurrentMap<Packet, Integer> H2InputSet = new ConcurrentHashMap<Packet, Integer>(); // 10M
	public LinkedBlockingQueue<Packet> H2TruthQueue = new LinkedBlockingQueue<Packet>(10000); // 10M
	public Lock h2InputSetMutex = new ReentrantLock(true);
	public long currentMaxPktTimestamp;
	
	//signal for all threads.
	public boolean AllIntervalsCompleted;
	public boolean h1exit;
	public boolean s1exit;
	public boolean s2exit;
	public boolean s3exit;
	public boolean s4exit;
	public boolean h2exit;
	public boolean h2ReaderExit;
	//signal from h2 to s1
	public int controllerIthInterval;
	
	private GlobalData() {
		//2 buffers
		gNormalFlowMapBuffer.add(new ConcurrentHashMap<FlowKey, Long>());
		gNormalFlowMapBuffer.add(new ConcurrentHashMap<FlowKey, Long>());
		//2 buffers
		gLostFlowMapBuffer.add(new ConcurrentHashMap<FlowKey, Long>());
		gLostFlowMapBuffer.add(new ConcurrentHashMap<FlowKey, Long>());
		//
		gFlowMapBufferIdx = 0;
		
		AllIntervalsCompleted = false;
		h1exit = false;
		s1exit = false;
		s2exit = false;
		s3exit = false;
		s4exit = false;
		h2exit = false;
		h2ReaderExit = false;
		
		controllerIthInterval = GlobalSetting.FIRST_INTERVAL;
	}
	
	public static GlobalData Instance() {
		return singleInstance;
	}
	
	private static GlobalData singleInstance = new GlobalData();
	
	//At the start of each experiment, this function should be called
	public void clear() {		
		gNormalFlowMapBuffer.get(0).clear();
		gNormalFlowMapBuffer.get(1).clear();
		gLostFlowMapBuffer.get(0).clear();
		gLostFlowMapBuffer.get(1).clear();
		gFlowMapBufferIdx = 0;

		gTargetFlowMap.clear();
		gTargetFlowMapEndOfInterval.clear();
		
		S1InputQueue.clear();
		S2InputQueue.clear();
		S3InputQueue.clear();
		S4InputQueue.clear();
		H2InputSet.clear();
		H2TruthQueue.clear();
		currentMaxPktTimestamp = 0;
		
		AllIntervalsCompleted = false;
		h1exit = false;
		s1exit = false;
		s2exit = false;
		s3exit = false;
		s4exit = false;
		h2exit = false;
		h2ReaderExit = false;
		
		controllerIthInterval = GlobalSetting.FIRST_INTERVAL;
	}
	
	public boolean AllThreadExit() {
		return AllIntervalsCompleted && h1exit && s1exit && s2exit && s3exit && s4exit 
				&& h2exit && h2ReaderExit;
	}
	
	public void insertIntoNormalFlowVolumeMap(FlowKey flow, Packet pkg) {
		ConcurrentHashMap<FlowKey, Long> gNormalFlowVolumeMap = gNormalFlowMapBuffer.get(gFlowMapBufferIdx);
		/* in order to get real loss rate */
		Long normalVolume = gNormalFlowVolumeMap.get(flow);
		if (normalVolume == null) {
			gNormalFlowVolumeMap.put(flow, pkg.length);
		} else {
			gNormalFlowVolumeMap.put(flow, normalVolume + pkg.length);
		}
		/* in order to get real loss rate */
	}
	
	public void insertIntoLostFlowVolumeMap(FlowKey flow, Packet pkg) {
		ConcurrentHashMap<FlowKey, Long> gLostFlowVolumeMap = gLostFlowMapBuffer.get(gFlowMapBufferIdx);
		/* in order to get real loss rate */
		Long lostVolume = gLostFlowVolumeMap.get(flow);
		if (lostVolume == null) {
			gLostFlowVolumeMap.put(flow, pkg.length);
		} else {
			gLostFlowVolumeMap.put(flow, lostVolume + pkg.length);
		}
		/* in order to get real loss rate */
	}
	
	public double getLossRateForOneFlow(FlowKey flowKey) {
		ConcurrentHashMap<FlowKey, Long> gNormalFlowVolumeMap = gNormalFlowMapBuffer.get(gFlowMapBufferIdx);
		ConcurrentHashMap<FlowKey, Long> gLostFlowVolumeMap = gLostFlowMapBuffer.get(gFlowMapBufferIdx);
		double lossRate = 0;
		Long flowLostVolume = gLostFlowVolumeMap.get(flowKey);
		if (null == flowLostVolume) {
			flowLostVolume = 0L;
		}
		Long normalVolume = gNormalFlowVolumeMap.get(flowKey);
		if (null == normalVolume) {
			normalVolume = 0L;
		}
		Long totalVolume = flowLostVolume + normalVolume;
		lossRate = 1.0 * flowLostVolume / totalVolume;

		return lossRate;		
	}
	
	public long getTotalVolumeForOneFlow(FlowKey flowKey) {
		ConcurrentHashMap<FlowKey, Long> gNormalFlowVolumeMap = gNormalFlowMapBuffer.get(gFlowMapBufferIdx);
		ConcurrentHashMap<FlowKey, Long> gLostFlowVolumeMap = gLostFlowMapBuffer.get(gFlowMapBufferIdx);
		
		Long flowLostVolume = gLostFlowVolumeMap.get(flowKey);
		if (null == flowLostVolume) {
			flowLostVolume = 0L;
		}
		Long normalVolume = gNormalFlowVolumeMap.get(flowKey);
		if (null == normalVolume) {
			normalVolume = 0L;
		}
		Long totalVolume = flowLostVolume + normalVolume;
		
		return totalVolume;
	}
	
	public void switchFlowMapBuffers() {
		//switch global data buffer
		gFlowMapBufferIdx = 1 - gFlowMapBufferIdx;
		
		//new interval comes, 
		//keep the existing target flows buffer for calculation in the controller.
		gTargetFlowMapEndOfInterval.clear();
		for (Map.Entry<FlowKey, Integer> entry : gTargetFlowMap.entrySet()) {
			FlowKey flowKey = entry.getKey();
			gTargetFlowMapEndOfInterval.put(flowKey, 1);
		}
	}
}
