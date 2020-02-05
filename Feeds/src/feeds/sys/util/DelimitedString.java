package feeds.sys.util;

import java.util.* ;

public class DelimitedString {
    
    private String items[] ;
    
    public DelimitedString(String src, String delim) {
        ArrayList<String> res = new ArrayList<String>() ;
        StringTokenizer t = new StringTokenizer( src, delim ) ;
        while( t.hasMoreTokens() ) {
            res.add( t.nextToken() ) ;
        }
        items = res.toArray( new String[ res.size() ] ) ;
    }
    
    public String[] items() {
        return items ;
    }
    
    public int size() {
        return items.length ;
    }
}
