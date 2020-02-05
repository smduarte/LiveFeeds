package sensing.simsim.sys.msg;

import sensing.core.network.*;
import sensing.core.query.*;
import sensing.core.sensors.SensorData;

import static sensing.simsim.sys.PipelineSimulation.display;
import simsim.core.*;
import simsim.gui.canvas.*;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Line;

public class PipelineSimMessage extends simsim.core.Message {

	UUID srcId;
	UUID dstId;
	NetworkMessage msg;

	Pen msgPen;

	public PipelineSimMessage(UUID srcId, NetworkMessage msg, UUID dstId) {
		super( true, RGB.RED ) ;
		if(msg.payload instanceof QueryData) {
			setColor(RGB.GREEN);
		} else 	if(msg.payload instanceof QueryResult) {
			setColor(RGB.BLUE);
		} else 	if(msg.payload instanceof QueryControl) {
			setColor(RGB.BLACK);
		} else if(msg.payload instanceof SensorData) {
			setColor(RGB.GRAY);
		}
		this.srcId = srcId;
		this.msg = msg;
		this.dstId = dstId;
		msgPen = new Pen(color,2);
	}

	public void deliverTo( EndPoint src, MessageHandler handler ) {
		handler.onReceive( src, this ) ;
	}
	
    protected static final double PHI = Math.toRadians(20);
	public void displayOn( Canvas canvas, EndPoint src, EndPoint dst,  double t, double p) {	
		if(display.messages) { // && msg.payload instanceof QueryData && msg.payload.query.vtableName == "sensing.persistence.core.monitoring.VTworkLoad"
		//if(display.messages && !(msg.payload instanceof SensorData)) {
			Peer srcP = NetworkService.getPeer(srcId);
			Peer dstP =  NetworkService.getPeer(dstId);
			Line l = new Line( srcP.pos, dstP.pos);
		    canvas.sDraw(msgPen, l ) ;
		    
		    // show direction arrow
		    double ar = Math.atan2(dstP.pos.y-srcP.pos.y, dstP.pos.x-srcP.pos.x) + Math.PI;
		    canvas.sDraw(msgPen, new Line(dstP.pos.x, dstP.pos.y, (dstP.pos.x + 10*Math.cos(ar-PHI)), (dstP.pos.y + 10*Math.sin(ar-PHI))));
		    canvas.sDraw(msgPen, new Line((dstP.pos.x + 10*Math.cos(ar+PHI)), (dstP.pos.y + 10*Math.sin(ar+PHI)), dstP.pos.x, dstP.pos.y));
            
		}
	}
}
