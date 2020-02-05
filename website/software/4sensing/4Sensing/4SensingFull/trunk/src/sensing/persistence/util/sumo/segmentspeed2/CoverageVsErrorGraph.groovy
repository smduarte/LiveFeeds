package sensing.persistence.util.sumo.segmentspeed2;
import sensing.persistence.util.sumo.*;
import  sensing.persistence.simsim.speedsense.sumo.SUMOEdgeStatsSource;
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;
import java.awt.geom.Rectangle2D;

List fields = ["count", "vcount", "minspeed", "maxspeed", "avgspeed", "stdspeed", "vpminspeed", "vpmaxspeed", "vpavgspeed", "vpstdspeed", "vpcount",
	"edgespeed", "edgeoccup", "edgedensity", "edgecount", "edgemaxspeed", "edgelanes", "edgelen", "edgetl", "edgecontained",
	"segmentId", "time", "avgspeederr", "edgerating", "blcount", "blvcount", "blavgspeed", "blstdspeed"]


String setupName 	= "segmentspeed2.SUMOTrafficSpeedSetup_10m"
String metricName	= "dinamic_length_coverage"
String inDir		= "results/segspeed2/rateresults"
String outDir 		= "results/segspeed2/ss_error_2"
String segRating	= "segmentRating_1800.tsv" 
def rates 			= [1,2,3,5,7,10]

new File(outDir).mkdirs()


Map segmentRating = [:];

File sr = new File(segRating);

sr.eachLine{ String line ->
	def (segmentId, traveledDistance) = line.split();
	segmentRating[segmentId] = traveledDistance.toDouble();
}


double binsize = 1
int binMax = 50;
int srStep = 10;

def segmentCoveragef = {1}
def ratingCoveragef  = {segmentRating[it.segmentId] }
def lengthCoveragef  = {it.edgelen.toDouble()}

def speedRatef = { vals, rate ->
	double edgespeed = vals.edgespeed.toDouble();
	double edgemaxspeed = vals.edgemaxspeed.toDouble() * 3.6;
	edgespeed <= (rate/100.0)*edgemaxspeed
}

def occupancyf = { vals, rate ->
	vals.edgeoccup.toDouble() <=  rate;
}


rates.each{ nodeRate ->

	List coverageMatrix = [ (0..binMax).collect{it*binsize}]; 
	File outF = new File("$outDir/ss_${metricName}_${nodeRate}.gpd");
	outF.write(""); // "#Error(s/m)\t${(10..50).step(10).collect{'$it%'}.join('\t')}\n"
	
	File outFOverall = new File("$outDir/ss_${metricName}_${nodeRate}_overall.gpd")
	outFOverall.write("#Speed rate\tCoverage\n");
	
	def run = {int rate, Closure  coveragef, Closure targetf, Closure filter = {true} -> 
		File results = new File("$inDir/${setupName}_err_${nodeRate}.gpd");
		File baselineResults = new File("$inDir/${setupName}_err_100.gpd");
		
		List coverageAll = []
		ResultReader reader = new ResultReader(results, fields);
		ResultReader baselineReader = new ResultReader(baselineResults, fields);
		
		while(reader.hasNext()) {
			baselineReader.readNextTimeFrame();
			int targetCoverage = 0;
			int totalValid = 0;
			 baselineReader.results.each {entry ->
				double edgelen = entry.value.edgelen.toDouble();
				double edgespeed = entry.value.edgespeed.toDouble();
				double edgemaxspeed = entry.value.edgemaxspeed.toDouble() * 3.6;
	//			double count100 = entry.value.count100.toInteger();
	//			double realcount100 = entry.value.realcount100.toInteger();
				
				if(edgelen >= 50 && filter(entry.value)) {
					totalValid++;
					if(targetf(entry.value, rate)) targetCoverage += coveragef(entry.value);
				}
			}
			reader.readNextTimeFrame();
			println "$rate] ${reader.timeFrame} targetCoverage: $targetCoverage totalValid: $totalValid"
	
			List coverage = [];
			coverage[binMax] = 0;
			double pass = 0;
			reader.results.each{String segmentId, Map vals ->
				double edgelen = vals.edgelen.toDouble();		
				double edgemaxspeed = vals.edgemaxspeed.toDouble() * 3.6;
				double edgespeed = vals.edgespeed.toDouble();
				double count = vals.count.toDouble();
				double vcount= vals.vcount.toDouble();
				
				if( edgelen >= 50 && filter(vals) && targetf(vals, rate)) {
					pass++;
					double error = vals.avgspeederr.toDouble();
					int bin = Math.min(binMax,(int)(error/binsize));
					if(null == coverage[bin]) coverage[bin] = 0;
					coverage[bin] += coveragef(vals);
				}
			}
			println "$rate] ${reader.timeFrame} coverage: $coverage"
			double cov = coverage.findAll{it}.sum();
			println "$rate] pass: $pass in ${reader.results.size()} covtotal:$cov (${cov/targetCoverage*100}%)"
			List coverageAcc = coverage.collect{it?:0}
			
			coverage.eachWithIndex{ val, idx ->
				if(val) ((idx+1)..<coverage.size()).each{coverageAcc[it] += val}
			}
			
			coverageAcc = coverageAcc.collect{it*1.0/targetCoverage}
			println "$rate] ${reader.timeFrame} coverageAcc: $coverageAcc"
			
			coverageAcc.eachWithIndex{ val, idx ->
				coverageAll[idx] = (coverageAll[idx] ?: []) + val;
			}
			
		}
		
		List coverageRate = coverageAll.collect{ vals -> vals ? vals.sum()/vals.size() : 0}
		println "$rate] coverageRate: $coverageRate"
		coverageMatrix << coverageRate;
		
	}
	
	(srStep..100).step(srStep).each{run(it, ratingCoveragef, occupancyf)}
	
	coverageMatrix.tail().eachWithIndex{ val, idx ->
		String line = "${(idx+1)*srStep}\t${val[val.size()-1]}\n";
		print line;
		outFOverall.append(line);
	}
	
	
	coverageMatrix.transpose().each{ 
		println it.collect{String.format("%.2f",it)}.join("\t");
		outF.append("${it.join('\t')}\n");
	}
}
