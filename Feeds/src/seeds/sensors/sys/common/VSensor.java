package seeds.sensors.sys.common;

import java.util.Collection;

import feeds.sys.core.Container;


abstract public class VSensor extends Container<Sensor> implements Sensor {
	
	protected VSensor( String name ) {
		super(0, name) ;
	}
	
	
	abstract public <Q> Q getValue()  ;

	abstract public <Q> Q getConfig() ;

	abstract protected void reconfigureSensor() ;
	
	abstract public void setParams( Collection<SensorParameters> params) ;
	
	
	public static class Impl extends VSensor {
		
		protected Impl( String name ) {
			super(name) ;
		}
		
		public <Q> Q getValue()  {
			return null ;
		}

		public <Q> Q getConfig() {
			return null ;
		}

		protected void reconfigureSensor() {
		}
		
		public void setParams( Collection<SensorParameters> params) {
		}	
	}
}


