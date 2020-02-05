package livefeeds.size_vs_age;

import static livefeeds.rtrees.config.Config.Config;

import simsim.core.AbstractNode;
import simsim.core.Task;

public class Node extends AbstractNode {

	public long key;
	public int index;
	
	public double sessionBegin = 0, sessionEnd = 0;

	protected int __storage_index ;
	
	public Node() {
		super();

		GlobalDB.store(this);
				
	}

	public double upTime() {
		return Math.min(Config.MAX_SESSION_DURATION, currentTime() - sessionBegin);
	}

	public String toString() {
		return String.format("%d", key);
	}

	public void shutdown() {
		super.dispose();

		sessionEnd = currentTime() ;
		GlobalDB.dispose(this);
	}

	// --------------------------------------------------------------------------------------------------------------------------------
	public void init() {

		sessionBegin = currentTime();
		double sessionDuration = Config.churn.sessionDuration() ;
		new Task(this, sessionDuration) {
			public void run() {
				shutdown();
			}
		};
	}
}
