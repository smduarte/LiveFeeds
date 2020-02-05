package sensing.persistence.util

DataSets sets = new DataSets( 
	root: args[0],
	runId: args[1],
	metrics: [
		clear: "ClearHistory",
		congested: "CongestedHistory"
	]
)

sets.load()

sets.each{ String setup, String tree, HashMap dataSet ->
	String rootFileName = "${sets.root}/${sets.runId}_${tree.tokenize(".").last()}_${setup.tokenize(".").last()}"
	use(MatrixOpsCategory) {
		if(dataSet.data.clear) {
			List data = [dataSet.data.clear[1]].transpose()
			data.saveGpd(rootFileName + "_clearHist.gpd", dataSet.setup)
		}
		if(dataSet.data.congested) {
			List data = [dataSet.data.congested[1]].transpose()
			data.saveGpd(rootFileName + "_congestedHist.gpd", dataSet.setup)
		}
	}
}
