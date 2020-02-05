package sensing.persistence.simsim;

import simsim.gui.charts.XYLineChart;

public class Metric {
	
	public void init() {}
	public void cleanup() {}
	public void update() {}
	public void output() {}
	
	public void stop() {
		output();
	}
	
	protected XYLineChart createChart(params) {
		XYLineChart chart = new XYLineChart(params.title, 1.0/PipelineSimulation.setup.SIM_UPDATE_INTERVAL, params.yAxis, params.xAxis)
		if(params.lines || params.shapes) {
			chart.setSeriesLinesAndShapes(params.yAxis, params.lines ?: false, params.shapes ?: false)
		}
		PipelineSimulation.Gui.setFrameRectangle(params.title, 484, 0, 400, 400)
		return chart
	}
	

	protected createOutputFile(String fileName, Closure header=null) {
		File outdir = new File(PipelineSimulation.setup.getOutputBasePath());
		outdir.mkdirs();
		File f = new File(PipelineSimulation.setup.getOutputBasePath()+fileName);
		f.write('#Setup\n');
		PipelineSimulation.setup.getConfigKeys().each{ key ->
			f.append("#${key}, ${PipelineSimulation.setup.getProperty(key)}\n")
		}
		if(header) f.withWriterAppend(header);
		return f;
	}
	
	
//	protected File openOutputFile(String fileName, Closure header=null) {
//		def f = new File(PipelineSimulation.setup.getOutputBasePath()+fileName);
//		if(f.exists()) {
//			return f;
//		} else {
//			return createOutputFileImpl(f, header);
//		}
//	}
	
	protected File openOutputFile(String fileName, List header) {
		if(PipelineSimulation.setup.OUTPUT_INTERVAL) {
			openOutputFile("HISTORY_"  + fileName, { writer ->
				writer << "#" + ["TIME", *header].join(",") + "\n";
			});
		} else {
			openOutputFile(fileName, { writer ->
				writer << "#" + (header).join(",") + "\n";
			})
		}
	}
	
	protected writeToFile(String fileName, List line, List header) {
		if(PipelineSimulation.setup.OUTPUT_INTERVAL)  {
			String sLine = [(int)(PipelineSimulation.currentTime()), *line].join(",") + "\n";
			createOutputFile("SNAPSHOT_$fileName", { writer ->
				writer << "#" + ["TIME", *header].join(",") + "\n";
			}).append(sLine);
			openOutputFile("HISTORY_$fileName").append(sLine);
		} else {
			openOutputFile(fileName).append(line.join(",") + "\n");
		}
	}
	
	protected boolean outputFileExists(String fileName) {
		def output = new File(PipelineSimulation.setup.getOutputBasePath()+fileName);
		return output.exists();
	}
	
	/*
	* Multiple file output (one file per sim)
	*/
	
	File createSimOutputFile(String fileName, Closure header) {
		def (basename, extention) = fileName.split("\\.");
		return createOutputFile("${basename}_${PipelineSimulation.simId}.${extention}", header)
	}

}
