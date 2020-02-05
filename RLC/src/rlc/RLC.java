package rlc;

import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;

public class RLC implements MetricDS, ClusterInterior
{

    public static final int DEFAULT_CAPACITY = 3200;

    public static final int DEFAULT_MAX_HIGHT = 2000;


    private Cluster[] clusters;

    private int arraySize;

    private double radius;

    private int level;

    private int size;


    public RLC( double theRadius )
    {
        this(theRadius, DEFAULT_CAPACITY, 0);
    }


    protected RLC( double theRadius, int capacity, int theLevel )
    {
        clusters = new Cluster[capacity];
        arraySize = 0;
        radius = theRadius;
        level = theLevel;
        size = 0;
    }


    public int getSize( )
    {
        return size;
    }


    public int getListSize( )
    {
        return arraySize;
    }


    public int getMaxLevel( )
    {
        int maxLevel = level;
        for ( int i = 0; i < arraySize; i++ )
        {
            int clusterLevel = clusters[i].getMaxLevel();
            if ( clusterLevel > maxLevel )
                maxLevel = clusterLevel;
        }
        return maxLevel;
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // ADD
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


    //
    // From interface MetricDS.
    //
    // Adds the specified point to the metric data structure.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    public void add( Point point, long[] results )
    {
        ArrayOfDouble dists = new ArrayOfDouble(DEFAULT_MAX_HIGHT);
        results[0] = 0;
        this.add(point, dists, 0, results);
    }


    //
    // From interface MetricDS.
    //
    // Adds every point in the specified array 
    // to the metric data structure.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    public void add( Point[] points, long[] results )
    {
        // To avoid memory fragmentation.
        ArrayOfDouble dists = new ArrayOfDouble(DEFAULT_MAX_HIGHT);
        results[0] = 0;
        for ( int i = 0; i < points.length; i++ )
        {
            this.add(points[i], dists, 0, results);
            dists.removeAllFrom(0);
        }
    }


    //
    // From interface ClusterInterior.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    public void add( Point point, ArrayOfDouble dists, long[] results )
    {
        this.add(point, dists, 0, results);
    }


    private void add( Point point, ArrayOfDouble dists, int firstIdx, 
                      long[] results )
    {
        boolean pointAdded = false;
        for ( int i = firstIdx; !pointAdded && i < arraySize; i++ )
            if ( clusters[i].canAdd(point, dists, results) )
            {
                clusters[i].add(point, dists, results);
                pointAdded = true;
            }

        if ( !pointAdded )
        {
            PointRLC pointRLC = new PointRLC(point, dists);
            this.pushCluster( new ClusterClass(pointRLC, radius, level) );
        }
        size++;
    }        


    private void pushCluster( Cluster cluster )
    {
        if ( arraySize == clusters.length )
            this.changeArray(2 * arraySize);
        clusters[arraySize] = cluster;
        arraySize++;
    }


    private void changeArray( int capacity )
    {
        Cluster[] newClusters = new Cluster[capacity];
        System.arraycopy(clusters, 0, newClusters, 0, arraySize);
        clusters = newClusters;
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // REMOVE
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


    //
    // From interface MetricDS.
    //
    // If the specified point is in the metric data structure,
    // removes it and returns true; otherwise, returns false.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    public boolean remove( Point point, long[] results )
    {
        double[] dist = new double[1];
        results[0] = 0;
        return this.remove(point, dist, results);
    }


    //
    // From interface ClusterInterior.
    //
    // Returned value:
    //   true, if the point has been removed;
    //   false, otherwise.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    public boolean remove( Point point, double[] dist, long[] results )
    {
        boolean clusterFound = false;
        boolean pointRemoved = false;
        for ( int i = 0; !clusterFound && i < arraySize; i++ )
        {
            Cluster cluster = clusters[i];
            int res = cluster.fitsIn(point, dist, results);
            if ( res == 1 ) 
            {
                // Point fits in the cluster interior.
                clusterFound = true;
                pointRemoved = cluster.remove(point, dist, results);
            }
            else if ( res == 0 )
            {
                // Remove center.
                clusterFound = true;
                pointRemoved = true;
                this.shiftLeft(i + 1);
                this.reAdd(cluster, i, results);
            }
            // else res == -1  and  point is outside the cluster ball,
            //  so continue.
        }

        if ( pointRemoved )
            size--;
        return pointRemoved;
    }


    private void reAdd( Cluster cluster, int firstIdx, long[] results )
    {
        // To avoid memory fragmentation.
        int distsSize = Math.max(DEFAULT_MAX_HIGHT, level);
        ArrayOfDouble dists = new ArrayOfDouble(distsSize);  
        Iterator<PointRLC> iterator = cluster.getInteriorIterator();
        while ( iterator.hasNext() )
        {
            PointRLC pointRLC = iterator.next();
            Point point = pointRLC.getPoint();
            dists.initialise(pointRLC.getDistances(), level);
            size--;
            this.add(point, dists, firstIdx, results);
        }
            
    }


    private void shiftLeft( int firstIdx )
    {
        for ( int i = firstIdx; i < arraySize; i++ )
            clusters[i - 1] = clusters[i];
        arraySize--;
    }



    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // RANGE SEARCH
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


    //
    // From interface MetricDS.
    //
    // Returns every point in the metric data structure
    // whose distance to the specified query does not exceed 
    // the specified search radius.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    public Collection<Point> rangeSearch( Point query, double searchRadius, 
                                          long[] results )
    {
        ArrayOfDouble minDist = new ArrayOfDouble(DEFAULT_MAX_HIGHT);
        results[0] = 0;
        Collection<Point> answer = new LinkedList<Point>();
        this.rangeSearch(query, searchRadius, minDist, answer, results);
        return answer;
    }


    //
    // From interface ClusterInterior.
    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    public void rangeSearch( Point query, double searchRadius, 
                             ArrayOfDouble minDist, 
                             Collection<Point> answer, long[] results )
    {
        for ( int i = 0; i < arraySize; i++ )
            if ( clusters[i].rangeSearch(query, searchRadius, minDist,
                                         answer, results) )
                // Query ball is inside the cluster.
                break;
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // ITERATOR
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


    public Iterator<PointRLC> iterator( )
    {
        return new RLCIterator(clusters, arraySize);
    }


}

