package rlc;                                         

class ArrayOfDouble
{

    public static final int DEFAULT_CAPACITY = 10;


    private double[] array;

    private int size;


    public ArrayOfDouble( )                      
    {    
        this(DEFAULT_CAPACITY);
    }


    public ArrayOfDouble( int capacity )  
    {    
        array = new double[capacity];
        size = 0;
    }


    public int getSize( )
    {
        return size;
    }


    public double get( int pos )
    {    
        return array[pos];
    }


    public void add( double value )
    {    
        if ( size == array.length )
            this.changeArray( 2 * size );
        array[size] = value;
        size++;
    }


    public void set( int pos, double value ) throws IndexOutOfBoundsException
    {
        if ( pos > size )
            throw new IndexOutOfBoundsException();

        if ( pos == array.length )
            // size == array.length.
            this.changeArray( 2 * size );
        array[pos] = value;
        size = pos + 1;
    }


    private void changeArray( int capacity )
    {
        double[] newArray = new double[capacity];
        System.arraycopy(array, 0, newArray, 0, size);
        array = newArray;
    }


    public void initialise( double[] values )
    {
        this.initialise(values, values.length);
    }


    public void initialise( double[] values, int theSize )
    {
        System.arraycopy(values, 0, array, 0, theSize);
        size = theSize;
    }


    public void removeAllFrom( int pos )
    {
        size = pos;
    }


}
