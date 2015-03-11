package multi.sampleModel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import multi.data.Packet;
import multi.main.GlobalSetting;

public class PacketSampleModelTraditional extends PacketSampleModel{
	
	public PacketSampleModelTraditional() {
		super();
	}
	
	@Override
	public boolean isSampled(Packet packet) {
		double byteSamplingRate = PacketSampleSetting.DEAFULT_BYTE_SAMPLE_RATE
				*PacketSampleSetting.BYTE_RATE_INCREASE_RATIO;
		
		double packetSampleRate = packet.length * byteSamplingRate;		
		double randDouble = random.nextDouble();
		if (GlobalSetting.DEBUG) {
			randDouble = randomGenerator.nextDouble();	
		}
		
		if (randDouble < packetSampleRate) {
			return true;
		}
		return false;
	}

}
