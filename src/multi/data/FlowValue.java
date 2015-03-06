package multi.data;

public class FlowValue {
	public long volume; // bytes
	public long normalVolume;
	public long lostVolume;
	public long sampledNormalVolume;
	public int numAllPackets;
	public int numNormalPackets;
	public int numLostPackets;
	public int numSampledNormalPackets;
	public double lossRate;
	public double accuracy;
	public long startime;
	public long endtime;

	public FlowValue() {
		super();
		this.volume = 0;
		this.normalVolume = 0;
		this.lostVolume = 0;
		this.sampledNormalVolume = 0;
		this.numAllPackets = 0;
		this.numNormalPackets = 0;
		this.numLostPackets = 0;
		this.numSampledNormalPackets = 0;
		this.lossRate = 0;
		this.accuracy = 0;
		this.startime = Long.MAX_VALUE;
		this.endtime = Long.MIN_VALUE;
	}

	public void clearSwitchData() {
		this.sampledNormalVolume = 0;
		this.numSampledNormalPackets = 0;
	}
}
