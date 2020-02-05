package feeds.sys.util;

import java.util.* ;

public interface ExpirableMapListener<K,V> {
    public void keyExpired( Map<K,V> m, K key, V value ) ;
}
