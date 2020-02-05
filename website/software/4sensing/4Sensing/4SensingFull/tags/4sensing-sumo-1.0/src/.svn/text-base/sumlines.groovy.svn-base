//File input = new File("/Users/heitor2/Documents/UNL MEI/30 Tese/90 INForum/CommonSenseDir/4Sensing/results/sensing.persistence.simsim.speedsense.map.setup.hotspots.HSSetup2_50kNodes_5kfixed/run_rtree-error06-08-2010-00-37-41/sensing.persistence.simsim.speedsense.map.rndtree.continuous.RTSpeedSenseSim/Workload_Sensor_All_Nodes.csv");
File input = new File("/Users/heitor2/Documents/UNL MEI/30 Tese/90 INForum/CommonSenseDir/4Sensing/results/sensing.persistence.simsim.speedsense.map.setup.hotspots.HSSetup2_50kNodes_5kfixed/run_qtree-error06-08-2010-00-38-24/sensing.persistence.simsim.speedsense.map.qtree.continuous.QTCSpeedSenseSim/Workload_Sensor_All_Nodes.csv");

def sumline(line) {
    def fields = line.tokenize(",").collect{Double.parseDouble(it)};
    return fields.sum()
}

def lines = [];
int start = 23;

input.eachLine() {line, n ->
 if(line.equals("Result")) {start == n};
 lines << line;
}

lines[22..26].each{line ->
    println sumline(line)
};
println 1
