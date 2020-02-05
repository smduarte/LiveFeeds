package sensing.persistence.simsim.speedsense.sumo.setup.segmentspeed2

class SUMOTrafficSpeedSetup_10m_10 extends SUMOTrafficSpeedSetup {

	public SUMOTrafficSpeedSetup_10m_10() {
		super();
		config.VT_WINDOW_SIZE = 600;
		config.SUMO_MNODE_RATE = 0.1;
	}

}
