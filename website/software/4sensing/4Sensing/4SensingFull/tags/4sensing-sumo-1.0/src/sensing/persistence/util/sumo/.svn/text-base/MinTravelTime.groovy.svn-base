package sensing.persistence.util.sumo;
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;

def mapModel = new SUMOMapModel()
mapModel.load ("koeln_bbox_net.xml")

int numSegs = 0;
int lt5 = 0;
int lt10 = 0;

mapModel.getEdges().each { SUMOMapModel.Edge e ->
	if(e.length >= 200) {
		double mt = e.length / e.maxSpeed
		switch(mt) {
			case {it <= 5}:
				lt5++
			case {it <= 10}:
				lt10++; break	
		}
		numSegs++;	
	}
}

printf("%d segments\n", numSegs)
printf("<= 5s:  %.1f%%\n", lt5/numSegs*100)
printf("<= 10s: %.2f%%\n", lt10/numSegs*100)
