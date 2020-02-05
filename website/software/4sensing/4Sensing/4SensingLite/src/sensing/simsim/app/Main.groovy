package sensing.simsim.app;

import static simsim.core.Simulation.Gui
import sensing.simsim.app.map.*
import sensing.simsim.app.osm.OSMSpeedSenseSim
import sensing.simsim.app.setup.*
import simsim.ssj.BinnedTally
import simsim.ssj.charts.BinnedTallyDisplay

public class Main extends OSMSpeedSenseSim {

	public static BinnedTally histogram = new BinnedTally(10.0, "Speed Histogram");
	
	protected static void config( String[] args) {	
		OSMSpeedSenseSim.config( args);
	}
	
	public static void main(String[] args) throws Exception {
		if( args.length != 4 ) {
			args = ["sensing.simsim.app.setup.Test", "dummy", "dev", "true"] ;
		}
		config(args);
		Main m = new Main() ;
		m.init() ;
		BinnedTallyDisplay chart = new BinnedTallyDisplay("Speed Histogram", histogram);
		chart.chart().chart().setTitle("Lisbon Car Speed Histogram") ;
		chart.chart().chart().removeLegend();
		chart.chart().setAxisLabels("car speed (Km/h)", "% cars");
		Gui.setFrameRectangle("MainFrame", 2, 0, 512, 512);
		Gui.setFrameRectangle("Speed Histogram", 520, 0, 512, 512);
		Gui.setDesktopSize(1024+12, 512+48);
		m.start();
		
	}
}
