package a;

import feeds.api.Channel;
import feeds.api.FeedbackSubscriber;
import feeds.api.Feeds;
import feeds.api.Payload;
import feeds.api.Receipt;
import feeds.api.Subscriber;


public class Tester  {
	
	
	public static void main(String[] args) throws Exception {
				
		final Channel<Long, String, Integer, String > x = Feeds.clone("centradupa_anycast", "/xpto_anycast", -1) ;
		
		x.subscribe( 1L, new Subscriber<Long, String>() {			
			public void notify( Receipt r, Long e, Payload<String> p) {
				Feeds.out.println("notify:" + e + "/" + p.data() ) ;
				x.feedback( r, 0, "" + Feeds.time() ) ;
			}			

		}) ;

		x.subscribe( 0L, new Subscriber<Long, String>() {			
			public void notify( Receipt r, Long e, Payload<String> p) {
				Feeds.out.println("notify:" + e + "/" + p.data() ) ;
				x.feedback( r, 0, "" + Feeds.time() ) ;
			}			

		}) ;

		
		x.subscribeFeedback( 0, new FeedbackSubscriber<Integer, String>() {			
			public void notifyFeedback( Receipt r, Integer e, Payload<String> p) {
				Feeds.out.println("notifyFeedback:" + r + " -> " + e + "/" + p.data() ) ;
			}
		}) ;
				
		long i = 0 ;
		for(;;) {	
			x.publish( i++ % 2, "Hello " + Feeds.time() ) ;
			Feeds.sleep(1.0) ;
		}

	}

}