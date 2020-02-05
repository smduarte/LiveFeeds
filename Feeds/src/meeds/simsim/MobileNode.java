package meeds.simsim;


import meeds.simsim.sys.SS_MobileNode;
import meeds.simsim.tests.latency.Latency1;
import meeds.sys.directory.MeedsDirectoryStorage;
import meeds.sys.homing.Homing;
import meeds.sys.proxying.ProxyBinding;
import meeds.sys.registry.MeedsRegistryService;
import feeds.simsim.NodeDB;
import feeds.sys.FeedsRegistry;
import feeds.sys.tasks.Task;

public class MobileNode extends SS_MobileNode {

	public static NodeDB<MobileNode> db = new NodeDB<MobileNode>();

	public MobileNode() {
		index = db.store(this);
	}

	public void initNode() {

		FeedsRegistry.put("DataURL", url());
		FeedsRegistry.put("HomebaseURLs", homebase.url());

		// RegistryService.start();

		MeedsDirectoryStorage.init();
		MeedsRegistryService.start();
		Homing.start();

		new ProxyBinding().start();
		
		new Task(100) {
			public void run() {
				//if(index==0) 
				//Latency1.sendProbes(index) ;
				//PacketLoss.recvProbes(index) ;
				//PacketLoss.sendProbes(index) ;
			}
		};
	}
}
