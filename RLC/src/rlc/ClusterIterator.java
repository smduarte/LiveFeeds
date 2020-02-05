package rlc;

import java.util.Iterator;
import java.util.NoSuchElementException;

class ClusterIterator implements Iterator<PointRLC>
{

    private PointRLC point;

    private Iterator<PointRLC> iterator;

    private boolean pointReturned;


    public ClusterIterator( PointRLC center, ClusterInterior interior )
    {
        point = center;
        iterator = interior.iterator();
        pointReturned = false;
    }


    public boolean hasNext( )
    {
        return !pointReturned || iterator.hasNext();
    }


    public PointRLC next( ) throws NoSuchElementException
    {
        if ( !this.hasNext() )
            throw new NoSuchElementException();

        if ( !pointReturned )
        {
            pointReturned = true;
            return point;
        }
        else
            return iterator.next();
    }


    public void remove( ) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }


}

