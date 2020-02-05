package sensing.persistence.util.snapshot
import sensing.persistence.util.*;

int maxSims = Integer.parseInt(args[0])


DataSets sets = new DataSets( 
	root: args[1],
	runId: args[2],
	metrics: [
		latencyGA0:     "SNAPSHOT_LatencyGALevel_0_Histogram",
        latencyGA1:     "SNAPSHOT_LatencyGALevel_1_Histogram",
        latencyGA2:     "SNAPSHOT_LatencyGALevel_2_Histogram",
		latencyGA3:     "SNAPSHOT_LatencyGALevel_3_Histogram",
        latencyGA4:     "SNAPSHOT_LatencyGALevel_4_Histogram",
		latencyGA5:		"SNAPSHOT_LatencyGALevel_5_Histogram",
		latencyGA6:		"SNAPSHOT_LatencyGALevel_6_Histogram",
		latencyGA7:     "SNAPSHOT_LatencyGALevel_7_Histogram"
	]
)

sets.load()

sets.each{ String setup, String tree, HashMap dataSet ->
	String rootFileName = "${sets.root}/${sets.runId}_${tree.tokenize(".").last()}_${setup.tokenize(".").last()}"
	use(MatrixOpsCategory) {
		// Lat Levels
		def latLevelsBuilder = { String s ->
			List latLevels = []
			def levelsHeader = "Lat"
			(0..7).each{
				if(dataSet.data["latency${s}${it}"]) {
					if(latLevels.size() == 0) {latLevels << dataSet.data["latency${s}${it}"][0].tail()} // save header
					latLevels << dataSet.data["latency${s}${it}"][1].tail()
					levelsHeader <<= "\tLev $it"
				}
			}
			latLevels.transpose().saveGpd(rootFileName + "_latency${s}Levels.gpd", [*dataSet.setup, levelsHeader])
		}
		latLevelsBuilder("GA")
	}
}
