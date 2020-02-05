package sensing.core;

//TODO: layer violation - rectangle implementation should be independent from simulator
import simsim.gui.geom.Rectangle;

import sensing.core.network.*;
import sensing.core.scheduler.*;
import sensing.core.logging.*;

public class ServicesConfig {
	public enum QueryImplPolicy {RND_TREE, QUAD_TREE, NEAREST_TREE, CENTRALIZED};
	public enum DistTreeType {SPT, RND};

	Peer 				peer;
	QueryImplPolicy 	queryImplPolicy;
	Rectangle 			world;
	NetworkProvider 	networkImpl;
	DistTreeType		distTreeType;
	int					distTreeBranchF;
	SchedulerProvider	schedulerImpl;
	LoggingProvider		loggingImpl;
	Random				rand
	String				vTableCodebase;
	boolean				completeMode = false;
	Map					monitoring;	
}
