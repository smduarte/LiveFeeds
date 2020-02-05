package sensing.persistence.util.sumo


class ResultReader {
	List fields
	BufferedReader reader
	Map nextL = null
	Map results
	double timeFrame = 0
	
	def ResultReader(File inF, List fields) {
		this.fields = fields
		reader = new BufferedReader(new FileReader(inF))
		readLine()
		
	}
	
	def readLine() {
		String line = null;
		nextL = null;
		line = reader.readLine()
		while(line?.startsWith('#')) {
			line = reader.readLine();
		}

		if(line) {
			nextL = [:]
			line.split().eachWithIndex{ val, idx -> nextL[fields[idx]] = val}
		}
	}
	
	def getNextTimeFrame() {
		if(!nextL) return null;
		return nextL.time.toDouble();
	}
	
	
	def readNextTimeFrame() {

		if(!nextL) return
		timeFrame = nextTimeFrame
		results = [:]
		while(nextTimeFrame == timeFrame) {
			results[nextL.segmentId]  = nextL
			readLine()
		}
	}
	
	boolean hasNext() {
		nextL != null
	}


}