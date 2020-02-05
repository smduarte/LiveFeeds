package meeds.sys.proxying.channel;

import feeds.sys.core.*;
import feeds.sys.pipeline.*;
import feeds.sys.catadupa.template.*;
import feeds.sys.membership.containers.*;

import meeds.sys.*;

public class ProxyDiscoveryChannel<E> extends CatadupaTemplate<Void, Void, Void, Void> {

	public void init() {
		try {
			super.init();

			if( MeedsNode.isCnode() ) {
				final CriteriaDB.Updater cdbu = Container.byClass( CriteriaDB.class ) ;
				psc.monitor( new ContainerListener<ChannelSubscriptions<Void>>(){

					public void handleContainerUpdate(ChannelSubscriptions<Void> c) {
						cdbu.pUpdate( channel, c.criteria() ) ;
						feeds.api.Feeds.err.println( "My filters:" + c.criteria() ) ;
					}
					
				}) ;
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

}
