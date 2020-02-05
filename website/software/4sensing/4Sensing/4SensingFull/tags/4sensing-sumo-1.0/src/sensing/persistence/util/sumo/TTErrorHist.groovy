package sensing.persistence.util.sumo

String setupName 	= "SUMOTrafficSpeedTTSetup"
String outDir 		= "results/traveltime/tt_error7"
rates = [1,2,3,5,7,10,100]

String metricName 	= "hist_speedrate_errorsm_all"
double binsize 		= 5;

List fields = ["count", "realcount", "mintt", "maxtt", "avgtt", "stdtt", "minexptt", "vpminspeed", "vpmaxspeed", "vpavgspeed", "vpstdspeed", "vpcount",
	"edgespeed", "edgeoccup", "edgedensity", "edgecount", "edgemaxspeed", "edgelanes", "edgelen", "edgetl", "edgecontained",
	"segmentId", "time", "tterror", "count100", "realcount100", "stdtt100"]


def binfPredictedRate = { vals ->
	int count = vals.count.toInteger()
	int realCount = vals.realcount.toInteger()
	double p = (count-realCount)/count*100
	int bin = (int)(p/binsize)
}

def binfLength = { vals -> (int)(vals.edgelen.toDouble()/binsize)}

def binfCount = {vals -> (int)(vals.count.toInteger()/binsize)}

def binfReadStd = {vals -> Math.min(Integer.MAX_VALUE,(int)(vals.stdtt100.toDouble()/binsize))}

def binfSamplStd = {vals -> Math.min(Integer.MAX_VALUE,(int)(vals.stdtt.toDouble()/binsize))}

def binfPredicRate = {vals -> Math.min(9, (int)((vals.count.toDouble() - vals.realcount.toDouble())/vals.count.toDouble()*100/binsize))}

def binfRealRate = { vals -> 
	9-binfPredicRate(vals)
	//vals.realcount.toDouble()/vals.count.toDouble() < 0.9 ? 0 : 1
}

def binfError = {vals -> (int)(vals.tterror.toDouble()/binsize)}


def speedRate = { vals ->
	vals.edgespeed.toDouble()/(vals.edgemaxspeed.toDouble()*3.6)*100
}

def binfSpeedRate = {vals -> 
	(int)(speedRate(vals)/binsize)
	
}

def delayMinuts = { vals -> 
	double expected = vals.edgelen.toDouble()/vals.edgemaxspeed.toDouble();
	Math.max(0, vals.avgtt.toDouble() - expected)/60;
}

def binfDelay = {vals -> 
	double expected = vals.edgelen.toDouble()/vals.edgemaxspeed.toDouble();
	double delay = Math.max(0, vals.avgtt.toDouble() - expected);
	Math.min(100/binsize,(int)(delayMinuts(vals)/binsize))
}

def delayRate = { vals ->
	double expected = vals.edgelen.toDouble()/vals.edgemaxspeed.toDouble();
	double delay = Math.max(0, vals.avgtt.toDouble() - expected);
	delay/expected*100;
}

def binfDelayRate = { vals -> 
	(int)Math.min(100/binsize,delayRate(vals)/binsize);
}


def errorfSecsPerMeter = { vals ->
	vals.tterror.toDouble()/vals.edgelen.toDouble();
}

def errorfSeconds = { vals -> vals.tterror.toDouble()}

def errorfRate = {vals -> 
	double tterror = vals.tterror.toDouble()
	double avgtt = vals.avgtt.toDouble()
	return tterror/avgtt * 100
}

def errorfStd = { vals ->
	vals.stdtt.toDouble()
}

def occup = { vals ->
	vals.edgeoccup.toDouble()
}

def binfOccup = { vals ->
	double o = occup(vals);
	(int)(o/binsize);	
}

def errorFPredicRate = { vals ->
	(1-vals.realcount.toDouble()/vals.count.toDouble())*100
}

def errorFRealRate = { vals ->
	vals.realcount.toDouble()/vals.count.toDouble()*100
}

