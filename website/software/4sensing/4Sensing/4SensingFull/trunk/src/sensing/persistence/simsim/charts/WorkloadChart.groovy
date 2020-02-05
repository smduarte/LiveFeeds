package sensing.persistence.simsim.charts;

import sensing.persistence.core.network.PeerDB;
import sensing.persistence.simsim.*;
import simsim.gui.charts.XYLineChart;

class WorkloadChart extends Metric {
	final  XYLineChart chart;
	def workload;
	int totalNodes;
	File output;
	Closure indicator;
	Closure slicer;
	String indName;
	int numSlices;
	boolean display;

	

	public WorkloadChart(Closure indicator, String indName, Closure slicer, int numSlices, boolean display=true) {
		this.indicator = indicator;
		this.slicer = slicer;
		this.numSlices = numSlices;
		this.indName = indName;
		this.display = display;
		if(display) {
			chart = new XYLineChart("Workload ${indName}" , 1.0, "Work (%)", "Nodes (%)") ;
			//chart.setYRange( false, 0, 100 ) ;
			chart.setXRange( false, 0, 100 ) ;
			chart.setSeriesLinesAndShapes("Load", true, true) ;
			OSMSpeedSenseSim.Gui.setFrameRectangle("Workload ${indName}", 484, 0, 480, 480);	
		}
	}
	
	public void init() {
		output = openOutputFile("Workload_${indName}.csv") { writer ->
			writer << "Result\n${100.0/numSlices}";
			(numSlices-1).times { 
				writer << ",${(it+2)*(100.0/numSlices)}";
			}
			writer << ",TOTAL_NODES\n";	
		}		
	
	}

	
	public void update(boolean force = false) {
		if(!display && !force) return	
		def series = null;
		if(chart) {
			series = chart.getSeries("Load");
			series.clear();
		}


		def nodeVals = PeerDB.peersList.inject([], indicator)
//		if(display) println nodeVals;
		def total  = nodeVals.sum();
		totalNodes = nodeVals.size();
		nodeVals.sort{a,b -> b.compareTo(a)}; //sort in descending order

		int nNodesSlice = slicer(nodeVals);
		
		if(nNodesSlice == 0 || total == 0 || totalNodes == 0) return;
		double percentSlice = 100.0/numSlices;

		int startIdx = 0;
		workload = [];
		numSlices.times {
			def endIdx = Math.min(totalNodes-1,(startIdx+nNodesSlice-1));
			if(totalNodes-1-endIdx<nNodesSlice) endIdx = totalNodes-1;
			double sliceTotal = nodeVals[startIdx..endIdx].sum();
			int sliceNodes = endIdx-startIdx+1;
			//series.add((it+1)*percentSlice, sliceTotal*100.0/total);
			if(series) series.add((it+1)*percentSlice, sliceTotal/sliceNodes);
			workload << sliceTotal/sliceNodes;
			startIdx = endIdx+1;
		}
	}
	
	public void stop() {
		update(true);
		if(workload) {
			output.append(workload.first());
			workload.tail().each{
				output.append(",${Double.toString(it)}");
			}
			output.append(",${totalNodes}\n");
		}
	}
}
