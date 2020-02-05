package livefeeds.twister7.gui;

import static livefeeds.twister7.config.Config.Config;
import livefeeds.twister7.CatadupaNode;
import livefeeds.twister7.GlobalDB;
import livefeeds.twister7.View;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;

public class MainDisplay implements Displayable {


	final Pen pen = new Pen(RGB.BLACK, 1);

	public void displayOn(Canvas canvas) {

		pen.useOn(canvas.gs);

		double ON = 0;
		for (CatadupaNode i : GlobalDB.liveNodes()) {
			if (i.isOnline())
				ON++;
		}
		double DN = GlobalDB.size();

		double JN = 0, WN = 0, LF = 0;
		for (CatadupaNode i : GlobalDB.liveNodes()) {
			if (i.isOnline()) {
				JN += (i.state.joined ? 1 : 0);
				WN += (!i.state.joined && i.state.db.loadedEndpoints ? 1 : 0);
				LF += (i.state.db.loadedEndpoints && !i.state.db.loadedFilters? 1 : 0) ;
			}
		}

		canvas.uDraw(String.format("ON: %4.0f (%4.0f)", ON, DN), 2, 48);
		canvas.uDraw(String.format("JN: %4.0f + WN: %4.0f = %4.0f (%4.0f) (LF=%4.0f%%)", JN, WN, JN + WN, ON, 100*LF/ON), 2, 68);

		View.GV.trim(Config.VIEW_CUTOFF);
		for (CatadupaNode i : GlobalDB.liveNodes()) {
			if (i.isOnline())
				i.displayOn(canvas);
		}
	}
}