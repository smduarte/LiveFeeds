package apps;

import java.awt.geom.Rectangle2D;

public class Grid {
	
	public static getCell(Rectangle2D area,coords, int level) {
		if(level == 0) return [bounds: area, id: "0"] 
		int nCells = Math.pow(2,level);
		double cellW = area.width/nCells
		double cellH = area.height/nCells
		int cellX = (int)((coords.lon-area.x)/cellW)
		int cellY = (int)((coords.lat-area.y)/cellH)

		Rectangle2D cell =  new Rectangle2D.Double(cellX*cellW+area.x, cellY*cellH+area.y, cellW, cellH);
		return [boundingBox: cell, cellId: "${level}_${cellX}_${cellY}"]
	}

	public static getCentroid(Rectangle2D area) {
		 return [lat: (area.y + area.height/2), lon:(area.x + area.width/2)]
	}
}
