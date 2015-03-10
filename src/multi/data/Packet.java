package multi.data;

import multi.main.GlobalSetting;

public class Packet {
	public static int HEADER_SIZE = 64; // head bytes

	// header
	public long microsec; // usecond
	public long srcip;
	public long destip;
	public int srcport;
	public int destport;
	public short protocol;
	public long length; // in bytes
	public boolean capturedAtH1;

	public Long getLength() {
		return length;
	}

	public Packet(Long microsec, Long srcip, Long destip, int srcport,
			int destport, short protocol, Long length) {
		this.microsec = microsec;
		this.srcip = srcip;
		this.destip = destip;
		this.srcport = srcport;
		this.destport = destport;
		this.protocol = protocol;
		this.length = length;
		
		capturedAtH1 = false;
	}
	
	public Packet(Packet pkt) {
		this.microsec = pkt.microsec;
		this.srcip = pkt.srcip;
		this.destip = pkt.destip;
		this.srcport = pkt.srcport;
		this.destport = pkt.destport;
		this.protocol = pkt.protocol;
		this.length = pkt.length;
		
		capturedAtH1 = false;
	}
	
	public int getIthInterval() {
		return (int) ((microsec - GlobalSetting.FIRST_INTERVAL_START_USECOND) / 
				GlobalSetting.USECONDS_IN_ONE_INTERVAL + GlobalSetting.FIRST_INTERVAL);		
	}

	public static Packet parsePacket(String line) {
		String[] subStrs = line.split(",");
		if (subStrs.length != 7) {
			return null;
		}
		Long microsec = Long.parseLong(subStrs[0]);
		Long srcip = Long.parseLong(subStrs[1]);
		Long destip = Long.parseLong(subStrs[2]);
		int srcport = Integer.parseInt(subStrs[3]);
		int destport = Integer.parseInt(subStrs[4]);
		if (subStrs[5].equals("null")) {
			return null;
		}
		short protocol = Short.parseShort(subStrs[5]);
		Long length = Long.parseLong(subStrs[6]); // body size
		length += Packet.HEADER_SIZE; // total packet size

		return new Packet(microsec, srcip, destip, srcport, destport, protocol,
				length);
	}
	
	@Override
	public int hashCode() {
		return (int) (srcip+destip+srcport+destport+protocol+microsec);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		/* one-tuple */
		Packet other = (Packet) obj;
		if (srcip == other.srcip &&
				destip == other.destip &&
				srcport == other.srcport &&
				destport == other.destport &&
				microsec == other.microsec &&
				protocol == other.protocol &&
				length == other.length
				) {
			return true;
		}
		return false;
	}
}
