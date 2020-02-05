package sensing.persistence.util.snapshot
import sensing.persistence.util.*;

int maxSims = Integer.parseInt(args[0])


DataSets sets = new DataSets( 
	root: args[1],
	runId: args[2],
	metrics: [
		niAcquisition:	"SNAPSHOT_NodeInfo_Acquisition",
		niAggregation:	"SNAPSHOT_NodeInfo_Aggregation"
	]
)

sets.load()

sets.each{ String setup, String tree, HashMap dataSet ->
	String rootFileName = "${sets.root}/${sets.runId}_${tree.tokenize(".").last()}_${setup.tokenize(".").last()}"
	use(MatrixOpsCategory) {
		if(dataSet.data.niAcquisition) {
			//println "acquisition: ${dataSet.data.niAcquisition[1].tail()}"
			List data = [(1..dataSet.data.niAcquisition[0].size()-1), 
						 dataSet.data.niAcquisition[1].tail().clone().sort{a,b -> b.compareTo(a)}
						].transpose()
			data.saveGpd(rootFileName + "_acqWorkload.gpd", dataSet.setup)
		}
		if(dataSet.data.niAggregation) {
			//println "aggregation: ${dataSet.data.niAggregation[1].tail()}"
			List data = [(1..dataSet.data.niAggregation[0].size()-1), 
						 dataSet.data.niAggregation[1].tail().clone().sort{a,b -> b.compareTo(a)}
						].transpose()
			data.saveGpd(rootFileName + "_agrWorkload.gpd", dataSet.setup)
		}
		if(dataSet.data.niAcquisition && dataSet.data.niAggregation) {
			List data = [(1..dataSet.data.niAcquisition[0].size()-1), 
						 [dataSet.data.niAcquisition[1].tail()].sumLines([dataSet.data.niAggregation[1].tail()])[0].sort{a,b -> b.compareTo(a)}
						].transpose()
			data.saveGpd(rootFileName + "_totalWorkload.gpd", dataSet.setup)
		}
	}
}
