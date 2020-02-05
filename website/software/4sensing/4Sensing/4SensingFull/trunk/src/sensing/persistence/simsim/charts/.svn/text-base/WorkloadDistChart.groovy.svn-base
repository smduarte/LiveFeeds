package sensing.persistence.simsim.charts;

import sensing.persistence.simsim.*;
import simsim.gui.charts.XYLineChart;

@Singleton(lazy=true) class WorkloadDistChart extends Metric{
	final  XYLineChart chart;
	final int numBuckets;
	
	public WorkloadDistChart(int numBuckets) {
		this.numBuckets = numBuckets;
		chart = new XYLineChart("Workload Dist", 1.0, "Work", "Nodes (%)") ;
		chart.setYRange( false, 0, 100 ) ;
		chart.setSeriesLinesAndShapes("Load", true, true) ;
		OSMSpeedSenseSim.Gui.setFrameRectangle("Workload Dist", 484, 0, 480, 480);	
	}
	
	public void update() {
		def series = chart.getSeries("Load");
		series.clear();
		def processedTuples = [];
		int total = 0;
		OSMSpeedSenseSim.nodeList.each { node ->
			def p = node.getProcessCount();
			total+=p;
			if(p>0) {
				processedTuples << p;
			}
			
		};
		if(!processedTuples) return;
		int min = processedTuples.min();
		int max = processedTuples.max();
		chart.setXRange( false, min, max ) ;

		double bucketSize = (max-min)/numBuckets;
		def buckets = [0]*numBuckets;
		processedTuples.each{
			int bucket = Math.min(numBuckets-1, (int)((it-min)/bucketSize));
			//println "bucket: ${bucket}";
			buckets[bucket]++;
		}
		println buckets;
		println "min: ${min} max: ${max} avg:${total/processedTuples.size()}"; 
		double tY = 0;
		buckets.eachWithIndex{ bVal, bIdx ->
			double x = min + (bucketSize*bIdx) + bucketSize/2.0;
			double y = 100*bVal/processedTuples.size();
			series.add(x, y);
			printf("x:%.2f y:%.2f\n",x, 100*bVal/processedTuples.size());
			tY+=y;
		}
		println "totalY: ${tY}";
	}
}
