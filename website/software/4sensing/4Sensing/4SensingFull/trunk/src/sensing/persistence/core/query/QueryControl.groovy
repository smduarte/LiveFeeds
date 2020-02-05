package sensing.persistence.core.query;

public class QueryControl {
	enum Cmd {CLOSE};
	String queryId;
	Cmd  cmd;
}