def errorfSpeedRate = {vals ->
	vals.edgespeed.toDouble()/(vals.edgemaxspeed.toDouble()*3.6)*100
}



def outfAvg = {bin, idx -> 
	
	if(bin) {
		"${idx*binsize}\t${bin.collect{it.error}.sum()/bin.size()}\t${bin.collect{it.std}.sum()/bin.size()}\t${bin.size()}\n"
	} else {
		"${idx*binsize}\t0\t0\t0\n"
	}
	
}

def stdf = {List l ->
	double avg = l.sum()/l.size();
	double sum = l.inject(0) {sum, val -> sum += (val-avg)*(val-avg)};
	Math.sqrt(sum/l.size());
}


def outfStats = {bin, idx ->
	
	if(bin) {
		def error = bin.collect{it.error};
		"${idx*binsize}\t${error.sum()/error.size()}\t${stdf(error)}\t${bin.size()}\t${error.min()}\t${error.max()}\n"
	} else {
		"${idx*binsize}\t0\t0\t0\t0\t0\n"
	}
}

def outfFreq = {bin, idx -> String.format("%.2f\t%d\n", idx*binsize, bin.size())}


def run = { int rate, Closure filterF, Closure binf, Closure errorf, Closure outf -> 
	String fName ="results/traveltime/rateresults/${setupName}_err_${rate}.gpd"
	println fName
	File f = new File(fName)
	
	def samples = []
	
	double totalError = 0
	double minError = Double.POSITIVE_INFINITY;
	double maxError = 0;
	String maxErrorSegmentId;
	double numSamples = 0;
	
	f.eachLine { String line ->	
		if(!line.startsWith('#')) {
			Map vals = [:]
			line.split().eachWithIndex{ val, idx -> vals[fields[idx]] = val}
	
			double edgelen = vals.edgelen.toDouble()
			double avgtt = vals.avgtt.toDouble()
			double count = vals.count.toDouble();
			double realcount = vals.realcount.toDouble();
			double count100 = vals.count100.toDouble()
			double realcount100 = vals.realcount100.toDouble()
			double speedrate = vals.edgespeed.toDouble()/vals.edgemaxspeed.toDouble()*3.6
			if(!avgtt.isInfinite() && edgelen >= 50 && filterF(vals)) { // && vals.edgetl.equals("1") && count == realcount && count >=5) {
				double std = vals.stdtt100.toDouble()
				if(std.isNaN()) std = 0
				int bin = binf(vals)
				double error = errorf(vals);	
				if(!samples[bin]) samples[bin] = []
				samples[bin] << [error: error, std: std];
				totalError += error
				
				minError = Math.min(minError, error);
				if(error > maxError) {
					maxError = error;
					maxErrorSegmentId = vals.segmentId;
				}
				numSamples += 1;
			}
		}
	}
	
	printf("Valid: %.2f\nMean error: %.2f\nMin error: %.2f\nMax Error: %.2f (%s)\n", numSamples, totalError/numSamples, minError, maxError, maxErrorSegmentId);
	
	
	String outFName ="$outDir/tt_${metricName}_${rate}.gpd"
	
	// (fName.split("/")[0..-2] + "tt_error_vs_count_${rate}.gpd").join ("/")
	println outFName
	File outF = new File(outFName);
	outF.write("")
	
	samples.eachWithIndex { bin, idx ->
		String line =  outf(bin, idx)
		print line
		outF.append(line)
	}
	return totalError/numSamples
}


def meanerror = rates.collect{
	[it,run(it, {vals->
		//double speedrate = vals.edgespeed.toDouble()/vals.edgemaxspeed.toDouble()*3.6
		true//vals.edgetl.equals("1")
	}, binfSpeedRate,  errorfSecsPerMeter, outfStats)]
}

File outF = new File("$outDir/tt_${metricName}.gpd")
outF.write("")
meanerror.each{ 
	def(rate,error) = it
	outF.append("$rate\t$error\n")	
}


