package sensing.persistence.util.sumo;
import  sensing.persistence.simsim.speedsense.sumo.SUMOEdgeStatsSource;
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;
import java.awt.geom.Rectangle2D;

List fields = ["count", "realcount", "mintt", "maxtt", "avgtt", "stdtt", "minexptt", "vpminspeed", "vpmaxspeed", "vpavgspeed", "vpstdspeed", "vpcount",
	"edgespeed", "edgeoccup", "edgedensity", "edgecount", "edgemaxspeed", "edgelanes", "edgelen", "edgetl", "edgecontained",
	"segmentId", "time", "tterror", "count100", "realcount100", "stdtt100"];


String setupName 	= "SUMOTrafficSpeedTTSetup"
String metricName	= "hist_speedrate_congestedcoverage_notl"
String outDir 		= "results/traveltime/tt_error6"

Map segmentRating = [:];

File sr = new File("segmentRating_1800.csv");

sr.eachLine{ String line ->
	def (segmentId, traveledDistance) = line.split(",");
	segmentRating[segmentId] = traveledDistance.toDouble();
}


double binsize = 0.025
int srStep = 10;

def segmentCoveragef = { vals -> 1}
def lengthCoveragef  = { vals -> segmentRating[vals.segmentId] }


[1,2,3,5,7,10].each{ nodeRate ->

	List coverageMatrix = [ (0..99).collect{it*binsize}]; 
	File outF = new File("$outDir/tt_${metricName}_${nodeRate}.gpd");
	outF.write(""); // "#Error(s/m)\t${(10..50).step(10).collect{'$it%'}.join('\t')}\n"
	
	File outFOverall = new File("$outDir/tt_${metricName}_${nodeRate}_overall.gpd")
	outFOverall.write("#Speed rate\tCoverage\n");
	
	def run = {int speedRate, Closure  coveragef, Closure filter -> 
		File results = new File("results/traveltime/rateresults/${setupName}_err_${nodeRate}.gpd");
		File baselineResults = new File("results/traveltime/rateresults/${setupName}_err_100.gpd");
		
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
					if(edgespeed <= (speedRate/100.0)*edgemaxspeed) targetCoverage += coveragef(entry.value);
				}
			}
			reader.readNextTimeFrame();
			println "$speedRate] ${reader.timeFrame} targetCoverage: $targetCoverage totalValid: $totalValid"
	
			List coverage = [];
			coverage[99] = 0;
			double pass = 0;
			reader.results.each{String segmentId, Map vals ->
				double edgelen = vals.edgelen.toDouble();		
				double edgemaxspeed = vals.edgemaxspeed.toDouble() * 3.6;
				double edgespeed = vals.edgespeed.toDouble();
	//			double count100 = vals.count100.toInteger();
	//			double realcount100 = vals.realcount100.toInteger();
				
				if( edgelen >= 50 && edgespeed <= (speedRate/100.0)*edgemaxspeed && filter(vals)) {
					pass++;
					double error = vals.tterror.toDouble()/edgelen;
					int bin = Math.min(99,(int)(error/binsize));
					if(null == coverage[bin]) coverage[bin] = 0;
					coverage[bin] += coveragef(vals);
				}
			}
			println "$speedRate] ${reader.timeFrame} coverage: $coverage"
			println "$speedRate] pass: $pass in ${reader.results.size()} covtotal: ${coverage.findAll{it}.sum()}"
			List coverageAcc = coverage.collect{it?:0}
			
			coverage.eachWithIndex{ val, idx ->
				if(val) ((idx+1)..<coverage.size()).each{coverageAcc[it] += val}
			}
			
			coverageAcc = coverageAcc.collect{it*1.0/targetCoverage}
			println "$speedRate] ${reader.timeFrame} coverageAcc: $coverageAcc"
			
			coverageAcc.eachWithIndex{ val, idx ->
				coverageAll[idx] = (coverageAll[idx] ?: []) + val;
			}
			
		}
		
		List coverageRate = coverageAll.collect{ vals -> vals ? vals.sum()/vals.size() : 0}
		println "$speedRate] coverageRate: $coverageRate"
		coverageMatrix << coverageRate;
		
	}
	
	(10..100).step(srStep).each{run(it, lengthCoveragef, {vals -> !vals.edgetl.equals("1")})}
	
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
