package rlc;

import java.util.Iterator;
import java.util.Collection;

class ClusterClass implements Cluster
{

    private static final int MAX_BUCKET_CAPACITY = 16;

    private static final int INITIAL_RLC_CAPACITY = 25;


    private PointRLC center;

    private int level;

    private double level0Radius;

    private double radius;

    private ClusterInterior interior;


    public ClusterClass( PointRLC theCenter, double theRadius, int theLevel )
    {
        center = theCenter;
        level = theLevel;
        level0Radius = theRadius;
        radius = level0Radius / ( level + 1 );
        interior = new Bucket(level);
    }


    public int getSize( )
    {
        return 1 + interior.getSize();
    }


    public int getMaxLevel( )
    {
        return interior.getMaxLevel();
    }


    public Iterator<PointRLC> getInteriorIterator( )
    {
        return interior.iterator();
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // ADD 
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


    //
    // Results in position:
    //   0 - Number of distances computed.
    //
    // If canAdd(point, dists, results) is true, then dists is updated.
    //

    public boolean canAdd( Point point, ArrayOfDouble dists, long[] results )
    {
        results[0]++;
        double distToCenter = center.distance(point);
        if ( distToCenter <= radius )
        {
            dists.add(distToCenter);
            return true;
        }
        else
            return false;
    }


    //
    // Results in position:
    //   0 - Number of distances computed.
    //
    // Pre-condition: canAdd(point, dists, results) has been executed and 
    //                has returned true.
    //

    public void add( Point point, ArrayOfDouble dists, long[] results )
    {
        if ( interior.getSize() == MAX_BUCKET_CAPACITY )
            this.moveToRLC(results);
        interior.add(point, dists, results);
    }


    private void moveToRLC( long[] results )
    {
        ClusterInterior newInterior = 
            new RLC(level0Radius, INITIAL_RLC_CAPACITY, level + 1);
        // To avoid memory fragmentation.
        ArrayOfDouble dists = new ArrayOfDouble(level + 2);
        for ( PointRLC point : interior )
        {
            dists.initialise( point.getDistances() );
            newInterior.add(point.getPoint(), dists, results);
        }
        interior = newInterior;
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // REMOVE
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


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

    public int fitsIn( Point point, double[] dist, long[] results )
    {
        results[0]++;
        double distToCenter = center.distance(point);
        if ( distToCenter == 0 )
            // Point is the center.
            return 0;
        else if ( distToCenter <= radius )
        {
            // Point fits in the cluster interior.
            dist[0] = distToCenter;
            return 1;
        }
        else
            // Point is outside the cluster ball.
            return -1;
    }


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

    public boolean remove( Point point, double[] dist, long[] results )
    {
        if ( interior.remove(point, dist, results) )
        {
            if ( interior.getSize() == MAX_BUCKET_CAPACITY )
                this.moveToBucket(results);
            return true;
        }
        else
            return false;
    }


    private void moveToBucket( long[] results )
    {
        ClusterInterior newInterior = new Bucket(MAX_BUCKET_CAPACITY, level);
        // To avoid memory fragmentation.
        ArrayOfDouble dists = new ArrayOfDouble(level + 1);
        for ( PointRLC point : interior )
        {
            dists.initialise(point.getDistances(), level + 1); 
            newInterior.add(point.getPoint(), dists, results);
        }
        interior = newInterior;
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // RANGE SEARCH
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


    //
    // Returned value: 
    //   true, if the query ball is inside the cluster;
    //   false, otherwise.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    public boolean rangeSearch( Point query, double searchRadius, 
                                ArrayOfDouble minDist, 
                                Collection<Point> answer, long[] results )
    {
        results[0]++;
        double distToCenter = center.distance(query);

        if ( distToCenter <= searchRadius )
        {
            // Center is in query ball.
            answer.add(center.getPoint());

            if ( distToCenter + searchRadius <= radius )
            {
                // Query ball is inside the cluster.
                minDist.set(level, distToCenter - searchRadius);
                interior.rangeSearch(query, searchRadius, minDist, 
                                     answer, results);
                return true;
            }
            else if ( distToCenter + radius <= searchRadius )
            {
                // Query ball contains the cluster.
                for ( PointRLC point : interior )
                    answer.add( point.getPoint() );
                return false;
            }
            else
            {
                // Query ball intersects the cluster.
                minDist.set(level, distToCenter - searchRadius);
                interior.rangeSearch(query, searchRadius, minDist, 
                                     answer, results);
                return false;
            }
        }
        else
            // Center is not in query ball.

            if ( distToCenter + searchRadius <= radius )
            {
                // Query ball is inside the cluster.
                minDist.set(level, distToCenter - searchRadius);
                interior.rangeSearch(query, searchRadius, minDist, 
                                     answer, results);
                return true;
            }
            else if ( distToCenter > searchRadius + radius )
                // Query ball is outside the cluster.
                return false;
            else
            {
                // Query ball intersects the cluster.
                minDist.set(level, distToCenter - searchRadius);
                interior.rangeSearch(query, searchRadius, minDist, 
                                     answer, results);
                return false;
            }
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // ITERATOR
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


    public Iterator<PointRLC> iterator( )
    {
        return new ClusterIterator(center, interior);
    }


}

