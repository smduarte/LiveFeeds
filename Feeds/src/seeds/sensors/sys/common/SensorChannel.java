package seeds.sensors.sys.common;

import java.util.Collection;
import java.util.HashSet;

import feeds.sys.FeedsNode;
import feeds.sys.core.ContainerListener;
import feeds.sys.core.ID;
import feeds.sys.pipeline.BasicTemplate;
import feeds.sys.pipeline.ChannelSubscription;
import feeds.sys.pipeline.ChannelSubscriptions;

abstract public class SensorChannel<E extends SensorParameters, F> extends BasicTemplate<E, Void, F, Void> {

	public void init() {
		super.init();
		
		
		final VSensor sensor = getSensor() ;
		
		sensor.setParams( getSensorParameters() ) ;

		psc.monitor(new ContainerListener<ChannelSubscriptions<E>>() {
			public void handleContainerUpdate(ChannelSubscriptions<E> c) {
				sensor.setParams( getSensorParameters() ) ;				
			}
		});
	}
	
	private Collection<SensorParameters> getSensorParameters() {
		Collection<SensorParameters> res = new HashSet<SensorParameters>() ;
		for( ChannelSubscription<E> i : psc.subscriptions() ) 
			res.add( (SensorParameters) i.criteria() ) ;
		return res ;
	}
	
	protected abstract VSensor getSensor() ;
	
    protected ID thisNode = FeedsNode.id() ;
}
