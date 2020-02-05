package sensing.persistence.util

String fName = args[0]
println fName

int STEP = 10;
int NBINS  = 51;

def error = [];

NBINS.times {int i -> error[i] = [n:0, nT:0d, total:0, max:0, min:1000]}

File f = new File(fName)
int maxCount = -1
int nRecords = 0;

f.eachLine { String line ->

	def (agrSpeed, count, contained, sumoSpeed, avgDensity, avgOccupancy, maxSpeed, numLanes, length, sampledSeconds, hasTrafficLight) =
		line.split()*.toDouble()
		
	if(contained) {
		nRecords++;
		error.eachWithIndex{e, i ->
			if(sampledSeconds >= i*STEP) {
				if(true) {
					e.n++
					absSpeedError = agrSpeed >= 0 ? Math.abs(agrSpeed-sumoSpeed) : sumoSpeed
					e.total += absSpeedError
					e.max = absSpeedError > e.max ? absSpeedError : e.max
					e.min = absSpeedError < e.min ? absSpeedError : e.min
				}
				e.nT++
			}
		}
	}
}

String outFName = (args[0].split("/")[0..-2] + "speed_error2_100.gpd").join ("/")
println outFName
File outF = new File(outFName);
outF.write("")

error.eachWithIndex{e, i ->
	def line = ([i*STEP, e.total/e.n, e.min, e.max, e.n, "${e.n/e.nT*100}%" , "${e.nT/nRecords*100}%"].join("\t"))
	println line
	outF <<  line +  "\n"
}

