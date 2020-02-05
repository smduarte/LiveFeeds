package sensing.persistence.core.pipeline;

import sensing.persistence.core.ServiceManager;
import sensing.persistence.core.network.Peer;
import sensing.persistence.core.query.QueryContext;

import static sensing.persistence.core.logging.LoggingProvider.*;

public abstract class Component {
	Component next;
	Pipeline pipeline;

//	public void setNext(Component c) {
//		this.next = c;
//		println "${this} next is ${c}";
//	}

	public void init() {};

	public void streamInit() {
		init();
		next?.streamInit();
	}
	
	public boolean canDispose() {
		return true;
	}
	
	public boolean streamCanDispose(){
		return canDispose() && (!next || next.streamCanDispose());
	}

	public void dispose() {}
	
	public void streamDispose() {
		dispose();
		next?.streamDispose();
	}
	
	public void reset() {};
	
	public void streamReset() {
		reset();
		next?.streamReset();
	}
	
	public void flush() {};
	
	public void streamFlush() {
		flush();
		next?.streamFlush();
	}
	

	/*
	 * input - default implementation simply forwards received value
	 */
	public void input(input) {
		//println "COMPONENT ${this.class.name} RECEIVED $input"
		forward(input);
	}
	
//	public abstract void input(tuple);

	protected final void forward(input) {
		//println "COMPONENT ${this.class.name} forwarding $input to ${next?.class?.name}}"
		next?.input(input);
	}

	/*
	 * Context
	 */
	public ServiceManager getServices() {
		return pipeline.context.services;
	}

	public QueryContext getQuerycontext() {
		return pipeline.context.querycontext;
	}

	public Peer getPeercontext() {
		return pipeline.context.peercontext;
	}
	
	/*
	 * Monitoring
	 */
	public int getStateCount() {
		if(next) {
			return next.getStateCount();
		} else {
			return 0;
		}
	}
	
	public getState() {
		if(next) {
			return next.getState();
		} else {
			return [];
		}
	}
	
	public getWindowContent() {
		if(next) {
			return next.getWindowContent();
		} else {
			return [];
		}
	}
	
}
