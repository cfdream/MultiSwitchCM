package multi.host;

import multi.controller.ControllerDataInOneInterval;
import multi.data.FlowKey;
import multi.data.Packet;
import multi.main.GlobalData;
import multi.main.GlobalSetting;
import multi.main.TargetFlowSetting;

public class Host2MainThread implements Runnable {
	private Thread thread;
	
	int ithInterval = GlobalSetting.FIRST_INTERVAL;
	int numReceivedPkts = 0;
	int numLostPkts = 0;
	
	int numSingalFlowsReportedToNetwork = 0;
	
	@Override
	public void run() {
		// go through all packets, sample&hold
		while (true) {
			//get one Packet
			Packet pkg = null;
			while ((pkg = GlobalData.Instance().H2TruthQueue.poll()) == null) {
				try {
					Thread.sleep(1);
					if (canExit()) {
						break;
					}
					//System.out.println("h2 wait for H2TruthQUeue");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (canExit()) {
				break;
			}
			
			//check interval
			if (pkg.getIthInterval() > ithInterval) {
				ithInterval = pkg.getIthInterval();
				System.out.println("Host2MainThread, " + ithInterval + " interval");
				System.out.println("h2"+ " #pkts dropped: " + numLostPkts
						+ " lossRate:" + 1.0*numLostPkts/numReceivedPkts + ", "	
						+ ", H2InputSet.size:" + GlobalData.Instance().H2InputSet.size());
				
				GlobalData.Instance().switchFlowMapBuffers();
				
				//set controller data
				ControllerDataInOneInterval.Instance().groundTruthFlowLostVolumeMap = 
						GlobalData.Instance().gLostFlowMapBuffer.get(1-GlobalData.Instance().gFlowMapBufferIdx);
				ControllerDataInOneInterval.Instance().groundTruthFlowNormalVolumeMap = 
						GlobalData.Instance().gNormalFlowMapBuffer.get(1-GlobalData.Instance().gFlowMapBufferIdx);
				
				ControllerDataInOneInterval.Instance().networkOverheadFromH2 = numSingalFlowsReportedToNetwork;
				ControllerDataInOneInterval.Instance().isH2DataReceived = true;
				ControllerDataInOneInterval.Instance().ithInterval = ithInterval;
				
				GlobalData.Instance().gTargetFlowMap.clear();
				numSingalFlowsReportedToNetwork = 0;
				
				numLostPkts = 0;
				numReceivedPkts = 0;
			}
			
			//pkg is from ground truth, check whether it is already received
			int waitTimes = 0;
			while (true) {
				FlowKey flowKey = new FlowKey(pkg);
				GlobalData.Instance().h2InputSetMutex.lock();
				Integer cnt = GlobalData.Instance().H2InputSet.get(pkg);
				if (cnt == null) {
					GlobalData.Instance().h2InputSetMutex.unlock();
				}
				if (cnt != null) {
					//cnt--, has concurrency problem here.
					if (cnt <= 0) {
						System.out.println(flowKey.srcip);
					}
					if(cnt-1 == 0) {
						GlobalData.Instance().H2InputSet.remove(pkg);
					} else {
						GlobalData.Instance().H2InputSet.put(pkg, cnt-1);
					}
					GlobalData.Instance().h2InputSetMutex.unlock();
					
					//the pkg is already received
					//update normal volume for the flow
					GlobalData.Instance().insertIntoNormalFlowVolumeMap(flowKey, pkg);
					checkOneFlowIsTargetFlow(flowKey);
					
					numReceivedPkts++;
					if (numReceivedPkts % GlobalSetting.NUM_PKTS_TO_SIGNAL_THE_NETWORK == 0) {
						numSingalFlowsReportedToNetwork += GlobalData.Instance().gTargetFlowMap.size();
						//set switch buffer
						Host2TargetFlowSet.Instance().toSwitchBuffer = true;
					}
					
					/*log*/
					if (numReceivedPkts % 1000000 == 0) {
						System.out.println("h2"+ " received " + numReceivedPkts + " packets, " 
											+ "lossRate:" + 1.0*numLostPkts/numReceivedPkts + ", "	
											+ "H2InputSet.size:" + GlobalData.Instance().H2InputSet.size());
					}
					break;
				}
				
				if (GlobalData.Instance().currentMaxPktTimestamp - pkg.microsec > GlobalSetting.MAX_MINISECONDS_TO_WAIT_FOR_ONE_PKT) {
					//time out, pkg is lost
					//update lost volume for the flow
					GlobalData.Instance().insertIntoLostFlowVolumeMap(flowKey, pkg);
					checkOneFlowIsTargetFlow(flowKey);
					
					/*log*/
					numLostPkts++;
					if (numLostPkts % 1000000 == 0) {
						System.out.println("h2"+ " lost " + numLostPkts + " packets"+ 
								", H2InputSet.size:" + GlobalData.Instance().H2InputSet.size());
					}
					if (numLostPkts == 1) {
						System.out.println("1st dropped pkt at H2:"
								+ pkg.microsec + ","
								+ pkg.srcip+","+pkg.destip
								+","+pkg.srcport+","+pkg.destport);
					}
					break;
				}
				
				try {
					Thread.sleep(1);	//1 millisecond
					if (canExit()) {
						break;
					}
					//System.out.println("h2 wait for H2Set");
					waitTimes++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (waitTimes == 100) {
					System.out.println("h2 wait for H2Set for 1000 times");
				}
			}//end while
		}
	}
	
	public void checkOneFlowIsTargetFlow(FlowKey flowKey) {
		double lossRate = GlobalData.Instance().getLossRateForOneFlow(flowKey);
		double totalVolume = GlobalData.Instance().getTotalVolumeForOneFlow(flowKey);
		if (totalVolume < TargetFlowSetting.TARGET_FLOW_TOTAL_VOLUME_THRESHOLD 
				|| lossRate < TargetFlowSetting.TARGET_FLOW_LOST_RATE_THRESHOLD) {
			GlobalData.Instance().gTargetFlowMap.remove(flowKey);
			return;
		}
		GlobalData.Instance().gTargetFlowMap.put(flowKey, 1);
	}
	
	public void start() {
		System.out.println("Starting Host2MainThread");
		if (thread == null)
		{
			thread = new Thread (this, "Host2MainThread");
			thread.start();
		}
	}
	
	private boolean canExit() {
		if (GlobalData.Instance().AllIntervalsCompleted 
				&& GlobalData.Instance().s4exit) {
			GlobalData.Instance().H2TruthQueue.clear();
			GlobalData.Instance().h2exit = true;
			System.out.println("Host2MainThread exit");
			return true;
		}
		return false;
	}
}
