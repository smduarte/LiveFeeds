package simsim.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {

	public static Logger Log ;
	
	static {
//		Log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);	
		Log = Logger.getAnonymousLogger(null) ;
		
//		ConsoleHandler ch = new ConsoleHandler() ;
//		ch.setFormatter( new SimplerFormatter() ) ;
//		ch.setLevel( Level.ALL ) ;
//		Log.addHandler( ch ) ;
		Log.setLevel( Level.ALL ) ;	
	}
	
	
	static class SimplerFormatter extends Formatter {

		public String format(LogRecord r) {
			return new StringBuilder().append(r.getLevel()).append(" : ").append( r.getMessage() ).append('\n').toString() ;
		}
		
	}
}
