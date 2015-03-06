package multi.host;

import multi.main.GlobalData;


public class Host2GenereateTargetSet implements Runnable {
private Thread thread;
	
	public void run() {
		while (true) {
			if (Host2TargetFlowSet.Instance().toSwitchBuffer) {
				Host2TargetFlowSet.Instance().copyAndSwitchBuffer(GlobalData.Instance().gTargetFlowMap);
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	//sleep 1 ms
		}
	}

	public void start()
	{
		System.out.println("Starting Host2GenereateTargetSet");
		if (thread == null)
		{
			thread = new Thread (this, "Host2GenereateTargetSet");
			thread.start();
		}
	}
}
