package randGenerator;

public class RandomGenerator {
	double now=0.0;
	double delta=0.0001;
	
	public double nextDouble() {
		now += delta;
		if (Math.abs(now - 1) < 0.00000001) {
			now = 0;
		}
		return now;
	}
}
