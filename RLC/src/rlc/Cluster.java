package rlc;

import java.util.Iterator;
import java.util.Collection;

interface Cluster extends Iterable<PointRLC>
{


    int getSize( );


    int getMaxLevel( );


    Iterator<PointRLC> getInteriorIterator( );


    //
    // Results in position:
    //   0 - Number of distances computed.
    //
    // If canAdd(point, dists, results) is true, then dists is updated.
    //

    boolean canAdd( Point point, ArrayOfDouble dists, long[] results );


    //
    // Results in position:
    //   0 - Number of distances computed.
    //
    // Pre-condition: canAdd(point, dists, results) has been executed and 
    //                has returned true.
    //

    void add( Point point, ArrayOfDouble dists, long[] results );


    //
    // Returned value: 
    //   1 - point fits in the cluster interior;
    //   0 - point is the center;
    //  -1 - point is outside the cluster ball.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //
    // If fitsIn(point, dist, results) is true, then dist is updated.
    //

    int fitsIn( Point point, double[] dist, long[] results );


    //
    // Returned value:
    //   true, if the point has been removed;
    //   false, otherwise.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //
    // Pre-condition: fitsIn(point, dist, results) has been executed and 
    //                has returned 1.
    //

    boolean remove( Point point, double[] dist, long[] results );


    //
    // Returned value: 
    //   true, if the query ball is inside the cluster;
    //   false, otherwise.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    boolean rangeSearch( Point query, double searchRadius, 
                         ArrayOfDouble minDist, 
                         Collection<Point> answer, long[] results );


}

