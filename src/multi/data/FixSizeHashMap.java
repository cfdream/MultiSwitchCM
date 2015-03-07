package multi.data;
import java.util.ArrayList;

import multi.host.Host2TargetFlowSet;
import multi.main.TargetFlowSetting;
import multi.sampleModel.PacketSampleSetting;

public class FixSizeHashMap {
	public int collideTimes = 0;
	Record[] entries;

	public FixSizeHashMap() {
		super();
		entries = new Record[PacketSampleSetting.SH_BUCKET_SIZE];
		clear();
		collideTimes = 0;
	}

	public void clear() {
		for (int i = 0; i < entries.length; i++) {
			entries[i] = null;
		}
	}

	public int getKey(FlowKey flowKey) {
		int idx = (int) (flowKey.srcip % PacketSampleSetting.SH_BUCKET_SIZE);
		return idx;
	}

	public Long get(FlowKey flowKey) {
		int idx = getKey(flowKey);
		if (null == entries[idx]) {
			return null;
		} else if (entries[idx].flowKey.srcip != flowKey.srcip) {
			/*
			 * if (++collideTimes % 10000 == 0) {
			 * System.out.println(collideTimes); }
			 */
			return null;
		} else {
			return entries[idx].value;
		}
	}

	public void put(FlowKey flowKey, long value) {
		int idx = getKey(flowKey);
		Record newEntry = new Record(flowKey, value);
		if (entries[idx] != null && entries[idx].flowKey.srcip != flowKey.srcip) {
			if (++collideTimes % 10000 == 0) {
				System.out.println("collideTime:" + collideTimes);
			}
		}
		entries[idx] = newEntry;
	}

	public void putWithReplaceMechanism(FlowKey flowKey, long value) {
		
		int idx = getKey(flowKey);
		if (null == entries[idx]) {
			// 1. first time to use the entry
			Record newEntry = new Record(flowKey, value);
			entries[idx] = newEntry;
			return;
		}
		// there is already the same entry in the bucket
		if (entries[idx].flowKey.srcip == flowKey.srcip) {
			// 2. flowKey already in the bucket
			Record newEntry = new Record(flowKey, value);
			entries[idx] = newEntry;
			return;
		}

		Record newEntry = new Record(flowKey, value);

		// 3. the bucket is already occupied by another flow
		// -----------replace mechanism---------------		
		if (1 == TargetFlowSetting.OBJECT_VOLUME_OR_RATE) {
			// ------target is loss rate
			if (!Host2TargetFlowSet.Instance().isTargetFlow(entries[idx].flowKey)) {
				// existing flow is not a target flow
				entries[idx] = newEntry;
				return;
			}
		}
	}

	public ArrayList<Record> getAllEntries() {
		ArrayList<Record> records = new ArrayList<Record>();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i] == null) {
				continue;
			}
			records.add(entries[i]);
		}
		return records;
	}

	public class Record {
		public FlowKey flowKey;
		public Long value;

		public Record(FlowKey flowKeyPara, long valuePara) {
			this.flowKey = flowKeyPara;
			this.value = valuePara;
		}
	}
}
