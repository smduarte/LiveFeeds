package apps.nodecount
;

import java.awt.geom.Rectangle2D;

import sensing.persistence.core.pipeline.Tuple;

class TNodeCount extends Tuple{
	int count;
	Rectangle2D boundingBox;
	String cellId;
	int level;
}
