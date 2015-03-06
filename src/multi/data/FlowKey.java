package multi.data;
public class FlowKey {

	// header
	public long srcip;

	public FlowKey(long srcip) {
		super();
		this.srcip = srcip;
	}

	public FlowKey(Packet packet) {
		srcip = packet.srcip;
	}

	@Override
	public int hashCode() {
		return (int) (srcip);
	}

	/*
	 * for flow comparison
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		/* one-tuple */
		FlowKey other = (FlowKey) obj;
		if (srcip == other.srcip) {
			return true;
		}

		return false;
	}
}
