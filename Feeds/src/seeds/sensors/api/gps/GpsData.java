package seeds.sensors.api.gps;

public class GpsData {

	public double longitude;
	public double latitude;
	public double currentTime;

	public GpsLocation currentLocation, previousLocation;

	public GpsData() {
		longitude = latitude = currentTime = -1;
		currentLocation = previousLocation = new GpsLocation();
	}

	public GpsData( GpsLocation currentLocation, GpsLocation previousLocation ) {
		this.currentLocation = currentLocation;
		this.previousLocation = previousLocation;

		if (currentLocation != null) {
			this.longitude = currentLocation.getLongitude();
			this.latitude = currentLocation.getLatitude();
			this.currentTime = currentLocation.getCurrentTime();
		}
	}
	
	
	public double getLongitude() {
		return longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
}