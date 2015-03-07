package multi.host;

import multi.fileRead.Reader;
import multi.main.GlobalData;
import multi.main.GlobalSetting;

public class Host2ReadGroundTruth implements Runnable {
	private Thread thread;
	
	public void run() {
		Reader reader = new Reader();
		reader.readTillIthIntervalPackets(
				GlobalSetting.SIMULATE_INVERVALS,
				GlobalData.Instance().H2TruthQueue,
				"h2");
	}

	public void start()
	{
		System.out.println("Starting Host2ReadGroundTruth");
		if (thread == null)
		{
			thread = new Thread (this, "Host2ReadGroundTruth");
			thread.start();
		}
	}
}
