package sensing.persistence.core.test;
import sensing.persistence.core.pipeline.*;

public class Main {

	static void helloWorld() {
		Pipeline p = new Pipeline();
		p << {String a -> "Hello ${a}"};
		p << {String a -> a.toUpperCase()};
		p << {println "Output: ${it}"; return null};
		p.run("World");
	}

	static void mixedPipe() {
		Pipeline p = new Pipeline();
		p << new IntProcessor([name: "A"]) << new IntProcessor([name: "B"]);
		p << {int a -> println "Filter ${a}"; (a > 300) ? a : null} 
		p << new IntProcessor([name: "C"]);
		p << {String a -> "Hello ${a}"};
		p << new StringProcessor([name: "D"]) ;
		p << {println "Output: ${it}"; return null}
		p.run(100);
		p.run("World!");
		p.run(200);		
	}

	static void speedSense1() {
		Pipeline p = new Pipeline();
		p << new SpeedSenseSelector() << {SpeedSenseReading r -> (r.speed >= 10 && r.speed <= 180) ?  r : null}

		p << {SpeedSenseReading r -> new SpeedSenseGridded(lat: r.lat, lon: r.lon, orientation: r.orientation, speed: r.speed, centroidLat: r.lat, centroidLon: r.lon)}
		/*p << new GroupedComponent({SpeedSenseReading r -> println "Group: ${r.centroidLat}, ${r.centroidLon}"; return r}).groupBy(["centroidLat", "centroidLon"]);*/

		p << new GroupedComponent(SpeedSenseFilter).groupBy(["centroidLat", "centroidLon"]);

		p << {SpeedSenseGridded r -> print "Output["
				r.properties.each { property -> print "${property.key}:${property.value} "};
				println "]";
				return r;
			}

		p << {Integer i -> println i};
		//p.setOutputListener({println it} as PipelineOutputListener);
		
		p.start();

	}

	
	public static void main(String[] args) {
		//helloWorld();
		speedSense1();
		Context c = new Context();
	}
}
