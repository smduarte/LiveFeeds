package feeds.simsim;

import feeds.api.Feeds;
import feeds.simsim.sys.SS_cNode;
import feeds.sys.FeedsRegistry;
import feeds.sys.binding.Binding;
import feeds.sys.membership.MembershipService;
import feeds.sys.registry.RegistryService;
import feeds.sys.tasks.Task;

public class cNode extends SS_cNode {

	public static NodeDB<cNode> db = new NodeDB<cNode>();

	public cNode() {
		super(sNode.db.randomNode());
		index = db.store(this);
	}

	public void initNode() {

		FeedsRegistry.put("DataURL", url());
		FeedsRegistry.put("BindingURLs", server.url());

		RegistryService.start();
		MembershipService.start();
		Binding.start();

		if( index == 0 ) {
			new Task(750) {
				public void run() {
					Feeds.clone("centradupa", "/testChannel", -1 ) ;
				}				
			};			
		}
		
//		new Task(this, 500 + 500 * simsim.core.Simulation.rg.nextDouble() ) {
//			public void run() {
//				Feeds.out.println("INDEX:" + index);
//				
//				
//				
//				
//				final Channel<Integer, Void, Void, Void> ch = Feeds.lookup("/testChannel");
//
//				ch.subscribe(0, new Subscriber<Integer, Void>() {
//					public void notify(Receipt r, Integer e, Payload<Void> p) {
//						Feeds.out.println(index + ":" + e);
//					}
//				});
//				
//
//				new PeriodicTask(500, 500) {
//					public void run() {
//						//int i = (int) (10 * Math.sin(Feeds.time()));
//						if( index == 0 ) 
//							ch.publish(0, null);
//					}
//				};
//			}
//		};
	}
}
