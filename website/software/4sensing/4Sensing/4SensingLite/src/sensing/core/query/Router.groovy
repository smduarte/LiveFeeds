package sensing.core.query;

import sensing.core.pipeline.Processor;
import sensing.core.pipeline.Pipeline;
import sensing.core.pipeline.EOS;
import sensing.core.pipeline.Tuple;
import sensing.core.network.*;
import static sensing.core.logging.LoggingProvider.*;

class Router extends Processor {
	QueryContext qc;
	boolean immediate;
	def queryDataBuffer = [:];
	def queryResultBuffer = [:];
	boolean running = true;
	
	public Router(QueryContext qc) {
		this.qc = qc;
		this.immediate = (qc.pipeline.mode == Pipeline.Mode.STREAM);
	}
	
	public void sendData(Tuple t, String context, Peer dest) {
		if(!running) return;
		if(immediate) {
			sendQD(dest, context, [t])
		} else {
			if(!queryDataBuffer[dest]) queryDataBuffer[dest] = [:];
			if(!queryDataBuffer[dest][context]) queryDataBuffer[dest][context] = []
			queryDataBuffer[dest][context] << t;
		}
	}
	
	public void sendResult(Tuple t, Peer dest) {
		if(!running) return;
		if(immediate) {
			sendQR(dest, [t]);
		} else {
			if(!queryResultBuffer[dest]) queryResultBuffer[dest] = [];
			queryResultBuffer[dest] << t;
		}
	}
	
	public void process(EOS eos) {
		if(!running) return;
		queryDataBuffer.each{ peer, contexts ->
			contexts.each{ ctx, data -> sendQD(peer, ctx, data)}
		}
		queryResultBuffer.each{ peer, data -> sendQR(peer, data)}
		queryDataBuffer = [:];
		queryResultBuffer = [:];
	}
	
	public void dispose() {
		running = false;
	}
	
	protected sendQD(Peer peer, String ctx, data) {
		QueryData qd = new QueryData(queryId: qc.query.id, query: qc.query, context: ctx, data: data);
		services.network.send(peer, qd);
//		services.logging.log(DEBUG, this, "sendQD", "${qc.context} sending ${data.size()} tuples");
//		data.each{ 
//			services.logging.log(DEBUG, this, "sendQD", " ${qc.context} sending ${it}");
//		}
	}
	
	protected sendQR(Peer peer, data) {
		QueryResult qr = new QueryResult(queryId: qc.query.id, data: data);
		services.network.send(peer, qr);
		services.logging.log(DEBUG, this, "sendQR", "${qc.context}  sending ${data.size()} tuples");
		data.each{ 
			services.logging.log(DEBUG, this, "sendQR", "${qc.context} sending ${it}");
		}
	}

}
