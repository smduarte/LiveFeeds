package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts.mq
;

import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import simsim.gui.charts.XYLineChart;
import speedsense.Hotspot;

class ErrorChart extends Metric {
	final  XYLineChart chart;
	File output;
	File outputDet;
	File outputFN;
	File outputFP;
	ResultsPerSlot results;

	public ErrorChart(ResultsPerSlot results) {
		this.results = results;
	
		chart = new XYLineChart("Error", 1.0, "Num Errors", "Time slot");
		chart.setYRange( false, 0, 100 ) ;
		chart.setSeriesLinesAndShapes("Error", true, true) ;
		OSMSpeedSenseSim.Gui.setFrameRectangle("Error", 484, 0, 480, 480);	
	}
	
	protected int sum(array) {
		return 	array.inject(0){sum, val -> sum+val};
	}
	
	public void init() {
		output = openOutputFile("ErrorRate.csv") { writer ->
			writer << "Result\n";
			(results.detections.size()).times {
				writer << "${it},";
			}
			writer << "TOTAL_DETECTED, TOTAL_FALSE_NEG, TOTA_FALSE_POS, TOTAL_ERROR\n";
		}
		def outWriter = { writer ->
                        writer << "Result\n";
                        (results.detections.size()).times {
                                writer << "${it},";
                        }
                        writer << "TOTAL\n";
                }

                outputDet = openOutputFile("Detections.csv", outWriter) 
                outputFN = openOutputFile("FalseNegative.csv", outWriter)
                outputFP = openOutputFile("FalsePositive.csv", outWriter)
	}
		
	public void stop() {
		if(results.slot < 0) return
		(results.detections.size()).times { i ->
			double error = results.falseNegative[i] + results.falsePositive[i];
			double events = error + results.detections[i];
			double val;
			if(events > 0) {
				val =  error*1.0/events*100;
			} else {
				val = 0;
			}
			output.append("${val},")
			outputDet.append("${results.detections[i]},");
			outputFN.append("${results.falseNegative[i]},");
			outputFP.append("${results.falsePositive[i]},");
		}
		output.append("${sum(results.detections)},");
		output.append("${sum(results.falseNegative)},");
		output.append("${sum(results.falsePositive)},");
		output.append("${sum(results.falsePositive)+sum(results.falseNegative)}\n");
	 	outputDet.append("${sum(results.detections)}\n");
		outputFN.append("${sum(results.falseNegative)}\n");
		outputFP.append("${sum(results.falsePositive)}\n");	
	}
	
	public void update() {
		if(results.slot < 0) return
		def series = chart.getSeries("Error");
		series.clear();
		for(int i=0; i<=results.slot; i++) {
			int error = results.falseNegative[i] + results.falsePositive[i];
			int events = error + results.detections[i];
			double val;
			if(events > 0) {
				val =  error*1.0/events*100;
			} else {
				val = 0;
			}
			series.add(i, val);
		}
		if((int)(OSMSpeedSenseSim.currentTime()+0.5) % 10 == 0) {
			println "detections: ${sum(results.detections)} false-positive: ${sum(results.falsePositive)} false-negative: ${sum(results.falseNegative)} total-error: ${sum(results.falsePositive)+sum(results.falseNegative)}"
		}
	}
}
