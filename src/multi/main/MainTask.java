package multi.main;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import multi.controller.Controller;
import multi.data.Packet;
import multi.host.Host1;
import multi.host.Host2GenereateTargetSet;
import multi.host.Host2MainThread;
import multi.host.Host2ReadGroundTruth;
import multi.switcher.Switch;
import multi.switcher.SwitchData;

public class MainTask {
	public static void main(String[] args) {
		//init 4 switches
		SwitchData s1Data = new SwitchData();
		SwitchData s2Data = new SwitchData();
		SwitchData s3Data = new SwitchData();
		SwitchData s4Data = new SwitchData();
		
		ArrayList<LinkedBlockingQueue<Packet>> s1OutQueueList = new ArrayList<LinkedBlockingQueue<Packet>>();
		s1OutQueueList.add(GlobalData.S2InputQueue);
		s1OutQueueList.add(GlobalData.S3InputQueue);
		s1Data.name = "s1";
		s1Data.inputQueue = GlobalData.S1InputQueue;
		s1Data.outputQueueS = s1OutQueueList;
		
		ArrayList<LinkedBlockingQueue<Packet>> s2OutQueueList = new ArrayList<LinkedBlockingQueue<Packet>>();
		s2OutQueueList.add(GlobalData.S4InputQueue);
		s2Data.name = "s2";
		s2Data.inputQueue = GlobalData.S2InputQueue;
		s2Data.outputQueueS = s2OutQueueList;
		s2Data.switchDropPackets = true;			//only S2 drops packets
		
		ArrayList<LinkedBlockingQueue<Packet>> s3OutQueueList = new ArrayList<LinkedBlockingQueue<Packet>>();
		s3OutQueueList.add(GlobalData.S4InputQueue);
		s3Data.name = "s3";
		s3Data.inputQueue = GlobalData.S3InputQueue;
		s3Data.outputQueueS = s2OutQueueList;
		
		s4Data.name = "s4";
		s4Data.inputQueue = GlobalData.S4InputQueue;
		s4Data.H2InputSet = GlobalData.H2InputSet;
		
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
		
		/*controller*/
		Controller controller = new Controller();
		controller.start();
		
		/*Host2GenereateTargetSet*/
		Host2GenereateTargetSet host2GenereateTargetSet = new Host2GenereateTargetSet();
		host2GenereateTargetSet.start();
	}
}
