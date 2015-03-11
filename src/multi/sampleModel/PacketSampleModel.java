package multi.sampleModel;

import java.util.Random;

import randGenerator.RandomGenerator;
import multi.data.Packet;


public abstract class PacketSampleModel {
	
	Random random;
	RandomGenerator randomGenerator;
	
	public PacketSampleModel() {
		super();
		random = new Random(123456);
		randomGenerator = new RandomGenerator();
	}
	
	abstract public boolean isSampled(Packet packet);
}
