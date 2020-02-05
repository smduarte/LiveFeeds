package feeds.sys.backbone;

import java.util.* ;

public class Scheduler<T extends Comparable<T>> {
    
    public static final double HIGHEST_PRIORITY    = 0.005 ;
    public static final double HIGH_PRIORITY       = 0.010 ;
    public static final double MEDIUM_PRIORITY     = 0.050 ;
    public static final double LOW_PRIORITY        = 0.100 ;
    public static final double LOWEST_PRIORITY     = 0.200 ;
    
    public Scheduler() {
        this( 1.0 ) ;
    }
    
    public Scheduler( double alpha ) {
        this.alpha = alpha ;
    }
    
    public T next() {
        try {
            synchronized( queue ) {
                QueueItem qi = queue.remove() ;
                lValue = qi.value ;
                qi.reSchedule() ;
                return qi.item ;
            }
        }
        catch( Exception x ) {
            return null ;
        }
    }
    
    public void schedule( T o, double priority ) {
        reSchedule( o, priority ) ;
    }
    
    public void reSchedule( T o ) {
        synchronized( queue )  {
            QueueItem qi = o2qi.get( o ) ;
            if( qi != null ) qi.reSchedule() ;
        }
    }
    
    public void scheduleAll( Collection<T> c, double priority ) {
        synchronized( queue )  {
        	for( T i : c )
        		schedule( i, priority ) ;
        }
    }
    
    public void reSchedule( T o, double priority ) {
        synchronized( queue )  {
            QueueItem qi = o2qi.get( o ) ;
            if( qi == null ) {
                qi = new QueueItem( o, priority ) ;
                o2qi.put( o, qi ) ;
            }
            qi.reSchedule( priority ) ;
        }
    }
    
    public void reScheduleAll( Collection<T> c ) {
    	for( T i : c )
    		reSchedule( i ) ;
    }

    public void reScheduleAll( Collection<T> c, double priority ) {
    	for( T i : c)
    		reSchedule( i, priority ) ;
    }
    
    public void remove( Object o ) {
        synchronized( queue ) {
            QueueItem qi = o2qi.get( o ) ;
            if( qi != null ) {
                o2qi.remove( o ) ;
                queue.remove( qi ) ;
            }
        }
    }
    
    public void removeAll( Collection<T> c ) {
        synchronized( queue ) {
        	for( T i : c ) {
                QueueItem qi = o2qi.get( i ) ;
        		if( qi != null ) {
        			o2qi.remove( i ) ;
        			queue.remove( qi ) ;
        		}
        	}
        }
    }
    
    public int size() {
    	return queue.size();
    }
    
    public String toString() {
    	TreeSet<String> res = new TreeSet<String>() ;
    	for( QueueItem i : queue ) 
    		res.add( i.toString() ) ;
    	return res.toString() ;
    	//return new TreeSet<QueueItem>( queue ).toString() ;
    }
    
    private double alpha ;
    private double lValue = 0.0f ;
    private Queue<QueueItem> queue = new PriorityQueue<QueueItem>() ;
    private Map<T, QueueItem> o2qi = new HashMap<T, QueueItem>() ;
    
    class QueueItem implements Comparable<QueueItem> {
        
        T item ;
        int sampled ;
        double value ;
        double priority ;
        
        QueueItem( T item, double priority ) {
            this.value = 0 ;
            this.item = item ;
            this.sampled = 0 ;
            this.value = lValue ;

            o2qi.put( item, this ) ;
            this.priority = Math.pow( priority, alpha ) ;
        }
        
        void reSchedule() {
            synchronized( queue ) {
                queue.remove( this ) ;
                value += priority ;
                queue.add( this ) ;
            }
        }
        
        void reSchedule( double newPriority ) {
            synchronized( queue ) {
                queue.remove( this );
                this.priority = Math.pow( newPriority, alpha ) ;
                value += priority ;
                queue.add( this ) ;
            }
        }
        
        public int compareTo( QueueItem other) {            
        	return value == other.value ? item.compareTo(other.item) : (value < other.value ? -1 : 1 ) ;
        }
        
        public String toString2() {
            return item + "/" + priority + "/" + value ;
        }
        
        public String toString() {
            return item.toString() ;
        }
    }
}
