package rlc;

import java.util.Collection;

interface ClusterInterior extends Iterable<PointRLC>
{

    int getSize( );


    int getMaxLevel( );


    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    void add( Point point, ArrayOfDouble dists, long[] results );


    //
    // Returned value:
    //   true, if the point has been removed;
    //   false, otherwise.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    boolean remove( Point point, double[] dist, long[] results );


    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    void rangeSearch( Point query, double searchRadius, 
                      ArrayOfDouble minDist, 
                      Collection<Point> answer, long[] results );


}

