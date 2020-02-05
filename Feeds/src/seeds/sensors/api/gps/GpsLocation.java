package seeds.sensors.api.gps;

public class GpsLocation {
	
	public double longitude;
	public double latitude;
	public double currTime;
	public double accuracy;
	public double speed;
	//orientation of the movement related to real north
	public double bearer;
	
	public GpsLocation(){
		this(-1, -1, -1, -1, -1, -1) ;
	}
	
	public GpsLocation(double lon, double lat, double currTime, double accuracy, double speed, double bearer){
		this.longitude = lon;
		this.latitude = lat;
		this.currTime = currTime;
		this.accuracy = accuracy;
		this.speed = speed;
		this.bearer = bearer;
	}
	
	public double getLongitude(){
		return this.longitude;
	}
	
	public double getLatitude(){
		return this.latitude;
	}
	
	public double getCurrentTime(){
		return this.currTime;
	}
	
	public double getAccuracy(){
		return this.accuracy;
	}
	
	public double getSpeed(){
		return this.speed;
	}
	
	public double getBearer(){
		return this.bearer;
	}
	
	@Override
	public String toString(){
		return "long: "+this.longitude+" lat: "+this.latitude+" time: "+this.currTime+" accuracy: "+this.accuracy;
	}
}