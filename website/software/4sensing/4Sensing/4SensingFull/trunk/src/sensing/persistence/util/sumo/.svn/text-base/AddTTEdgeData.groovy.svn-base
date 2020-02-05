package sensing.persistence.util.sumo
import  sensing.persistence.simsim.map.sumo.SUMOMapModel;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import groovy.xml.StreamingMarkupBuilder
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

//def mapModel = new SUMOMapModel()
//mapModel.load ("koeln_bbox_net.xml")
TravelTime tt = new TravelTime("sumocfg3_vtypeprobe_5s_nointernal.xml")

def fileInputStream = new FileInputStream('sumocfg3_meandata-edge_5m_nointernal.xml')
def bufferedInputStream = new BufferedInputStream(fileInputStream)

XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(fileInputStream)

def getAttributes = {
	def atts = [:]
	reader.attributeCount.times {
		atts[reader.getAttributeLocalName(it)] = reader.getAttributeValue(it)
	}
	atts
}

def builder = new StreamingMarkupBuilder() 

def proc = builder.bind {
	mkp.xmlDeclaration()
	netstats {
		while(reader.hasNext()) {
			int eventCode = reader.next()
			if(eventCode == XMLStreamReader.START_ELEMENT  && reader.getLocalName().equals("interval")) {
				def ia =  getAttributes()
				interval(ia) {
					println ia
					tt.readTo(ia.end.toDouble() - 5)
					while(reader.hasNext()) {
						eventCode = reader.next()
						if(eventCode == XMLStreamReader.START_ELEMENT  && reader.getLocalName().equals("edge")) {
							def ea = getAttributes()
							def data = tt.segmentData[ea.id]
							if(data) {
								ea.vpTravelTimeCount = data.size()
								ea.vpTravelTimeMin = data.min()
								ea.vpTravelTimeMax = data.max()
								double avg = data.sum()*1.0/data.size()
								ea.vpTravelTimeAvg = avg
								ea.vpTravelTimeStd = Math.sqrt(data.inject(0) {acc, val -> acc + (val-avg)*(val-avg)}/(data.size()))
							}
							if(ea.id.equals("47426157#0")) {
								println ea
							}
							edge(ea)
						} else if(eventCode == XMLStreamReader.END_ELEMENT  && reader.getLocalName().equals("interval")) {
							break;
						}
					}
					tt.segmentData.clear()
				} 
			} else if(eventCode == XMLStreamReader.END_ELEMENT  && reader.getLocalName().equals("netstats")) {
				break;
			}
		}
	}
} 

new File('sumocfg3_meandata-edge_5m_nointernal_tt.xml').withWriter{ it << proc }