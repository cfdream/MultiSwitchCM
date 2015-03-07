package multi.host;

import multi.fileRead.Reader;
import multi.main.GlobalData;
import multi.main.GlobalSetting;

public class Host1 implements Runnable {
	private Thread thread;
	
	public void run() {
		Reader reader = new Reader();
		reader.readTillIthIntervalPackets(
				GlobalSetting.SIMULATE_INVERVALS,
				GlobalData.S1InputQueue);
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
