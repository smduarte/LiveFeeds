package sensing.persistence.util.sumo;

Map segmentRating = [:];
Map segmentRatingSum = [:];

File totalsr = new File("segmentRating_1800.tsv");

totalsr.eachLine{ String line ->
	def (segmentId, traveledDistance) = line.split();
	segmentRating[segmentId] = traveledDistance.toDouble();
}

List srFields = ["time", "segmentId", "tdist"]
ResultReader sr = new ResultReader(new File("segmentRating_1800_ts.tsv"), srFields)
while(sr.hasNext()) {
	println "Timeframe $sr.timeFrame"
	sr.readNextTimeFrame();
	sr.results.each{String segmentId, vals -> 
		segmentRatingSum[segmentId] = (segmentRatingSum[segmentId] ?: []) + vals.tdist.toDouble();
	}
}
assert segmentRating.size() == segmentRatingSum.size()
segmentRating.each{String segmentId, double m -> 
	println " $m > ${segmentRatingSum[segmentId].sum()}"
//	if(!(int)(segmentRatingSum[segmentId].sum()+0.5) == (int)(m+0.5)) {
//		
//	}
}