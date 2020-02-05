package seeds.sensors.api.acc;

public class AccData {

	public float x, y, z ;

	public AccData() {
		x = y = z = 0 ;
	}
	
	public AccData( float x, float y, float z ) {
		this.x = x ;
		this.y = y ;
		this.z = z ;
	}
}
