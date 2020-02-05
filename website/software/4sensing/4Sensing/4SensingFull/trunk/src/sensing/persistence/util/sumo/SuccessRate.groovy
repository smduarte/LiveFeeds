package sensing.persistence.util.sumo

List fields = ["count", "realcount", "mintt", "maxtt", "avgtt", "stdtt", "minexptt", "vpminspeed", "vpmaxspeed", "vpavgspeed", "vpstdspeed", "vpcount",
	"edgespeed", "edgeoccup", "edgedensity", "edgecount", "edgemaxspeed", "edgelanes", "edgelen", "edgetl", "edgecontained",
	"segmentId", "time", "tterror", "count100", "realcount100", "stdtt100"]

def run = {int rate ->
	File results = new File("results/sensing.persistence.simsim.speedsense.sumo.setup.SUMOTrafficSpeedTTSetup_100/run_noderate03-05-2011-18-40-30/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/rateresults/resultserr_${rate}.gpd");
	File baselineResults = new File("results/sensing.persistence.simsim.speedsense.sumo.setup.SUMOTrafficSpeedTTSetup_100/run_noderate03-05-2011-18-40-30/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/rateresults/resultserr_100.gpd");
	
	ResultReader reader = new ResultReader(results, fields);
	ResultReader baselineReader = new ResultReader(baselineResults, fields);
	
	
	int numResults = 0;
	int numHit = 0;
	int numFP = 0;
	int numFN = 0;
	int numMiss = 0;
	
	while(reader.hasNext()) {
		baselineReader.readNextTimeFrame();
		reader.readNextTimeFrame();

		baselineReader.results.each { String segmentId, Map valsBl ->

			int cBl = valsBl.count.toInteger();
			int rcBl = valsBl.realcount.toInteger();
			boolean congestedBl =  rcBl*1.0/cBl < 0.9;
			if(congestedBl) {
				numResults++
				vals = reader.results[valsBl.segmentId];
				if(vals == null) {
					numFN++;
				} else {
					int c = vals.count.toInteger();
					int rc = vals.realcount.toInteger();
					boolean congested = rc*1.0/c < 0.9;
					if(congested) {
						numHit++;
					} else {
						numFN++;
					}
				}			
			}
		}
	}
	return [hit: numHit*1.0/numResults*100,  fp: numFP*1.0/numResults*100, fn: numFN*1.0/numResults*100, count: numResults];
}

[1,2,3,5,7, 10,20,30,40,50,60,70,80,90,100].each{ println "$it] ${run(it)}"}
