package sensing.persistence.simsim.speedsense.sumo.setup
import sensing.persistence.simsim.Metric;
import java.io.PrintWriter;

class LineOutputMetric extends Metric {
	PrintWriter outWritter;
	String fileName;
	String header;
	
	LineOutputMetric(String fileName, String header) {
		this.header	  = header;
		this.fileName = fileName;
	}
	
	public void init() {
		File outFile = createSimOutputFile(fileName, {writer -> writer << header});
		outWritter = outFile.newWriter(true).newPrintWriter(); 
		//TODO: Header is overwritten
	}
	
	public void stop() {
		outWritter.flush();
		outWritter.close();	
	}
	
	public void newLine(List line) {
		outWritter.println(line.join("\t"));
	}
}
