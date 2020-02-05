package sensing.persistence.util

int maxSims = Integer.parseInt(args[0])


DataSets sets = new DataSets( 
	root: args[1],
	runId: args[2],
	metrics: [
		latency:		"Latency_Histogram",
		latency0:		"Latency_0_Histogram",
		latency1:		"Latency_1_Histogram",
		latency2:       "Latency_2_Histogram",
		latency3:       "Latency_3_Histogram",
		latency4:       "Latency_4_Histogram",
		latency5:       "Latency_5_Histogram",
		latency6:       "Latency_6_Histogram",
		latency7:       "Latency_7_Histogram",
		latencyGA0:     "LatencyGALevel_0_Histogram",
        latencyGA1:     "LatencyGALevel_1_Histogram",
        latencyGA2:     "LatencyGALevel_2_Histogram",
		latencyGA3:     "LatencyGALevel_3_Histogram",
        latencyGA4:     "LatencyGALevel_4_Histogram",
		latencyGA5:		"LatencyGALevel_5_Histogram",
		latencyGA6:		"LatencyGALevel_6_Histogram",
		latencyGA7:     "LatencyGALevel_7_Histogram"
	]
)

sets.load()

sets.each{ String setup, String tree, HashMap dataSet ->
	String rootFileName = "${sets.root}/${sets.runId}_${tree.tokenize(".").last()}_${setup.tokenize(".").last()}"
	use(MatrixOpsCategory) {
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
			tData.addAverageCol(1..-1)
			tData.saveGpd(rootFileName + "_latency.gpd", dataSet.setup)
		}
		
		// Lat Levels
		def latLevelsBuilder = { String s ->
			List latLevels = []
			def levelsHeader = "Lat"
			(0..7).each{
				if(dataSet.data["latency${s}${it}"]) {
					if(latLevels.size() == 0) {latLevels << dataSet.data["latency${s}${it}"][0]} // save header
					List data = dataSet.data["latency${s}${it}"].truncate(maxSims+1).transpose()
					data.addAverageCol(1..-1)
					data = data.transpose()
					latLevels << data.last()
					levelsHeader <<= "\tLev $it"
				}
			}
			latLevels.transpose().saveGpd(rootFileName + "_latency${s}Levels.gpd", [*dataSet.setup, levelsHeader])
		}
		latLevelsBuilder("")
		latLevelsBuilder("GA")
	}
}
