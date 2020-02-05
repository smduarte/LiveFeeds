package sensing.persistence.simsim.speedsense.sumo.setup.segmentspeed2

class SUMOTrafficSpeedSetup_10m_90 extends SUMOTrafficSpeedSetup {

	public SUMOTrafficSpeedSetup_10m_90() {
		super();
		config.VT_WINDOW_SIZE = 600;
		config.SUMO_MNODE_RATE = 0.9;
	}

}
