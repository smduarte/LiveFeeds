package sensing.persistence.util.sumo;

List fields = ["count", "realcount", "mintt", "maxtt", "avgtt", "stdtt", "minexptt", "vpminspeed", "vpmaxspeed", "vpavgspeed", "vpstdspeed", "vpcount",
	"edgespeed", "edgeoccup", "edgedensity", "edgecount", "edgemaxspeed", "edgelanes", "edgelen", "edgetl", "edgecontained",
	"segmentId", "time", "tterror", "count100", "realcount100", "stdtt100"];

Map segmentRating = [:];

File sr = new File("segmentRating_1800.csv");

sr.eachLine{ String line -> 
	def (segmentId, traveledDistance) = line.split(",");
	segmentRating[segmentId] = traveledDistance.toDouble();
}

double totalTd = segmentRating.inject(0){sum, entry -> sum += entry.value}
println "totalTD: ${totalTd/1000} Km"

double binsize = 0.025

List coverageMatrix = [ (0..99).collect{it*binsize}] 


([1,2,3,5,7] + (10..100).step(10)).each { rate -> 
	File results = new File("results/sensing.persistence.simsim.speedsense.sumo.setup.SUMOTrafficSpeedTTSetup_100/run_noderate03-05-2011-18-40-30/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/rateresults/resultserr_${rate}.gpd");

	List coverageAll = []
	ResultReader reader = new ResultReader(results, fields);
	while(reader.hasNext()) {
		reader.readNextTimeFrame();
		List coverage = [];
		coverage[99] = 0;
		
		reader.results.each{String segmentId, Map vals ->
			double edgelen = vals.edgelen.toDouble()
			if( edgelen >= 50) {
				double error = vals.tterror.toDouble()/edgelen;
				int bin = (int)(error/binsize);
				if(bin<=99) {
					if(!coverage[bin]) coverage[bin] = 0;
					coverage[bin] += segmentRating[segmentId];
				}
			}
		}
		
		List coverageAcc = coverage.collect{it?:0}
		
		coverage.eachWithIndex{ val, idx ->
			if(val) ((idx+1)..<coverage.size()).each{coverageAcc[it] += val}
		}
		
		coverageAcc.eachWithIndex{ val, idx ->
			coverageAll[idx] = (coverageAll[idx] ?: []) + val;
		}
		println "${reader.timeFrame}"
	}
	
	List coverageRate = coverageAll.collect{ vals -> (vals ? vals.sum()/vals.size() : 0) / totalTd}
	println "$rate] $coverageRate"
	coverageMatrix << coverageRate;
	
}

File outF = new File("results/sensing.persistence.simsim.speedsense.sumo.setup.SUMOTrafficSpeedTTSetup_100/run_noderate03-05-2011-18-40-30/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/tt_hist_errorsm_coverage.gpd");
outF.write("#Error(s/m)\t1%\t2%\t3%\t5%\t7%\t10%\t20%\t30%\t40%\t50%\t60%\t70%\t80%\t90%\n");

coverageMatrix.transpose().each{ 
	println it.join(",");
	outF.append("${it.join('\t')}\n");
}