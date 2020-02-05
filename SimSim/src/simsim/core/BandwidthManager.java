package simsim.core;

public class BandwidthManager {

	private final double rate;
	private final double peakRate;

	BandwidthManager(double peak, double rate) {
		this.rate = rate;
		this.peakRate = peak;
		this.lastTime = now();
	}

	double accountTransfer(double nb) {
		total += nb;
		credit += elapsed() * rate - nb;
		lastTime = now();
		return Math.max(nb / peakRate, delay());
	}

	public double expectedDelay(double nb) {
		double c = credit + elapsed() * rate - nb;
		return Math.max(nb / peakRate, -c / rate);
	}

	public double delay() {
		double res = -credit / rate;
		return res > 0 ? res : 0;
	}

	private double now() {
		return Simulation.currentTime();
	}

	private double elapsed() {
		return now() - lastTime;
	}

	public void showOutputRate() {
		System.out.printf("%.1f Bytes/s\n", total / (now() - startTime));
	}

	private double total = 0;
	private double credit = 0;
	private double lastTime = 0.0;
	private double startTime = now();
}