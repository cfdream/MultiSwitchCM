package multi.data;
import java.util.Comparator;

public class FlowValueComparable implements Comparator<FlowValue> {
	@Override
	public int compare(FlowValue o1, FlowValue o2) {
		return o1.volume < o2.volume ? -1 : (o1.volume == o2.volume ? 0 : 1);
	}
}