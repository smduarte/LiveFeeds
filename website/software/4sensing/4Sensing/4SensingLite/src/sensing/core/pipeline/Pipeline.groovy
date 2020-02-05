package sensing.core.pipeline;

import static sensing.core.pipeline.Constants.*
import groovy.lang.Binding
import groovy.lang.Closure
import sensing.core.*
import sensing.core.logging.LoggingProvider.*
import sensing.core.network.*
import sensing.core.query.*

public class Pipeline {
	enum Mode {SET, STREAM};
	Mode mode = Mode.STREAM;
	Binding context;
	def invariant = null; // list of attributes that are constant in this pipeline - used for group aggregates
	protected Component head;
	protected Component tail;
	Pipeline parent = null;
	protected int processCount = 0;
	protected int processedTuples = 0;
	boolean prioritary = false; // used to give priority to pipeline tasks

	public Pipeline addComponent(Component c) {
		if(tail) {
			tail.next = c;
		} else {
			head = c;
		}
		tail = c;
		c.pipeline = this;
		return this;		
	}
	
	/*
	 * Context
	 */	
	public ServiceManager getServices() {
		return context.services;
	}

	public QueryContext getQuerycontext() {
		return context.querycontext;
	}

	public Peer getPeercontext() {
		return context.peercontext;
	}
	

	public void input(tuple) {
//smd		println "PIPELINE GOT $tuple: forward to $head"
		processedTuples++;
		head?.input(tuple);
	}


	public void init() {
		head?.streamInit();
	}

	public void dispose() {
		head?.streamDispose();
		head = null;
		services.scheduler.unschedule(querycontext.query.id, this)
	}
	
	public boolean canDispose() {
		return head?.streamCanDispose();
	}
	
	public void reset() {
		head?.streamReset();	
	}
	
	public void flush() {
		head?.streamFlush();
	}
	
	public void schedule (double period, Closure clos) {
		services.scheduler.schedule(period, querycontext.query.id, this, clos, prioritary)
	}
	
	public void scheduleOnce(double due, Closure clos) {
		services.scheduler.schedule(due, clos)
	}
	
	/*
	 * Monitoring
	 */
	
	
	public getState() {
		if(head) {
			return head.getState();
		} else {
			return [];
		}
	}
	
	public getWindowContent() {
		if(head) {
			return head.getWindowContent();
		} else {
			return [];
		}
	}
	
	public void incrProcessCount() {
		processCount++;
		if(parent) {
			parent.incrProcessCount();
		}
	}
	
	public int getProcessCount() {
		return processCount;
	}
	
	public int getProcessedTuples() {
		return processedTuples;
	}

}
