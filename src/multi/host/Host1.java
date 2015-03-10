package multi.host;

import multi.fileRead.ReaderAndHandle;
import multi.main.GlobalData;
import multi.main.GlobalSetting;

public class Host1 implements Runnable {
	private Thread thread;
	
	public void run() {
		ReaderAndHandle reader = new ReaderAndHandle();
		reader.readTillIthIntervalPackets(
				GlobalSetting.SIMULATE_INVERVALS,
				GlobalData.Instance().S1InputQueue,
				"h1");
	}

	public void start()
	{
		System.out.println("Starting host1");
		if (thread == null)
		{
			thread = new Thread (this, "host1");
			thread.start();
		}
	}
}
