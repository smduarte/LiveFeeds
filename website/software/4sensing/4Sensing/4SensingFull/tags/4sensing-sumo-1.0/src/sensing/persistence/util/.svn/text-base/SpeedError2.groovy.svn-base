package sensing.persistence.util

String fName = args[0]
println fName
File f = new File(fName)

//s.count,
//s.vCount,
//s.avgSpeed * 3.6,
//edge.vProbeAvgSpeed * 3.6,
//edge.pAvgSpeed * 3.6,
//edge.avgOccupancy,
//edge.avgDensity,
//edge.maxSpeed,
//edge.numLanes,
//edge.length,
//edge.sampledSeconds,
//edge.hasTrafficLight ? 1 : 0,
//q.aoi.contains(edge.bbox) ? 1 : 0

def sumError = []
def sCount = []

double totalError = 0;
double minError = Double.POSITIVE_INFINITY;
double maxError = 0;
String maxErrorSegmentId;
int numSamples = 0;
int numSamplesLt5 = 0;
int numSamplesGt10 = 0;
int numSamplesGt20 = 0;
def gt20 = [:];
def gt10 = [:];

f.eachLine { String line ->	
		def fields = line.split();
		def (count, vCount, agrSpeed, vpMinSpeed, vpMaxSpeed, vpAvgSpeed, vpStdSpeed, vpSamples, sumoSpeed, avgOccupancy, avgDensity, sampledSeconds, maxSpeed, numLanes, length, hasTrafficLight, contained) =
			fields[0..-3]*.toDouble()
			
		def segmentId = fields[17];
		def time = fields[18].toDouble();
		
		int vCountI = (int)vCount
		if(time < 1800 && sumoSpeed >= 0 && vCount >=5  && length >=100) {
			double error = Math.abs(agrSpeed-sumoSpeed);
			if(sCount[vCountI]) {
				sCount[vCountI]++;
				sumError[vCountI] += error;
			} else {
				sCount[vCountI] = 1;
				sumError[vCountI] = error;
			}
			totalError += error;
			minError = Math.min(minError, error);
			if(error > maxError) {
				maxError = error;
				maxErrorSegmentId = segmentId;
			}
			numSamples++;
			if(error <= 5) {
				numSamplesLt5++;	
			} 

			if(error >= 10) {
				numSamplesGt10++;
				if(!gt10[segmentId] || gt10[segmentId] < error) {
					gt10[segmentId]	= error
				}
			}
			if(error >= 20) {
				numSamplesGt20++;
				if(!gt20[segmentId] || gt20[segmentId] < error) {
					gt20[segmentId]	= error
				}
			}
		}
}

printf("Valid: %d\nMean error: %.2f\nMin error: %.2f\nMax Error: %.2f (%s)\nLt5: %.2f%% (%d) Gt10: %.2f%% (%d) Gt20: %.2f%% (%d)\n", numSamples, totalError/numSamples, minError, maxError, maxErrorSegmentId, numSamplesLt5/numSamples*100, numSamplesLt5, numSamplesGt10/numSamples*100, numSamplesGt10, numSamplesGt20/numSamples*100, numSamplesGt20);
gt20 = gt20.sort { a, b -> -1*(a.value <=> b.value)}
gt20.each{String segmentId, double error -> 
	printf("%s\t%.2f\n", segmentId, error);	
}

//String outFName = (args[0].split("/")[0..-2] + "speed_error_weighwin_100.gpd").join ("/")
//println outFName
//File outF = new File(outFName);
//outF.write("")
//
//sCount.eachWithIndex { c, idx ->
//	if(c) {
//		println "$idx\t${sumError[idx]/c}\t$c"
//		outF.append("$idx\t${sumError[idx]/c}\t$c\n")
//	}
//}


