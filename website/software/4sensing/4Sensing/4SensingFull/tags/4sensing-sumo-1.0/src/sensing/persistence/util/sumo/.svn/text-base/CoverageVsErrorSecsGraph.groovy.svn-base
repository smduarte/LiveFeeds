package sensing.persistence.util.sumo;

List fields = ["count", "realcount", "mintt", "maxtt", "avgtt", "stdtt", "minexptt", "vpminspeed", "vpmaxspeed", "vpavgspeed", "vpstdspeed", "vpcount",
	"edgespeed", "edgeoccup", "edgedensity", "edgecount", "edgemaxspeed", "edgelanes", "edgelen", "edgetl", "edgecontained",
	"segmentId", "time", "tterror", , "count100", "realcount100", "stdtt100"];

Map segmentRating = [:];

File sr = new File("segmentRating_1800.csv");

sr.eachLine{ String line -> 
	def (segmentId, traveledDistance) = line.split(",");
	segmentRating[segmentId] = traveledDistance.toDouble();
}

double totalTd = segmentRating.inject(0){sum, entry -> sum += entry.value}
println "totalTD: ${totalTd/1000} Km"

File outF = new File("results/sensing.persistence.simsim.speedsense.sumo.setup.SUMOTrafficSpeedTTSetup_100/run_noderate03-05-2011-18-40-30/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/tt_coverage.gpd");
outF.write("#Node rate\tLTE5\tLTE60\tTOTAL\n");

(10..100).step(10).each { rate -> 
	File results = new File("results/sensing.persistence.simsim.speedsense.sumo.setup.SUMOTrafficSpeedTTSetup_$rate/run_noderate03-05-2011-18-40-30/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/results_err.gpd");
	
	double ts = 0;
	List traveledDs = [];
	
	ResultReader reader = new ResultReader(results, fields);
	while(reader.hasNext()) {
		reader.readNextTimeFrame();
		Map timeFrameTd = [5:0,60:0,T:0];
		reader.results.each{String segmentId, Map vals ->
			switch(vals.tterror.toDouble()) {
				case {it <= 5}:
					timeFrameTd[5] += segmentRating[segmentId] ?: 0;
				case {it <= 60}:
					timeFrameTd[60] += segmentRating[segmentId] ?: 0;
				default:
					timeFrameTd.T += segmentRating[segmentId] ?: 0;
			}
		}
		traveledDs << timeFrameTd	
	}
	
	List avgs = [5,60,"T"].collect { key ->
		List taveledDs4Key = traveledDs.collect{it[key]};
		double avg = taveledDs4Key.sum()/taveledDs4Key.size();
		return avg/totalTd;
	}
	String line = String.format("%d\t%f\t%f\t%f\n", rate, *avgs);
	print line;
	outF.append line;
}