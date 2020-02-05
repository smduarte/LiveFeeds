package sensing.persistence.util.sumo

List fields = ["count", "realcount", "mintt", "maxtt", "avgtt", "stdtt", "minexptt", "vpminspeed", "vpmaxspeed", "vpavgspeed", "vpstdspeed", "vpcount",
	"edgespeed", "edgeoccup", "edgedensity", "edgecount", "edgemaxspeed", "edgelanes", "edgelen", "edgetl", "edgecontained",
	"segmentId", "time"]

String setupName 	= "SUMOTrafficSpeedTTSetup_10m"
String baseRunName	= "run_ttime_10m30-10-2011-16-48-46"
String runName 		= "run_ttime_10m30-10-2011-16-48-46"  //"run_noderate_3_17-05-2011-11-09-24"  "run_noderate03-05-2011-18-40-30" "run_ttime_10m30-10-2011-16-48-46"
String outDir 		= "results/traveltime/rateresults"
def rates 			= [1,2,3,5,7,10,20,30,40,50,60,70,80,90, 100];

File results100 = new File("results/sensing.persistence.simsim.speedsense.sumo.setup.${setupName}_100/$baseRunName/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/results_null.gpd");


def run = { int rate ->
	String resultsFName = "results/sensing.persistence.simsim.speedsense.sumo.setup.${setupName}_${rate}/$runName/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/results_null.gpd"
	File results = new File(resultsFName)
	String outFName = "$outDir/${setupName}_err_${rate}.gpd"
	//(resultsFName.split("/")[0..-2] + "results_err.gpd").join("/")
	File outFile = new File(outFName)
	BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))
	writer.write("# $runName\n");
	
	println "out: $outFile"
	
	ResultReader r = new ResultReader(results100, fields)
	r.readNextTimeFrame();
	
	int lt5 = 0;
	int lt60 = 0;
	int total = 0;
	
	results.eachLine{String line ->
		Map lineVals = [:]
		
		line.split().eachWithIndex{ val, idx -> lineVals[fields[idx]] = val}
		if(lineVals.time.toDouble() != r.timeFrame) {
			r.readNextTimeFrame()	
		}
		if(r.results[lineVals.segmentId]) { //TODO nao devia ser necessario
			double avgTT = lineVals.avgtt.toDouble()
			double refTT = r.results[lineVals.segmentId].avgtt.toDouble()
			double length = r.results[lineVals.segmentId].edgelen.toDouble()
			if(!avgTT.isInfinite() && !refTT.isInfinite()) { //TODO nao devia ser necessario
				double err = Math.abs(avgTT - refTT)
				println "${lineVals.time}\t${lineVals.segmentId}: $err\t($avgTT, $refTT)"
				line += "\t $err\t${r.results[lineVals.segmentId].count}\t${r.results[lineVals.segmentId].realcount}\t${r.results[lineVals.segmentId].stdtt}"
				writer.writeLine(line)
				if(length >= 50) {
					total++;
					if(err <= 5) lt5++;
					if(err <= 60) lt60++;
				}
			}
		}
	}
	
	println "total: $total"
	println "lt5: $lt5 (${lt5*1.0/total*100})"
	println "lt60: $lt60 (${lt60*1.0/total*100})"
	
	writer.close()
}

rates.each{run(it)}
