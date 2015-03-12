package multi.switcher;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import multi.data.Packet;
import multi.lib.library.PQSortPkts;


public class SwitchData {
	
	public static int S4_PRIORITY_QUEUE_SIZE = 10000;
	public static int S4_PRIORITY_QUEUE_WATI_SIZE = 1000;
	
	
	public String name;
	//one switch has only one inputQueue
	public LinkedBlockingQueue<Packet> inputQueue;
	//one switch has many outputQueueS
	public ArrayList<LinkedBlockingQueue<Packet>> outputQueueS;
	//switch 4 can output to H2Set
	public ConcurrentMap<Packet, Integer> H2InputSet;
	
	//
	PriorityBlockingQueue<Packet> s4PriorityBlockingQueue;
	
	//whether this switch drops packets
	public boolean switchDropPackets;
	
	public SwitchData() {
		switchDropPackets = false;
		
		PQSortPkts pqSortPkts = new PQSortPkts();
		s4PriorityBlockingQueue = new PriorityBlockingQueue<Packet>(10000, pqSortPkts);
	}
}
