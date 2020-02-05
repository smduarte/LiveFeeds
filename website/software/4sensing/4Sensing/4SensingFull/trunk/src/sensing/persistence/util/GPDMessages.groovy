package sensing.persistence.util


int maxSims = Integer.parseInt(args[0])


DataSets sets = new DataSets( 
	root: args[1],
	runId: args[2],
	metrics: [
		messages: 		"Messages_Cumulative",
		niMsgSent:		"NodeInfo_MsgSent",
		niMsgReceived:	"NodeInfo_MsgReceived",
		niMsgAcquired:		"NodeInfo_MsgAcquired"
	]
)

sets.load()

sets.each{ String setup, String tree, HashMap dataSet ->
	String rootFileName = "${sets.root}/${sets.runId}_${tree.tokenize(".").last()}_${setup.tokenize(".").last()}"
	use(MatrixOpsCategory) {
		if(dataSet.data.messages) {
			List data = dataSet.data.messages.truncate(maxSims+1).transpose()
			data.addAverageCol(1..-1)
			data.saveGpd(rootFileName + "_messages.gpd", dataSet.setup)
		}
		if(dataSet.data.niMsgSent) {
			List msgSent  = dataSet.data.niMsgSent.truncate(maxSims+1)
			List msgRcvd  = dataSet.data.niMsgReceived.truncate(maxSims+1)
			//List acquired = dataSet.data.niAcquired.truncate(maxSims+1)
			
			// sum lines and sort descending (excluding header)
			List totalMsg = [(1..msgSent[0].size()), *msgSent.tail().sumLines(msgRcvd.tail()).sortLines{a,b -> b.compareTo(a)}]
			//List totalMsgAcquired = [totalMsg[0], *totalMsg.tail().sumLines(acquired.tail()).sortLines{a,b -> b.compareTo(a)}]
			
			totalMsg = totalMsg.transpose()
			totalMsg.addAverageCol(1..-1)
			//totalMsgAcquired = totalMsgAcquired.transpose()
			//totalMsgAcquired.addAverageCol(1..-1)
			
			totalMsg.saveGpd(rootFileName + "_msgDist.gpd", dataSet.setup)
			//totalMsgAcquired.saveGpd(rootFileName + "_msgAcquiredDist.gpd", dataSet.setup)
			
			msgSent = [(1..msgSent[0].size()), *msgSent.tail().copy().sortLines{a,b -> b.compareTo(a)}].transpose()
			msgSent.addAverageCol(1..-1)
			msgRcvd = [(1..msgRcvd[0].size()), *msgRcvd.tail().copy().sortLines{a,b -> b.compareTo(a)}].transpose()
			msgRcvd.addAverageCol(1..-1)
			
			msgSent.saveGpd(rootFileName + "_msgSentDist.gpd", dataSet.setup)
			msgRcvd.saveGpd(rootFileName + "_msgRcvdDist.gpd", dataSet.setup)
		}
	}
}
