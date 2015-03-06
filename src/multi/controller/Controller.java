package multi.controller;


public class Controller implements Runnable {

	private Thread thread;
	
	public void run() {
		// TODO Auto-generated method stub
		// go through all packets, sample&hold
		while (true) {
			while (ControllerDataInOneInterval.Instance().numSwitchDataReceived < 4
					|| ControllerDataInOneInterval.Instance().isH2DataReceived == false) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//while
			
			ControllerDataInOneInterval.Instance().analyze();
			ControllerDataInOneInterval.Instance().clear();
		}
	}

	public void start()
	{
		System.out.println("Starting Controller_thread" );
		if (thread == null)
		{
			thread = new Thread (this, "Controller");
			thread.start();
		}
	}
}
