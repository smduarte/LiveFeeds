package rlc;

import java.util.Collection;

public interface MetricDS {

	//
	// Adds the specified point to the metric data structure.
	//
	// Results in position:
	// 0 - Number of distances computed.
	//

	void add(Point point, long[] results);

	//
	// Adds every point in the specified array
	// to the metric data structure.
	//
	// Results in position:
	// 0 - Number of distances computed.
	//

	void add(Point[] point, long[] results);

	//
	// If the specified point is in the metric data structure,
	// removes it and returns true; otherwise, returns false.
	//
	// Results in position:
	// 0 - Number of distances computed.
	//

	boolean remove(Point point, long[] results);

	//
	// Returns every point in the metric data structure
	// whose distance to the specified query does not exceed
	// the specified search radius.
	//
	// Results in position:
	// 0 - Number of distances computed.
	//

	Collection<Point> rangeSearch(Point query, double searchRadius, long[] results);

}