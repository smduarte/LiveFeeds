package meeds.simsim.sys;

import java.util.*;

import feeds.sys.*;
import feeds.sys.core.*;
import feeds.sys.transports.*;

public class SS_Transports extends feeds.simsim.sys.SS_Transports {


	public Transport openTransport(String url, String mode) {

		if (url.startsWith("hoq")) {
			ID src = FeedsNode.id();
			Transport res = hoqs.get(src);
			if (res == null) {
				res = new SS_HoqTransport(url, mode);
				hoqs.put(src, res);
			}
			return res;
		} else if (url.startsWith("poq")) {
			ID src = FeedsNode.id();
			Transport res = poqs.get(src);
			if (res == null) {
				res = new SS_PoqTransport(url, mode);
				poqs.put(src, res);
			}
			return res;
		}
			return super.openTransport(url, mode);
	}

	public static Transports factory() {
		if (factory == null || !(factory instanceof SS_Transports) ) {
			factory = new SS_Transports();
		}
		return factory;
	}

	static Map<ID, Transport> hoqs = new HashMap<ID, Transport>();
	static Map<ID, Transport> poqs = new HashMap<ID, Transport>();
}
