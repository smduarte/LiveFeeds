package sensing.persistence.util.sumo.segmentspeed2;


String setupName 	= "segmentspeed2.SUMOTrafficSpeedSetup"
String inDir 		= "results/segmentspeed2/rateresults"
String outDir 		= "results/segmentspeed2/ss_error_2"
rates = [1,2,3,5,7,10,50,100]
String metricName 	= "hist_occup_errorkmh_all"


double binsize 		= 10;

new File(outDir).mkdirs()


List fields = ["count", "vcount", "minspeed", "maxspeed", "avgspeed", "stdspeed", "vpminspeed", "vpmaxspeed", "vpavgspeed", "vpstdspeed", "vpcount",
	"edgespeed", "edgeoccup", "edgedensity", "edgecount", "edgemaxspeed", "edgelanes", "edgelen", "edgetl", "edgecontained",
	"segmentId", "time", "avgspeederr", "edgerating", "blcount", "blvcount", "blavgspeed", "blstdspeed"]



def binfLength = { vals -> (int)(vals.edgelen.toDouble()/binsize)}

def binfCount = {vals -> (int)(vals.count.toInteger()/binsize)}

def binfReadStd = {vals -> Math.min(Integer.MAX_VALUE,(int)(vals.stdtt100.toDouble()/binsize))}

def binfSamplStd = {vals -> Math.min(Integer.MAX_VALUE,(int)(vals.stdtt.toDouble()/binsize))}


def speedRate = { vals ->
	vals.edgespeed.toDouble()/(vals.edgemaxspeed.toDouble()*3.6)*100
}

def binfSpeedRate = {vals -> 
	(int)(speedRate(vals)/binsize)
}

def errorKmh = {vals ->
	vals.avgspeederr.toDouble();	
}

def sumoErrorKmh = { vals ->
	Math.abs(vals.avgspeed.toDouble()-vals.edgespeed.toDouble())
}



def wErrorKmh = {vals ->
	vals.wavgspeederr.toDouble();
}

def binfVCount = { vals ->
	(int)(vals.vcount.toDouble()/binsize)
}

def binfBlVCount = { vals ->
	(int)(vals.blvcount.toDouble()/binsize)
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
		def weight = bin.collect{it.weight}
		"${idx*binsize}\t${error.sum()/weight.sum()}\t${stdf(error)}\t${bin.size()}\t${error.min()}\t${error.max()}\n"
	} else {
		"${idx*binsize}\t0\t0\t0\t0\t0\n"
	}
}

def outfFreq = {bin, idx -> String.format("%.2f\t%d\n", idx*binsize, bin.size())}


def run = { int rate, Closure filterF, Closure binf, Closure errorf, Closure outf, Closure weightf = {1.0} -> 
	String fName = "$inDir/${setupName}_err_${rate}.gpd"
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

			double speedrate = vals.edgespeed.toDouble()/vals.edgemaxspeed.toDouble()*3.6
			if(edgelen >= 50 && filterF(vals)) { // && vals.edgetl.equals("1") && count == realcount && count >=5) {
				double std = vals.blstdspeed.toDouble()
				if(std.isNaN()) std = 0
				int bin = binf(vals)
				double weight = weightf(vals)
				double error = errorf(vals) * weight
				
				if(!samples[bin]) samples[bin] = []
				samples[bin] << [error: error, weight: weight, std: std];
				totalError += error
				
				minError = Math.min(minError, error);
				if(error > maxError) {
					maxError = error;
					maxErrorSegmentId = vals.segmentId;
				}
				numSamples += weightf(vals);
			}
		}
	}
	
	printf("Valid: %.2f\nMean error: %.2f\nMin error: %.2f\nMax Error: %.2f (%s)\n", numSamples, totalError/numSamples, minError, maxError, maxErrorSegmentId);
	
	
	String outFName ="$outDir/ss_${metricName}_${rate}.gpd"
	
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
		true //vals.edgetl.equals("1") // vals.vcount.toDouble() > 1
	}, binfSpeedRate, errorKmh, outfStats )] //{vals -> vals.edgerating.toDouble()}
}

File outF = new File("$outDir/ss_${metricName}.gpd")
outF.write("")
meanerror.each{ 
	def(rate,error) = it
	outF.append("$rate\t$error\n")	
}


