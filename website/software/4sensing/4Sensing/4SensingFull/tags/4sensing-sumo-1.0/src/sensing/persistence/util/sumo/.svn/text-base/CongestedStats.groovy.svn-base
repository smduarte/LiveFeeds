package sensing.persistence.util.sumo


List fields = ["count", "realcount", "mintt", "maxtt", "avgtt", "stdtt", "minexptt", "vpminspeed", "vpmaxspeed", "vpavgspeed", "vpstdspeed", "vpcount",
	"edgespeed", "edgeoccup", "edgedensity", "edgecount", "edgemaxspeed", "edgelanes", "edgelen", "edgetl", "edgecontained",
	"segmentId", "time", "tterror", "count100", "realcount100", "stdtt100"]

File f = new File("results/sensing.persistence.simsim.speedsense.sumo.setup.SUMOTrafficSpeedTTSetup_100/run_noderate03-05-2011-18-40-30/sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim/rateresults/resultserr_100.gpd");

int numCongested = 0;
int numNormal =  0;
double tOCongested = 0;
double tONormal = 0;

f.eachLine { String line ->	
	
	Map vals = [:]
	line.split().eachWithIndex{ val, idx -> vals[fields[idx]] = val}
	
	int c = vals.count.toInteger();
	int rc = vals.realcount.toInteger();
	double o = vals.edgeoccup.toDouble();
	double len = vals.edgelen.toDouble();
	double avgtt = vals.avgtt.toDouble()
	
	if(!avgtt.isInfinite() && len >= 50) {
		if(rc*1.0/c < 0.9) {
			numCongested++;
			tOCongested += o;
		} else {
			numNormal++;
			tONormal += o;
		}		
	}
}

printf("Congested: %.2f%% (%d)\n", tOCongested/numCongested, numCongested);
printf("Normal: %.2f%% (%d)\n", tONormal/numNormal, numNormal);
