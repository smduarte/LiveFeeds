package rlc;                                         

class PointRLC implements Point
{

    private Point point;

    private double[] distances;


    public PointRLC( Point thePoint, ArrayOfDouble dists )
    {
        point = thePoint;
        int distsSize = dists.getSize();
        distances = new double[distsSize];
        for ( int i = 0; i < distsSize; i++ )
            distances[i] = dists.get(i);
    }


    // From interface Point.


    public double distance( Point thePoint )
    {
        if ( thePoint instanceof PointRLC )
        {
            PointRLC pointRLC = (PointRLC) thePoint;
            return point.distance(pointRLC.point);
        }
        else
            return point.distance(thePoint);
    }


    // From class Object.


    public boolean equals( Object object )
    {
        if ( object instanceof PointRLC )
        {
            PointRLC pointRLC = (PointRLC) object;
            return point.equals(pointRLC.point);
        }
        else
            return point.equals(object);
    }
                      

    public String toString( )
    {
        return point.toString();
    }


    // Specific to class RLCPoint.


    public Point getPoint( )
    {
        return point;
    }


    public double[] getDistances( )
    {
        return distances;
    }


    public double getDist( int pos )
    {
        return distances[pos];
    }
        

    public double getLastDist( )
    {
        return distances[distances.length - 1];
    }
        

}

