package sensing.persistence.util

import java.util.List;

class MatrixOpsCategory {
	
	static List truncate(List self, int numLines) {
		return self[0..< Math.min(self.size(), numLines)]
	}
	
	static List copy(List self) {
		return self.collect{it.collect{it}}
	}
	
	static List sumLines(List self, List other) {
		List result = []
		self.eachWithIndex { List line, int lineNum ->
			List rLine = []
			line.eachWithIndex { val, int colNum ->
				rLine << (val + other[lineNum][colNum])
			}
			result << rLine
		}
		return result
	}
	
	static List sortLines(List self, Closure comparator = null) {
		self.each{ List line -> line.sort(comparator)}
		return self	
	}
	
	static List addAverageCol(List self, IntRange r) {
		self.each{ List line -> 
			List sub = line.getAt(r)
			line << (sub.sum()*1.0/(sub.size()))
		}
		return self
	}
	
	public static void saveGpd(List self, String fileName, List header) {
		File out = new File(fileName)
		out.write("") // clear contents
		header.each{out.append("# ${it}\n")} // output setup header
		self.size().times{ int line ->
			self[0].size().times{int col -> out.append("${self[line][col]}\t")}
			out.append("\n")
		}
	}
	
}
