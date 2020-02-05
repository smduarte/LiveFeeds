package feeds.sys.util;

import java.util.* ;

public interface ExpirableCollectionListener<T> {
    public void valueExpired( Collection<T> s, T value ) ;
}
