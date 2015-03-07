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
		double byteSamplingRate = PacketSampleSetting.DEAFULT_BYTE_SAMPLE_RATE;
		if (GlobalSetting.IS_USE_REPLACE_MECHANISM == 1) {
			byteSamplingRate = PacketSampleSetting.REPLACEMENT_BYTE_SAMPLE_RATE;			
		}
		double packetSampleRate = packet.length * byteSamplingRate;		
		double randDouble = random.nextDouble();
		
		if (GlobalSetting.DEBUG && packet.srcip == GlobalSetting.DEBUG_SRCIP) {
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter("tradi_lossRate_samplingRate.txt", true));
				writer.write(packet.srcip + " " 
						+ packet.length + " " + byteSamplingRate + " " 
						+ packetSampleRate + " " + randDouble + "\n\r");
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (randDouble < packetSampleRate) {
			return true;
		}
		return false;
	}

}
