package meeds.a;

import feeds.api.* ;
import meeds.api.* ;

public class Tester  {
	
	
	public static void main(String[] args) throws Exception {
				
		final Channel<Long, Integer, Float, Double > x = Meeds.lookup("/System/Test") ;
		
		x.subscribe( new Subscriber<Long, Integer>() {			
			public void notify( Receipt r, Long e, Payload<Integer> p) {
				System.out.println( "here:" + e + "/" + p.data() + "/" + Thread.currentThread() ) ;
				x.feedback( r, 0.0f, 3.0) ;
			}			

		}) ;
		x.subscribeFeedback( new FeedbackSubscriber<Float, Double>() {			
			public void notifyFeedback( Receipt r, Float e, Payload<Double> p) {
				System.out.println( e + "/" + p.data() + "/" + Thread.currentThread() ) ;
				x.feedback(r, 0.0f, 1.0) ;
			}
		}) ;
//		for(;;) {
//			
//			x.publish( 1L, 0 ) ;
//			Feeds.out.println( Feeds.time() ) ;
//			Feeds.sleep(5) ;
//		}

	}

}