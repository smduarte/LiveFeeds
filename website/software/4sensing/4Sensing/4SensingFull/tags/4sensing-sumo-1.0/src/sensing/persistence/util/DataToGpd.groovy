package sensing.persistence.util

int maxSims = Integer.parseInt(args[0])

DataSets sets = new DataSets( 
	root: args[1],
	runId: args[2],
	metrics: [
//		messages: 		"Messages_Cumulative",
//		totalWorkload:	"Workload_Input_All_Nodes",
//		sensorWorkload:   "Workload_Sensor_All_Nodes",
//		agrWorkload:	"Workload_Aggregation_All_Nodes",
//		latency:		"Latency_Histogram",
//		latency0:		"Latency_0_Histogram",
//		latency1:		"Latency_1_Histogram",
//		latency2:               "Latency_2_Histogram",
//		latency3:               "Latency_3_Histogram",
//		latency4:               "Latency_4_Histogram",
//		latency5:               "Latency_5_Histogram",
//		latency6:               "Latency_6_Histogram",
//		latency7:               "Latency_7_Histogram",
//		latencyGA0:             "LatencyGALevel_0_Histogram",
//        latencyGA1:             "LatencyGALevel_1_Histogram",
//        latencyGA2:             "LatencyGALevel_2_Histogram",
//		latencyGA3:             "LatencyGALevel_3_Histogram",
//        latencyGA4:             "LatencyGALevel_4_Histogram",
//		latencyGA5:		"LatencyGALevel_5_Histogram",
//		latencyGA6:		"LatencyGALevel_6_Histogram",
//		latencyGA7:             "LatencyGALevel_7_Histogram"
//		niMsgSent:		"NodeInfo_MsgSent",
//		niMsgReceived:	"NodeInfo_MsgReceived",
		niAcquired:		"NodeInfo_Acquired",
		niAggregation:	"NodeInfo_Aggregation"
	]
)


def saveGpd(String fileName, List header, List data) {
	File out = new File(fileName)
	out.write("") // clear contents
	header.each{out.append("# ${it}\n")} // output setup header
	data.size().times{ int line -> 
		data[0].size().times{int col -> out.append("${data[line][col]}\t")}
		out.append("\n")
	}
}

//def truncate(List data, int maxLines) { // returns list truncated to maxLines (excluding header)
//	return data[0..Math.min(data.size()-1, maxLines)]
//}

// add average column (excluding first column - x values)
def addAverageCol(List matrix) {
	matrix.each{ List line -> line << (line.tail().sum()*1.0/(line.size()-1))} 
}

sets.load()

sets.each{ String setup, String tree, HashMap dataSet ->
	String rootFileName = "${sets.root}/${sets.runId}_${tree.tokenize(".").last()}_${setup.tokenize(".").last()}"
	use(MatrixOpsCategory) {
		if(dataSet.data.totalWorkload) {
			println "Total Workload"
			// truncate to maxSims, transpose and remove last line
			List data = dataSet.data.totalWorkload.truncate(maxSims+1).transpose()
			data.pop() // remove last line - TOTAL_NODES
			addAverageCol(data)
			saveGpd(rootFileName + "_totalWorkload.gpd", dataSet.setup, data) 
		}
		if(dataSet.data.sensorWorkload) {
			println "Sensor Workload"
			// truncate to maxSims, transpose and remove last line
			List data = dataSet.data.sensorWorkload.truncate(maxSims+1).transpose()
			data.pop() // remove last line - TOTAL_NODES
			addAverageCol(data)
			saveGpd(rootFileName + "_sensorWorkload.gpd", dataSet.setup, data)
		}
		if(dataSet.data.agrWorkload) {
			println "Aggregation Workload"
			// truncate to maxSims, transpose and remove last line
			List data = dataSet.data.agrWorkload.truncate(maxSims+1).transpose()
			data.pop() // remove last line - TOTAL_NODES
			addAverageCol(data)
			saveGpd(rootFileName + "_agrWorkload.gpd", dataSet.setup, data)
		} else if(dataSet.data.niAggregation) {
			List data = [(1..dataSet.data.niAggregation[0].size()), *dataSet.data.niAggregation.tail().sortLines{a,b -> b.compareTo(a)}].transpose()
			addAverageCol(data)
			saveGpd(rootFileName + "_agrWorkload.gpd", dataSet.setup, data)
		}
//		if(dataSet.data.messages) {
//			List data = dataSet.data.messages.truncate(maxSims+1).transpose()
//			addAverageCol(data)
//			saveGpd(rootFileName + "_messages.gpd", dataSet.setup, data) 
//		}
		if(dataSet.data.latency) {
			List data = dataSet.data.latency.truncate(maxSims+1)
			// tData - acumulated histogram
			List tData = [data[0]]
			data.tail().each{List line ->
				List tLine = []
				double totalDetected = line.sum()
				line.eachWithIndex{ double val, int idx -> 
					tLine << (totalDetected ? line[0..idx].sum()/totalDetected : 0)
				}
				tData << tLine
			}
			tData = tData.transpose()
			addAverageCol(tData)
			saveGpd(rootFileName + "_latency.gpd", dataSet.setup, tData)
		}
		// Lat Levels
		def latLevelsBuilder = { String s ->
			List latLevels = []
			def levelsHeader = "Lat"
			(0..7).each{
				if(dataSet.data["latency${s}${it}"]) {
					if(latLevels.size() == 0) {latLevels << dataSet.data["latency${s}${it}"][0]} // save header
					List data = dataSet.data["latency${s}${it}"].truncate(maxSims+1).transpose()
					addAverageCol(data)
					data = data.transpose()
					latLevels << data.last()
					levelsHeader <<= "\tLev $it"
				}
			}
			saveGpd(rootFileName + "_latency${s}Levels.gpd", [*dataSet.setup, levelsHeader], latLevels.transpose())
		}
		latLevelsBuilder("")
		latLevelsBuilder("GA")


		if(dataSet.data.niMsgSent) {
			List msgSent = dataSet.data.niMsgSent.truncate(maxSims+1)
			List msgRcvd = dataSet.data.niMsgReceived.truncate(maxSims+1)
			List acquired = dataSet.data.niAcquired.truncate(maxSims+1)
			
			// sum lines and sort descending (excluding header)
			List totalMsg = [(1..msgSent[0].size()), *msgSent.tail().sumLines(msgRcvd.tail()).sortLines{a,b -> b.compareTo(a)}]
			List totalMsgAcquired = [totalMsg[0], *totalMsg.tail().sumLines(acquired.tail()).sortLines{a,b -> b.compareTo(a)}]
			
			totalMsg = totalMsg.transpose()
			addAverageCol(totalMsg)
			totalMsgAcquired = totalMsgAcquired.transpose()
			addAverageCol(totalMsgAcquired)
			
			saveGpd(rootFileName + "_msgDist.gpd", dataSet.setup, totalMsg)
			saveGpd(rootFileName + "_msgAcquiredDist.gpd", dataSet.setup, totalMsgAcquired)
			
			msgSent = [(1..msgSent[0].size()), *msgSent.tail().sortLines{a,b -> b.compareTo(a)}].transpose()
			addAverageCol(msgSent)
			msgRcvd = [(1..msgRcvd[0].size()), *msgRcvd.tail().sortLines{a,b -> b.compareTo(a)}].transpose()
			addAverageCol(msgRcvd)
			
			saveGpd(rootFileName + "_msgSentDist.gpd", dataSet.setup, msgSent)
			saveGpd(rootFileName + "_msgRcvdDist.gpd", dataSet.setup, msgRcvd)
		}
	}
}
