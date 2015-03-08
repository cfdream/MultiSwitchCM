package multi.controller;

import multi.main.GlobalData;


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
			
			if (ControllerDataInOneInterval.Instance().numSwitchDataReceived == 4) {
				ControllerDataInOneInterval.Instance().analyze();	
			} else {
				System.out.println("disorder, discard the result");
			}
			
			if (GlobalData.Instance().AllIntervalsCompleted) {
				break;
			}
			
			//clear the interval data for future usage
			//the data are used by each switch and host 2
			ControllerDataInOneInterval.Instance().clearEveryInterval();
		}
		
		System.out.println("Controller exit");
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
