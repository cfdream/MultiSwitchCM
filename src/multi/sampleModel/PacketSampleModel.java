package multi.sampleModel;

import java.util.Random;
import multi.data.Packet;


public abstract class PacketSampleModel {
	
	Random random;
	
	public PacketSampleModel() {
		super();
		random = new Random(System.currentTimeMillis());
	}
	
	abstract public boolean isSampled(Packet packet);
}
