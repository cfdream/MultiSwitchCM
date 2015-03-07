package multi.fileRead;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import multi.data.Packet;
import multi.main.GlobalData;
import multi.main.GlobalSetting;

public class Reader {
	/*
	 * About experiment data
	 */
	public static String FILE_HEADER = "C:\\workspace\\data\\equinix-sanjose.dirA.20120920-";
	public static String FILE_TAILER = ".UTC.anon.pcap.csv";
	public static int MINUTE_STRING_START = 130000;
	public static int MINUTE_STRING_DELTA = 100; // file of ith minute:
													// MINUTE_START +
													// MINUTE_INTERVAL * I
	public static int SECONDS_IN_ONE_FILE = 60;
	public static long START_USECOND = 21600000000L; // microsecond

	long currentUSecond; // data in currentSecond at reader starts at reader
	int fileString;
	BufferedReader reader;

	public Reader() {
		super();
		this.currentUSecond = START_USECOND;
		this.fileString = MINUTE_STRING_START;
		this.reader = null;
	}

	/*
	 * @return: line read, or null
	 */
	private String readOneLineFromFile(Integer ithInterval) {
		String line = null;
		try {
			if (null == reader) {
				// open the first file
				int firstFileNO = GlobalSetting.INTERVAL_SECONDS
						/ SECONDS_IN_ONE_FILE;
				fileString = MINUTE_STRING_START + firstFileNO
						* MINUTE_STRING_DELTA;
				String filePath = FILE_HEADER + fileString + FILE_TAILER;
				File file = new File(filePath);
				if (file.exists() && !file.isDirectory()) {
					reader = new BufferedReader(new FileReader(filePath));
				}
			} else {
				// reader is just the exact file
			}

			// try reading one line is ok
			while ((line = reader.readLine()) == null) {
				// close current file;
				// open next file and read;
				reader.close();

				// open the next file;
				fileString += MINUTE_STRING_DELTA;
				String filePath = FILE_HEADER + fileString + FILE_TAILER;
				File file = new File(filePath);
				if (!file.exists() || file.isDirectory()) {
					System.out.println(filePath + " not exist");
					return null;
				}
				System.out.println(filePath + " starts");
				reader = new BufferedReader(new FileReader(filePath));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return line;
	}

	/*
	 * ithInterval: [0, SIMULATE_INTERVALS) 
	 * 
	 * @return: -1: error, 0: succ
	 */
	public int readTillIthIntervalPackets(int ithInterval,
			LinkedBlockingQueue<Packet> outputQueue, String hostName) {
		long startUSecond = START_USECOND;
		long endUSecond = startUSecond + 
				GlobalSetting.SIMULATE_INVERVALS *
				GlobalSetting.INTERVAL_SECONDS * GlobalSetting.SECOND_2_USECOND;

		long totalVolume = 0; // header + body
		long totalLostVolume = 0;
		int lines = 0;

		String line = null;
		while ((line = readOneLineFromFile(ithInterval)) != null) {
			Packet packet = Packet.parsePacket(line);
			if (null == packet) {
				continue;
			}

			// read starting from startUSecond, stopping at endUSecond
			if (packet.microsec < startUSecond) {
				continue;
			}
			if (packet.microsec > endUSecond) {
				break;
			}
			if (canExit(hostName)) {
				break;
			}
			
			//add the packet into a Queue
			try {
				outputQueue.put(packet);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// handle the packet
			lines++;
			totalVolume += packet.length;
		}
		
		System.out.println("summary info for " + ithInterval + " interval: "
				+ "\r\n lines=" + lines + "\r\n totalVolumeForAllFlows="
				+ totalVolume + "\r\n totoalLostVolume=" + totalLostVolume
				+ "\r\n VolumeLostRatio=" + 1.0 * totalLostVolume / totalVolume
				);
		return 0;
	}
	
	public boolean canExit(String hostName) {
		if (!GlobalData.Instance().AllIntervalsCompleted) {
			return false;
		}
		
		if (hostName == "h1") {
			System.out.println("h1 exit");
			GlobalData.Instance().h1exit = true;
			return true;
		}
		
		if (hostName == "h2" && GlobalData.Instance().s4exit) {
			System.out.println("h2 exit");
			return true;
		}
		
		return false;
	}
}
