package sensing.persistence.util.sumo.segmentspeed2
import sensing.persistence.util.sumo.*;

List fields = ["count", "vcount", "minspeed", "maxspeed", "avgspeed", "stdspeed", "vpminspeed", "vpmaxspeed", "vpavgspeed", "vpstdspeed", "vpcount",
	"edgespeed", "edgeoccup", "edgedensity", "edgecount", "edgemaxspeed", "edgelanes", "edgelen", "edgetl", "edgecontained",
	"segmentId", "time"]

List srFields = ["time", "segmentId", "tdist"]

String setupName 	= "segmentspeed2.SUMOTrafficSpeedSetup"
String baseRunName	= "run_segmentspeed206-06-2011-17-40-43"
String baseSimId = "null"
String runName 		= "run_debug_29-06-2011-20-07-04"
String simId			= "0"
String segRating 	= "segmentRating_1800.tsv"
String outDir 		= "results/segspeed3/rateresults"
def rates 			= [1,2,3,5,7,10,100]

new File(outDir).mkdirs();

File results100 = new File("results/sensing.persistence.simsim.speedsense.sumo.setup.${setupName}_100/$baseRunName/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/results_${baseSimId}.gpd");


def run = { int rate ->	
	String resultsFName = "results/sensing.persistence.simsim.speedsense.sumo.setup.${setupName}_${rate}/$runName/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/results_${simId}.gpd"
	File results = new File(resultsFName)
	String outFName = "$outDir/${setupName}_err_${rate}.gpd"
	//(resultsFName.split("/")[0..-2] + "results_err.gpd").join("/")
	File outFile = new File(outFName)
	BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))
	writer.write("# $runName\n");
	
	println "out: $outFile"
	
	ResultReader r = new ResultReader(results100, fields)
	r.readNextTimeFrame();
	
	Map segmentRating = [:];
	
	File totalsr = new File(segRating);
	
	totalsr.eachLine{ String line ->
		def (segmentId, traveledDistance) = line.split();
		segmentRating[segmentId] = traveledDistance.toDouble();
	}
	
//	ResultReader sr = new ResultReader(new File("segmentRating_1800_ts.tsv"), srFields)
//	sr.readNextTimeFrame();

	double totalErr = 0;
	int numSamples = 0;
	double totalEdgeErr = 0;

	double totalAWErr = 0;
	double aweightSum = 0;
	
	results.eachLine{ String line ->
		if(!line.startsWith('#')) {
			Map lineVals = [:]
			
			line.split().eachWithIndex{ val, idx -> lineVals[fields[idx]] = val}
			if(lineVals.time.toDouble() != r.timeFrame) {
				r.readNextTimeFrame()	
			}

			double avgSpeed = lineVals.avgspeed.toDouble()
			double refSpeed = r.results[lineVals.segmentId].avgspeed.toDouble() 
			double err = Math.abs(avgSpeed - refSpeed)
			double aweight = segmentRating[lineVals.segmentId] 
			
			if(lineVals.edgelen.toDouble() >= 50 ) {
				totalErr += err;
				totalEdgeErr += Math.abs(avgSpeed - lineVals.edgespeed.toDouble())
				numSamples++;

				totalAWErr += err * aweight
				aweightSum += aweight
			}
			line += "\t$err\t$aweight\t${r.results[lineVals.segmentId].count}\t${r.results[lineVals.segmentId].vcount}\t${r.results[lineVals.segmentId].avgspeed}\t${r.results[lineVals.segmentId].stdspeed}"
			writer.writeLine(line)
		}		
	}
	println "$r.timeFrame > err: ${totalErr/numSamples}  wAErr: ${totalAWErr/aweightSum} edgeErr: ${totalEdgeErr/numSamples}"
	writer.close()
}

rates.each{run(it)}
