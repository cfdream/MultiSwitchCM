package multi.switcher;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import multi.data.Packet;

public class SwitchData {
	public String name;
	//one switch has only one inputQueue
	public LinkedBlockingQueue<Packet> inputQueue;
	//one switch has many outputQueueS
	public ArrayList<LinkedBlockingQueue<Packet>> outputQueueS;
	//switch 4 can output to H2Set
	public ConcurrentMap<Packet, Integer> H2InputSet;
	//whether this switch drops packets
	public boolean switchDropPackets;
	
	public SwitchData() {
		switchDropPackets = false;
	}
}
