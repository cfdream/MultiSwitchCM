package multi.lib;

import java.util.ArrayList;
import java.util.Iterator;

public class library {
	public static class AverageDeviation {
		public double avgValue;
		public double deviation;
		
		public AverageDeviation(double avg, double dev) {
			avgValue = avg;
			deviation = dev;
		}
	}
	
	public static AverageDeviation computeAverageDeviation(ArrayList<Double> listValue) {
		double totalValue = 0;
		for (Iterator<Double> iterator = listValue.iterator(); iterator.hasNext();) {
			double value = iterator.next();
			totalValue += value;
		}
		
		double avgValue = totalValue / listValue.size();
		
		double standardDeviation = 0;
		for (Iterator<Double> iterator = listValue.iterator(); iterator.hasNext();) {
			double value = iterator.next();
			standardDeviation += Math.pow(value - avgValue, 2);
		}
		standardDeviation = Math.pow(standardDeviation / listValue.size(), 0.5);
		
		AverageDeviation averageDeviation = new AverageDeviation(avgValue, standardDeviation);
		return averageDeviation;
	} 
}
