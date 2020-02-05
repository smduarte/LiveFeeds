package sensing.persistence.core.query;

import java.util.UUID;

public class QueryData {
	String queryId;
	Query query; // needed for NEAREST_TREE
	def data;
	String context;
}
