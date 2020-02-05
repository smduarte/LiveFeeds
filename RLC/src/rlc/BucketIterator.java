package rlc;

import java.util.Iterator;
import java.util.NoSuchElementException;

class BucketIterator implements Iterator<PointRLC>
{

    private PointRLC[] bucket;

    // Number of points in the bucket.
    private int size;

    private int nextToReturn;


    public BucketIterator( PointRLC[] theBucket, int theSize )
    {
        bucket = theBucket;
        size = theSize;
        nextToReturn = 0;
    }


    public boolean hasNext( )
    {
        return nextToReturn < size;
    }


    public PointRLC next( ) throws NoSuchElementException
    {
        if ( !this.hasNext() )
            throw new NoSuchElementException();

        return bucket[ nextToReturn++ ];
    }


    public void remove( ) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }


}

