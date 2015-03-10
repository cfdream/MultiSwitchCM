package multi.dropModel;
import multi.data.Packet;



public abstract class PacketDropModel {
	/*
	 * return KEEP/DROP to sign whether to drop the packet or not 
	 */
	abstract public boolean drop(Packet packet);
}
