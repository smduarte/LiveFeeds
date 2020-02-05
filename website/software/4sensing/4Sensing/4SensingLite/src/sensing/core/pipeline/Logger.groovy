package sensing.core.pipeline;

public class Logger extends Component {
	String name;
	int level;
	Closure filter;
	
	public Logger(String name, int level) {
		this.name = name;
		this.level = level;
		this.filter = filter;
	}

	public void input(Object input) {
		def properties = "";
		input.properties.each { prop, val ->
		    if(prop != "metaClass" && prop != "class") {
		    	properties <<= " ${prop}: ${val}";
			}
		}
		services.logging.log(level, this, name, "Received ${input.class.name} [${properties} ]");
		forward(input);	
	}
}
