package multi.lib;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import multi.data.Packet;

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
	
	public static AverageDeviation computeAverageDeviationInt(ArrayList<Integer> listValue) {
		Integer totalValue = 0;
		for (Iterator<Integer> iterator = listValue.iterator(); iterator.hasNext();) {
			Integer value = iterator.next();
			totalValue += value;
		}
		
		Integer avgValue = totalValue / listValue.size();
		
		double standardDeviation = 0;
		for (Iterator<Integer> iterator = listValue.iterator(); iterator.hasNext();) {
			Integer value = iterator.next();
			standardDeviation += Math.pow(value - avgValue, 2);
		}
		standardDeviation = Math.pow(standardDeviation / listValue.size(), 0.5);
		
		AverageDeviation averageDeviation = new AverageDeviation(avgValue, standardDeviation);
		return averageDeviation;
	} 
	
	public static class PQSortPkts implements Comparator<Packet> {
		 
		public int compare(Packet one, Packet two) {
			return (int) (one.microsec - two.microsec);
		}
	}
}
