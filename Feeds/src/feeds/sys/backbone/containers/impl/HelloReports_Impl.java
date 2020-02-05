package feeds.sys.backbone.containers.impl;

import java.util.*;

import feeds.sys.core.*;
import feeds.sys.util.*;
import feeds.sys.backbone.*;
import feeds.sys.backbone.containers.*;

public class HelloReports_Impl extends Container<HelloReports> implements HelloReports, HelloReports.Updater {

	private static final double NEW_SAMPLE_WEIGHT = 0.7;
	private static final double ExpirationDeadline = -1.0;

	public HelloReports_Impl(String name) {
		super.notifyUpdate() ;
	}

	synchronized public void remove( ID node) {
		reports.remove(node);
	}

	synchronized public void add(HelloReport hr) {
		HelloReport o = reports.get(hr.src());
		if (o != null && hr.rtt() >= 0)
			hr.setData((1 - NEW_SAMPLE_WEIGHT) * o.rtt() + NEW_SAMPLE_WEIGHT * hr.rtt(), hr.samples() + o.samples());

		reports.put(hr.src(), hr, ExpirationDeadline);

		if (o == null)
			super.notifyUpdate();
	}

	public boolean isNewServer( ID s) {
		return !reports.containsKey(s);
	}

	public Map<ID, HelloReport> reports() {
		return Collections.unmodifiableMap( reports ) ;
	}

	synchronized public Collection<ID> nodes() {
		return reports.keySet();
	}

	synchronized public Collection<ID> nodes( int samples) {
		List<ID> res = new ArrayList<ID>();
		for (HelloReport i : reports.values())
			if (i.samples() > samples)
				res.add(i.src());
		return res;
	}

	protected ExpirableMap<ID, HelloReport> reports = new ExpirableMap<ID, HelloReport>(10.0, new ExpirableMapListener<ID, HelloReport>(){
		public void keyExpired(Map<ID, HelloReport> m, ID key, HelloReport value) {
			notifyUpdate() ;
		}
	});

	
}