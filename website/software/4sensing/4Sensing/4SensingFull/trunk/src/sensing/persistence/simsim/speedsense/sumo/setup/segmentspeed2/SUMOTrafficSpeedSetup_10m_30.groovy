package sensing.persistence.simsim.speedsense.sumo.setup.segmentspeed2

class SUMOTrafficSpeedSetup_10m_30 extends SUMOTrafficSpeedSetup {

	public SUMOTrafficSpeedSetup_10m_30() {
		super();
		config.VT_WINDOW_SIZE = 600;
		config.SUMO_MNODE_RATE = 0.3;
	}

}
