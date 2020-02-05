package seeds.sensors.api.cam;



public class CamData {

	public int width;
	public int height;
	public byte[] data;
	
	public CamData( int width, int height, byte[] pic) {
		super();
		this.width = width;
		this.height = height;

	}
	
}
