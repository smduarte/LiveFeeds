package rlc;

public class PointString implements Point
{

    private char[] word;


    public PointString( String point )
    {
        word = new char[ point.length() ];
        for ( int i = 0; i < point.length(); i++ )
            word[i] = point.charAt(i);
    }


    // From interface Point.


    //
    // Edit or Levenshtein distance.
    //
    public double distance( Point point )
    {
        PointString pointStr = (PointString) point;
        int m = word.length;
        int n = pointStr.word.length;
        int[][] dist = new int[m + 1][n + 1];
        int i, j;
        int cost;

//      if ( m == 0 ) 
//             return n;
//      else if ( n == 0 )
//             return m;

        for ( i = 0; i <= m; i++ ) 
            dist[i][0] = i;

        for ( j = 1; j <= n; j++ ) 
            dist[0][j] = j;

        for ( i = 1; i <= m; i++ ) 
            for ( j = 1; j <= n; j++ )
            {
                if ( word[i-1] == pointStr.word[j-1] )
                    cost = 0;
                else
                    cost = 1;
                dist[i][j] = minimum( dist[i-1][j] + 1, 
                                      dist[i][j-1] + 1, 
                                      dist[i-1][j-1] + cost);
            }
        return dist[m][n];
    }


    private static int minimum ( int a, int b, int c ) 
    {
        int min;

        if ( a <= b ) 
            min = a;
        else
            min = b;
        if ( c < min )
            min = c;
        return min;
    }


    // From class Object.


    public boolean equals ( Object object )
    {
        if ( object instanceof PointString )
        {
            PointString point = (PointString) object;

            if ( word.length != point.word.length )
                return false;

            for ( int i = 0; i < word.length; i++ )
                if ( word[i] != point.word[i] )
                    return false;
            return true;
        }
        else
            return false;
    }


    public String toString( )
    {
        return new String(word);
    }


}

