package sensing.persistence.simsim.speedsense.sumo.setup

class SUMOTrafficSpeedTTSetup_10m_1 extends SUMOTrafficSpeedTTSetup {

	public SUMOTrafficSpeedTTSetup_10m_1() {
		super();
		config.VT_WINDOW_SIZE = 600;
		config.SUMO_MNODE_RATE = 0.01;
	}

}