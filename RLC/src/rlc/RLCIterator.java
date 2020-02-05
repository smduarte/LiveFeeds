package rlc;

import java.util.Iterator;
import java.util.NoSuchElementException;

class RLCIterator implements Iterator<PointRLC>
{

    private Cluster[] clusters;

    private int arraySize;

    private int nextIdx;

    private Iterator<PointRLC> clusterIterator;


    public RLCIterator( Cluster[] theClusters, int theArraySize )
    {
        clusters = theClusters;
        arraySize = theArraySize;
        nextIdx = 0;
        this.nextClusterIterator();
    }


    public boolean hasNext( )
    {
        return clusterIterator != null;
    }


    public PointRLC next( ) throws NoSuchElementException
    {
        if ( !this.hasNext() )
            throw new NoSuchElementException();

        PointRLC point = clusterIterator.next();
        if ( !clusterIterator.hasNext() )
            this.nextClusterIterator();
        return point;
    }


    private void nextClusterIterator( )
    {
        if ( nextIdx < arraySize )
            clusterIterator = clusters[ nextIdx++ ].iterator();
        else
            clusterIterator = null;
    }


    public void remove( ) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }


}

