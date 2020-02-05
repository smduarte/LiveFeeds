package feeds.sys.membership;

import feeds.api.Feeds;


public class MembershipService {

	void init() {
		Feeds.lookup("/System/MembershipChannel");
	}

	public static void start() {
		new MembershipService().init();
	}
}
