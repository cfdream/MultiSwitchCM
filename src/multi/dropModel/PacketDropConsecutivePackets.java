package multi.dropModel;


import java.util.Random;

import randGenerator.RandomGenerator;

import multi.data.Packet;
import multi.main.GlobalSetting;


/*
 * drop consecutive packets (one RTT time) when queue overflow happens
 */
public class PacketDropConsecutivePackets extends PacketDropModel{
	static long NOT_START = 0;
	static long MILLISECOND = 1000;	//1ms = 1000 us
	
	boolean isIntervalRandomed;  //whether the interval has used random function to decide drop or not.
	boolean isIntervalDropped;
	long ongoingMilliSecond;
	Random random;
	RandomGenerator randomGenerator = new RandomGenerator();
	
	public PacketDropConsecutivePackets() {
		super();
		this.isIntervalRandomed = false;
		this.isIntervalDropped = false;
		ongoingMilliSecond = NOT_START;
		//TODO: set back;
		random = new Random(System.currentTimeMillis());
	}

	@Override
	public boolean drop(Packet packet) {
		long ithMillsecond = packet.microsec / (MILLISECOND * GlobalSetting.RTT);
		if (ithMillsecond != ongoingMilliSecond) {
			ongoingMilliSecond = ithMillsecond;
			isIntervalRandomed = false;
			isIntervalDropped = false;
		}
		if (!isIntervalRandomed) {
			isIntervalRandomed = true;
			//random to decide whether dropping the interval or not
			double randNum = random.nextDouble();
			if (GlobalSetting.DEBUG) {
				randNum = randomGenerator.nextDouble();	
			}
			
			if (randNum < GlobalSetting.VOLUME_DROP_RATE) {
				isIntervalDropped = true;
			}
		}
		return isIntervalDropped;
	}
	
}
