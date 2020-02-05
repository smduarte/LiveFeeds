package sensing.persistence.util.snapshot
import sensing.persistence.util.*;


int maxSims = Integer.parseInt(args[0])


DataSets sets = new DataSets( 
	root: args[1],
	runId: args[2],
	metrics: [
		messages: 		"SNAPSHOT_Messages_Cumulative",
		niMsgSent:		"SNAPSHOT_NodeInfo_MsgSent",
		niMsgReceived:	"SNAPSHOT_NodeInfo_MsgReceived",
		niMsgAcquired:	"SNAPSHOT_NodeInfo_MsgAcquired"
	]
)

sets.load()

sets.each{ String setup, String tree, HashMap dataSet ->
	String rootFileName = "${sets.root}/${sets.runId}_${tree.tokenize(".").last()}_${setup.tokenize(".").last()}"
	use(MatrixOpsCategory) {
		if(dataSet.data.messages) {
			List data = [dataSet.data.messages[0].tail(), dataSet.data.messages[1].tail()].transpose()
			data.saveGpd(rootFileName + "_messages.gpd", dataSet.setup)
		}
		if(dataSet.data.niMsgSent) {
			List header = dataSet.data.niMsgSent[0].tail()
			List msgSent  = dataSet.data.niMsgSent[1].tail()
			List msgRcvd  = dataSet.data.niMsgReceived[1].tail()

			List totalMsg = [(1..header.size()), [msgSent].sumLines([msgRcvd])[0].sort{a,b -> b.compareTo(a)}]
			totalMsg.transpose().saveGpd(rootFileName + "_msgDist.gpd", dataSet.setup)

			msgSent = [(1..header.size()), msgSent.clone().sort{a,b -> b.compareTo(a)}]
			msgSent.transpose().saveGpd(rootFileName + "_msgSentDist.gpd", dataSet.setup)
			
			msgRcvd = [(1..header.size()), msgRcvd.clone().sort{a,b -> b.compareTo(a)}].transpose()			
			msgRcvd.transpose().saveGpd(rootFileName + "_msgRcvdDist.gpd", dataSet.setup)
		}
	}
}
