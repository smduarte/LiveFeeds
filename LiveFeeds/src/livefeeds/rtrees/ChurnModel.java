package livefeeds.rtrees;

import static livefeeds.rtrees.config.Config.Config;
import umontreal.iro.lecuyer.probdist.ExponentialDist;
import umontreal.iro.lecuyer.probdist.TruncatedDist;
import umontreal.iro.lecuyer.probdist.WeibullDist;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.util.Num;

public class ChurnModel {

	static double gamma(double x) {
		return Math.exp(Num.lnGamma(x));
	}

	final private double lambda = Config.MEAN_SESSION_DURATION / gamma(1 + 1 / Config.WEIBULL_SHAPE);
	final RandomVariateGen arrivalGen = new RandomVariateGen(new MRG32k3a(), new ExponentialDist(Config.AVERAGE_ARRIVAL_RATE));
	final RandomVariateGen sessionGen = new RandomVariateGen(new MRG32k3a(), new TruncatedDist(new WeibullDist(Config.WEIBULL_SHAPE, 1 / lambda, 0), 0, Config.MAX_SESSION_DURATION));

	
	public double nextArrival() {
		return arrivalGen.nextDouble() ;
	}
	
	
	public double sessionDuration() {
		return sessionGen.nextDouble() ;
	}
	
}
