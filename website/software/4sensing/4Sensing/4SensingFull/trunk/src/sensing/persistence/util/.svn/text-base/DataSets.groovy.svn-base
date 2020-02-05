package sensing.persistence.util

import java.util.List;
import static groovy.io.FileType.*

class DataSets {
	
	String 	root
	String 	runId
	HashMap metrics
	
	protected HashMap setups = [:]
	protected HashMap dataSets  = [:]
	
	
	public void load() {
		File rootF = new File(root)
		println "root: $root"
		rootF.eachFileMatch(DIRECTORIES, ~/sensing.persistence.simsim.speedsense.osm.setup.*/) { File setup ->
			if(!setups[setup.name]) setups[setup.name] = []
			setup.eachFileMatch(DIRECTORIES,  ~/$runId.*/) { File run ->
				run.eachFile(DIRECTORIES) { File tree ->					
					metrics.each{ metricName, fName ->	
						tree.eachFileMatch(FILES, ~/$fName\.csv/) { data ->
							println data.absolutePath
							int start = -1;
							List lines = []
							data.eachLine() {String line, int n ->
								if(line.startsWith("#Result")) {start = n};
								lines << line
							}
			
							if(start > -1 && start < lines.size() - 1) { // file has data
								if(!dataSets["${setup.name}/${tree.name}"]) dataSets["${setup.name}/${tree.name}"] = 
									[tree: tree.name, setup: setups[setup.name], data: [:]]
								HashMap dataSet = dataSets["${setup.name}/${tree.name}"];
								if(!dataSet.data[metricName]) {
									dataSet.data[metricName] = [lines[start].tokenize(",")] // first line is metric header
								} else if(dataSet.data[metricName][0] != lines[start].tokenize(",")){
									println "Warning: different metric headers"
									println "1] ${dataSet.data[metricName][0]}"
									println "2] ${lines[start].tokenize(",")}"
								}
								List setupHeader = lines[0..start-2]
								if(setups[setup.name].size() == 0) setups[setup.name].addAll(setupHeader)
									else if(setups[setup.name] != setupHeader) {
										def tmp = setups[setup.name] as Set
										def diff =tmp + setupHeader
										tmp.retainAll(setupHeader)
										diff.removeAll(tmp)
										println "Warning: different setup headers"
										diff.each{println it}
									}
								lines[start+1..lines.size()-1].each{ line ->
									dataSet.data[metricName] << line.tokenize(",")*.toDouble()
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	public void each(Closure c) {
		dataSets.each{String key, HashMap dataSet ->
			def (setupName, treeName) = key.tokenize("/")

			c.call(setupName, treeName, dataSet)	
		}
	}

}
