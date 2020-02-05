package seeds.sensors.api.cam;

import seeds.sensors.sys.common.SensorParameters;

public class CamRequest implements SensorParameters {

	public int width;
	public int height;

	public CamRequest() {
		this( DEFAULT_WIDTH, DEFAULT_HEIGHT ) ;
	}

	public CamRequest( int width, int height ) {
		this.width = width;
		this.height = height;
	}	
	
	protected static final int DEFAULT_WIDTH = 200;
	protected static final int DEFAULT_HEIGHT = 180;
}
