package sensing.persistence.simsim.speedsense.osm.setup.hotspots.charts;

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
	boolean display;

	public ErrorChart( boolean display=true) {
		this.results = ResultsPerSlot.instance
		this.display = display;
		if(display) {
			chart = new XYLineChart("Error", 1.0, "Error (%)", "Time slot");
			chart.setYRange( false, 0, 100 ) ;
			chart.setSeriesLinesAndShapes("Error", true, true) ;
			OSMSpeedSenseSim.Gui.setFrameRectangle("Error", 484, 0, 480, 480);	
		}
	}
	
	
	public void init() {
		output = openOutputFile("ErrorRate.csv") { writer ->
			writer << "Result\n";
			(results.detections.size()).times {
				writer << "${it*OSMSpeedSenseSim.setup.SIM_UPDATE_INTERVAL},";
			}
			writer << "TOTAL_DETECTED, TOTAL_FALSE_NEG, TOTA_FALSE_POS, TOTAL_ERROR\n";
		}
		def outWriter = { writer ->
                        writer << "Result\n";
                        (results.detections.size()).times {
                                writer << "${it*OSMSpeedSenseSim.setup.SIM_UPDATE_INTERVAL},";
                        }
                        writer << "TOTAL\n";
                }

                outputDet = openOutputFile("Detections.csv", outWriter) 
                outputFN = openOutputFile("FalseNegative.csv", outWriter)
                outputFP = openOutputFile("FalsePositive.csv", outWriter)
	}
		
	public void stop() {
		if(results.slot < 0) return
		update()
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
		output.append("${results.total(results.detections)},");
		output.append("${results.total(results.falseNegative)},");
		output.append("${results.total(results.falsePositive)},");
		output.append("${results.total(results.falsePositive)+results.total(results.falseNegative)}\n");
	 	outputDet.append("${results.total(results.detections)}\n");
		outputFN.append("${results.total(results.falseNegative)}\n");
		outputFP.append("${results.total(results.falsePositive)}\n");	
	}
	
	public void update() {
		if(results.slot < 0) return

			def series;
			if(display) {
				series = chart.getSeries("Error");
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
					series.add(i*OSMSpeedSenseSim.setup.SIM_UPDATE_INTERVAL, val);
				}
			}
	}
}
