import static groovy.io.FileType.*
String root = "/Users/heitor2/Documents/UNL MEI/60 Artigos Conferncias/results"
File rootF = new File(root)
String runId = "run_qtree-noerror"
HashMap metrics = [messages: "Messages_Cumulative"]

int maxSims = 3
HashMap setups = [:]


rootF.eachFileMatch(DIRECTORIES, ~/sensing.persistence.simsim.speedsense.map.setup.*/) { File setup -> 
	if(!setups[setup.name]) setups[setup.name] = [head: null, trees: [:]]
	setup.eachFileMatch(DIRECTORIES,  ~/$runId.*/) { File run ->
		run.eachFile(DIRECTORIES) { File tree ->
			if(!setups[setup.name].trees[tree.name]) setups[setup.name].trees[tree.name] = [:]
			metrics.each{ key, fName ->
				if(!setups[setup.name].trees[tree.name][key]) setups[setup.name].trees[tree.name][key] = [:]
				tree.eachFileMatch(FILES, ~/$fName\.csv/) { data ->
					println data.absolutePath
					int start;
					List lines = []
					data.eachLine() {String line, int n ->
						if(line.startsWith("Result")) {start = n};
						lines << line
					}
	
					if(start < lines.size() - 1) {
						if(!setups[setup.name].trees[tree.name][key].head) {
							setups[setup.name].trees[tree.name][key].head = lines[start].tokenize(",")
							setups[setup.name].trees[tree.name][key].lines = []
						}
						if(!setups[setup.name].head) setups[setup.name].head = lines[0..start-2];
						lines[start+1..lines.size()-1].each{ line ->
							setups[setup.name].trees[tree.name][key].lines << line.tokenize(",").collect{Double.parseDouble(it)}
						}
					}
				}
			}
		}
	}
}

def output(Closure c) {
	
}

setups.each{ String setup, HashMap setupData->
	setupData.trees.each { String tree, HashMap dataSet ->
		if(dataSet.messages) {
			File out = new File("${root}/${runId}_${tree.tokenize(".").last()}_${setup.tokenize(".").last()}_messages.gpd")
			out.write("")
			println out.absolutePath
			setupData.head.each{out.append("# ${it}\n")}
			//TODO assume que todas as linhas tem o mesmo numero de valores
			dataSet.messages.lines[0].size().times{ int col ->
				out.append("${dataSet.messages.head[col]}\t");
				Math.min(maxSims,dataSet.messages.lines.size()).times{int line -> 
					 out.append("${dataSet.messages.lines[line][col]}\t")
				}
				out.append("\n")
			}
		}
	}
}
