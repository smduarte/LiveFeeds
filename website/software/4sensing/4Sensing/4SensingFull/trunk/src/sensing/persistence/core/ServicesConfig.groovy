package sensing.persistence.core;

//TODO: layer violation - rectangle implementation should be independent from simulator
import simsim.gui.geom.Rectangle;

import sensing.persistence.core.network.*;
import sensing.persistence.core.scheduler.*;
import sensing.persistence.core.logging.*;

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