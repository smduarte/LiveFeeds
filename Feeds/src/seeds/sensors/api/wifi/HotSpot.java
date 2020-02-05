package seeds.sensors.api.wifi;

public class HotSpot {
	private String bssid;
	private String ssid;
	private String capabilities;
	private int frequency;
	private int level;

	public HotSpot( String bssid, String ssid, String capabilities, int frequency, int level) {
		this.bssid = bssid;
		this.ssid = ssid;
		this.capabilities = capabilities;
		this.frequency = frequency;
		this.level = level;
	}

	public String getBSSID() {
		return bssid;
	}

	public String getSSID() {
		return ssid;
	}

	public String getCapabilities() {
		return capabilities;
	}

	public int getFrequency() {
		return frequency;
	}

	public int getLevel() {
		return level;
	}
	
	public String toString() {
		return String.format("%s (%d)", ssid, level) ;
	}
}