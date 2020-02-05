package meeds.sys;


public class MeedsProxy {

	public static void main(String args[]) {
		try {			
			new meeds.sys.core.ProxyNodeContext().init();
		} catch (Exception x) {
			x.printStackTrace();
		}
		
//		Feeds.newThread(true, new Runnable() {
//			public void run() {
//				final Channel<Integer, Void, Void, Void> ch = Feeds.clone("catadupa", "/xpto", -1);
//				ch.subscribe(new Subscriber<Integer, Void>() {
//					public void notify(Receipt r, Integer e, Payload<Void> p) {
//						Feeds.err.println( e ) ;
//					}
//				});
//
//				for(;;) {
//					ch.publish(new java.util.Random().nextInt(), null);
//					Feeds.sleep(1);
//				}
//			}
//			}).start();
	}
}
