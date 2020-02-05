package sensing.persistence.util

int maxSims = Integer.parseInt(args[0])


DataSets sets = new DataSets( 
	root: args[1],
	runId: args[2],
	metrics: [
		niAcquisition:	"NodeInfo_Acquisition",
		niAggregation:	"NodeInfo_Aggregation"
	]
)

sets.load()

sets.each{ String setup, String tree, HashMap dataSet ->
	String rootFileName = "${sets.root}/${sets.runId}_${tree.tokenize(".").last()}_${setup.tokenize(".").last()}"
	use(MatrixOpsCategory) {
		if(dataSet.data.niAcquisition) {
			List data = [(1..dataSet.data.niAcquisition[0].size()), 
						 *dataSet.data.niAcquisition.tail().truncate(maxSims).copy().sortLines{a,b -> b.compareTo(a)}
						].transpose()
			data.addAverageCol(1..-1)
			data.saveGpd(rootFileName + "_acqWorkload.gpd", dataSet.setup)
		}
		if(dataSet.data.niAggregation) {
			List data = [(1..dataSet.data.niAggregation[0].size()), 
						 *dataSet.data.niAggregation.tail().truncate(maxSims).copy().sortLines{a,b -> b.compareTo(a)}
						].transpose()
			data.addAverageCol(1..-1)
			data.saveGpd(rootFileName + "_agrWorkload.gpd", dataSet.setup)
		}
		if(dataSet.data.niAcquisition && dataSet.data.niAggregation) {
			List data = [(1..dataSet.data.niAcquisition[0].size()), 
						 *dataSet.data.niAcquisition.tail().sumLines(dataSet.data.niAggregation.tail()).truncate(maxSims).sortLines{a,b -> b.compareTo(a)}
						].transpose()
			data.addAverageCol(1..-1)
			data.saveGpd(rootFileName + "_totalWorkload.gpd", dataSet.setup)
		}
	}
}
