package multi.ecmp;

import multi.data.Packet;

public class ECMP {
	public static int getOutIndex(Packet pkt, int numOfOuts) {
		//calculate based srcip, dstip, srcport, dstport, protocol
		//long hashValue = pkt.srcip * pkt.srcport * pkt.destport * pkt.protocol
		//		+ pkt.destip * pkt.srcport + pkt.destport * pkt.protocol;
		long hashValue = pkt.srcip + pkt.destip + pkt.srcport + pkt.destport + pkt.protocol;
		if (hashValue < 0) {
			hashValue *= -1;
		}
		//System.out.println(hashValue);
		return (int) (hashValue % numOfOuts);
	}
}
