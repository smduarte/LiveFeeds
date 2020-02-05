package sensing.persistence.util

int maxSims = Integer.parseInt(args[0])


DataSets sets = new DataSets( 
	root: args[1],
	runId: args[2],
	metrics: [
		errorRate:	"ErrorRate",
		detections: "Detections",
		falseNegative: "FalseNegative",
		falsePositive: "FalsePositive"
	]
)

sets.load()

sets.each{ String setup, String tree, HashMap dataSet ->
	String rootFileName = "${sets.root}/${sets.runId}_${tree.tokenize(".").last()}_${setup.tokenize(".").last()}"
	use(MatrixOpsCategory) {
		if(dataSet.data.errorRate) {
			List data = dataSet.data.errorRate.truncate(maxSims+1).transpose()
			data.addAverageCol(1..-1)
			data.saveGpd(rootFileName + "_errorRate.gpd", dataSet.setup)
		}
		if(dataSet.data.detections) {
			List data = dataSet.data.detections.truncate(maxSims+1).transpose()
			data.addAverageCol(1..-1)
			data.saveGpd(rootFileName + "_detections.gpd", dataSet.setup)
		}
		if(dataSet.data.falseNegative) {
			List data = dataSet.data.falseNegative.truncate(maxSims+1).transpose()
			data.addAverageCol(1..-1)
			data.saveGpd(rootFileName + "_falseNegative.gpd", dataSet.setup)
		}
		if(dataSet.data.falsePositive) {
			List data = dataSet.data.falsePositive.truncate(maxSims+1).transpose()
			data.addAverageCol(1..-1)
			data.saveGpd(rootFileName + "_falsePositive.gpd", dataSet.setup)
		}
	}
}
