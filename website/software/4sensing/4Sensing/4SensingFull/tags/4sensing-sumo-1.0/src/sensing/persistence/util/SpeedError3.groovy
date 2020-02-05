package sensing.persistence.util

String fName = args[0]
println fName
File f = new File(fName)

def bins = [];

f.eachLine { String line ->	
		def (count, vCount, agrSpeed, vpSpeed, sumoSpeed, avgOccupancy, avgDensity, maxSpeed, numLanes, length, sampledSeconds, hasTrafficLight, contained) =
			line.split()[0..-3]*.toDouble()
		
		if(sumoSpeed >= 0) {
			int bin = (int)length/50.0
			if(bins[bin] == null) {
				bins[bin] = [];
			}
			bins[bin] << sumoSpeed;
		}
}

String outFName = (args[0].split("/")[0..-2] + "seg_length_speed.gpd").join ("/")
println outFName
File outF = new File(outFName);
outF.write("")

bins.eachWithIndex { c, idx ->
	if(c) {
		double mean = c.sum() / c.size();
		double var = (c.inject(0) {sum, val -> sum + (val - mean) * (val - mean)})/(c.size()-1)
		double std = Math.sqrt(var)
		int len = idx*50+50;
		def line = String.format("%d\t%.2f\t%.2f\t%.2f\t%.2f\t%d\n", len, c.max(), c.min(), mean, std, c.size())
		print line
		outF.append(line)
	}
}


