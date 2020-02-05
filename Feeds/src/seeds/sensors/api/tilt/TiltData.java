package seeds.sensors.api.tilt;

public class TiltData {

	public float azimuth, pitch, roll ;
	
	public TiltData() {
		this(0,0,0) ;
	}
	
	public TiltData( float a, float p, float r) {
		this.azimuth = a ; this.pitch = p ; this.roll = r ;
	}

}
