package rlc;

import java.util.Iterator;
import java.util.Collection;

class Bucket implements ClusterInterior
{

    private static final int MIN_CAPACITY = 2;


    // bucket is sorted in decreasing order by the last distances.
    private PointRLC[] bucket;

    private int size;

    private int level;


    public Bucket( int theLevel )
    {
        this(MIN_CAPACITY, theLevel);
    }


    public Bucket( int capacity, int theLevel )
    {
        bucket = new PointRLC[capacity];
        size = 0;
        level = theLevel;
    }


    public int getSize( )
    {
        return size;
    }


    public int getMaxLevel( )
    {
        return level;
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // ADD 
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


    //
    // Results in position:
    //   0 - Number of distances computed. NONE in this method.
    //

    public void add( Point point, ArrayOfDouble dists, long[] results )
    {
        if ( size == bucket.length )
            this.changeBucket(2 * size);
        PointRLC pointRLC = new PointRLC(point, dists);
        this.insertOrd(pointRLC);
    }


    //
    // bucket is sorted in decreasing order by the last distances.
    //

    private void insertOrd( PointRLC point )
    {
        double dist = point.getLastDist();
        int hole = size;
        while ( hole > 0 && dist > bucket[hole-1].getLastDist() )
        {
            bucket[hole] = bucket[hole-1];
            hole--;
        }
        bucket[hole] = point;
        size++;
    }
        

    private void changeBucket( int capacity )
    {
        PointRLC[] newBucket = new PointRLC[capacity];
        System.arraycopy(bucket, 0, newBucket, 0, size);
        bucket = newBucket;
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // REMOVE
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


    //
    // Returned value:
    //   true, if the point has been removed;
    //   false, otherwise.
    //
    // Results in position:
    //   0 - Number of distances computed. NONE in this method.
    //

    public boolean remove( Point point, double[] dist, long[] results )
    {
        if ( this.removeOrd(point, dist[0]) )
        {
            if ( bucket.length > MIN_CAPACITY && size <= bucket.length / 2 )
                this.changeBucket(bucket.length / 2);
            return true;
        }
        else
            return false;
    }


    private boolean removeOrd( Point point, double dist )
    {
        int pos = this.findOrd(point, dist, 0, size - 1);
        if ( pos == -1 )
            return false;
        else
        {
            for ( int i = pos; i < size - 1; i++ )
                bucket[i] = bucket[i+1];
            bucket[size - 1] = null;
            size--;
            return true;
        }
    }


    //
    // bucket is sorted in decreasing order by the last distances.
    //

    private int findOrd( Point point, double dist, int firstIdx, int lastIdx )
    {
        if ( firstIdx > lastIdx )
            return -1;
        else
        {
            int midIdx = ( firstIdx + lastIdx ) / 2;
            double midDist = bucket[midIdx].getLastDist();
            if ( midDist == dist )
                return this.findEqualDist(point, dist, midIdx);
            else if ( midDist < dist )
                return this.findOrd(point, dist, firstIdx, midIdx - 1);
            else
                return this.findOrd(point, dist, midIdx + 1, lastIdx);
        }        
    }


    private int findEqualDist( Point point, double dist, int pos )
    {
        if ( bucket[pos].equals(point) )
            return pos;
        else
        {
            int curPos = pos - 1;
            while ( curPos >= 0 && bucket[curPos].getLastDist() == dist )
            {        
                if ( bucket[curPos].equals(point) )
                    return curPos;
                curPos--;
            }
                
            curPos = pos + 1;
            while ( curPos < size && bucket[curPos].getLastDist() == dist )
            {
                if ( bucket[curPos].equals(point) )
                    return curPos;
                curPos++;
            }

            return -1;
        }
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // RANGE SEARCH
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


    //
    // Results in position:
    //   0 - Number of distances computed.
    //

    public void rangeSearch( Point query, double searchRadius, 
                             ArrayOfDouble minDist, 
                             Collection<Point> answer, long[] results )
    {
        double minLastDist = minDist.get(level);

        for ( int i = 0; i < size; i++ )
        {
            double pointLastDist = bucket[i].getLastDist();

            if ( pointLastDist < minLastDist )
                // Discard all objects from i to size - 1.
                break;

            else if ( pointLastDist <= -minLastDist )
            {
                // Collect all objects from i to size - 1.
                for ( int j = i; j < size; j++ )
                    answer.add( bucket[j].getPoint() );
                break;
            }

            else
            {
                int res = this.elimByAncestor(bucket[i], minDist);
                if ( res == 1 )
                    // Collect object in bucket[i].
                    answer.add( bucket[i].getPoint() );
                else if ( res == 0 )              
                {
                    // Compute distance.
                    results[0]++;
                    if ( bucket[i].distance(query) <= searchRadius )
                        answer.add( bucket[i].getPoint() );
                }
                // else res == -1, 
                //      so object in bucket[i] is discarded.
            }
        }
    }


    //
    // Returned value: 
    //   1 - point belongs to the answer set;
    //   0 - nothing can be stated about the point;
    //  -1 - point does not belong to the answer set.
    //

    private int elimByAncestor( PointRLC point, ArrayOfDouble minDist )
    {
        for ( int i = level - 1; i >= 0; i-- )
        {
            double pointDistI = point.getDist(i);
            double minDistI = minDist.get(i);
            if ( pointDistI < minDistI )
                // Point does not belong to the answer set.
                return -1;
            else if ( pointDistI <= -minDistI )
                // Point belongs to the answer set.
                return 1;
        }
        // Nothing can be stated about the point.
        return 0;
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // ITERATOR
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //


    public Iterator<PointRLC> iterator( )
    {
        return new BucketIterator(bucket, size);
    }


}

