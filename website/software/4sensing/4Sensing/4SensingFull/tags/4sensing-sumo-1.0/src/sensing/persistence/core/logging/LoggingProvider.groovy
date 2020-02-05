package sensing.persistence.core.logging;

public interface LoggingProvider {
	public static int DEBUG = 1;
	public static int INFO = 2;
	public static int ERROR = 3;
	public void log(int level, Object src, String where, String msg);
}
