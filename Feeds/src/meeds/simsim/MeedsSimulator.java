package meeds.simsim;

import meeds.simsim.osm.OsmMapModel;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import simsim.core.Displayable;
import simsim.core.Globals;
import simsim.core.Simulation;
import simsim.core.Task;
import simsim.gui.TileMapFrame;
import simsim.gui.TileMapFrame.Map;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import feeds.simsim.Node;
import feeds.simsim.pNode;
import feeds.simsim.sNode;

public class MeedsSimulator extends Simulation implements Displayable {

	public static final int TOTAL_mNODES = 24;
	public static final int TOTAL_hNODES = 7;
	public static final int TOTAL_xNODES = 10;
	public static final int TOTAL_sNODES = 1;
	public static final int TOTAL_pNODES = 1;

	public static OsmMapModel osm;

	public static TileMapFrame carMap;

	MeedsSimulator() {
		super(1, null);
	}

	MeedsSimulator init() {
		Spanner.setThreshold(1e10);

		for (int i = 0; i < TOTAL_pNODES; i++)
			new pNode();

		for (int i = 0; i < TOTAL_sNODES; i++)
			new sNode();

		System.out.println("Allocated secondary nodes...");

		for (int i = 0; i < TOTAL_xNODES; i++)
			new ProxyNode();

		for (int i = 0; i < TOTAL_hNODES; i++)
			new HomebaseNode();

		for (int i = 0; i < TOTAL_mNODES; i++)
			new MobileNode();

		System.out.println("Allocated client nodes...");

		for (Node i : Node.nodes())
			i.init();

		GeoPosition mapCenter = new GeoPosition(38.738711, -9.139981);
		Globals.set("MapCenter", mapCenter);

		TileMapFrame tmf0 = new TileMapFrame("Lisbon-Map", mapCenter.getLatitude(), mapCenter.getLongitude(), 5, 13, Map.OSM, false);
		Gui.addDisplayable("Lisbon-Map", this, 10);
		Gui.setFrameRectangle("Lisbon-Map", 2, 2, 500, 500);

		carMap = new TileMapFrame("Car-Map", mapCenter.getLatitude(), mapCenter.getLongitude(), 3, 13, Map.OSM, false);
		Gui.addDisplayable("Car-Map", this, 20);
		Gui.setFrameRectangle("Car-Map", 505, 2, 500, 500);
		Gui.setDesktopSize("Meeds/Feeds Simulator", 1008, 502);

		try {
			osm = new OsmMapModel();
			osm.load("src/meeds/simsim/osm/Lisbon-old.osm").process(tmf0.map);
			Gui.addDisplayable("Lisbon-Map", osm, 1);
		} catch (Exception x) {
			x.printStackTrace();
		}
		super.setSimulationMaxTimeWarp(1000.0);
		new Task(5000) {
			@Override
			public void run() {
				setSimulationMaxTimeWarp(25.0);
			}
		};
		return this;
	}

	public static void main(String[] args) throws Exception {
		new MeedsSimulator().init().start();
	}

	final Pen pen = new Pen(RGB.BLACK, 1);

	// ------------------------------------------------------------------------------------------------------------------
	@Override
	public void displayOn(Canvas canvas) {
		pen.useStrokeOn(canvas.gs);
		for (Node i : Node.nodes())
			i.displayOn(canvas);
	}

	static {
		
//		Globals.set("Gui_ForceTitleBars", true);
		Globals.set("Net_Jitter", 0.0);
		Globals.set("Sim_RandomSeed", 4L);
		Globals.set("Net_RandomSeed", 4L);

		Globals.set("Net_FontSize", 18.0f);
		Globals.set("Net_Euclidean_NodeRadius", 15.0);
		Globals.set("Net_Euclidean_CostFactor", 0.00001);
		Globals.set("Net_Euclidean_DisplayNodeLabels", true);
		Globals.set("Net_Euclidean_MinimumNodeDistance", 500.0);

		Globals.set("Net_FontSize", 18.0f);
		Globals.set("Net_Euclidean_NodeRadius", 15.0);
		Globals.set("Net_Euclidean_CostFactor", 0.00005);
		Globals.set("Net_Euclidean_DisplayNodeLabels", true);
		Globals.set("Net_Euclidean_MinimumNodeDistance", 500.0);

		Globals.set("Traffic_DeadPacketHistory", 1.0);
		Globals.set("Traffic_DisplayDeadPackets", true);
		Globals.set("Traffic_DisplayDeadPacketsHistory", "time");
	}
}