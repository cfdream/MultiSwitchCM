package multi.host;

import multi.fileRead.ReaderAndHandle;
import multi.main.GlobalData;
import multi.main.GlobalSetting;

public class Host2ReadGroundTruth implements Runnable {
	private Thread thread;
	
	public void run() {
		ReaderAndHandle reader = new ReaderAndHandle();
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
