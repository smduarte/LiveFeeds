package sensing.persistence.core.pipeline;
import org.codehaus.groovy.runtime.InvokerHelper;
import java.awt.geom.Rectangle2D;
import sensing.persistence.core.pipeline.Tuple;
import static sensing.persistence.core.vtable.VTableDefinition.DATASRC;

class Aggregator extends Component {
	
	Tuple agResult;
	Tuple input;
	boolean queryExtent = false;
	double maxTimestamp = -1;
	int tupleCount = 0;
	Class outputClass;
	Closure aggregator;
	boolean canDispose;
	double winStart = Double.POSITIVE_INFINITY;
	double winEnd = 0;
	int gaLevel = 0;

	public Aggregator(Class outputClass, Closure aggregator) {
		this.aggregator = aggregator.clone();
		this.outputClass = outputClass;
	}

	public void init() {
		aggregator.delegate = this;
		aggregator.resolveStrategy = Closure.DELEGATE_FIRST
		initAgResult()
		canDispose = true;
	}
	
	public void reset() {
		initAgResult();
	}

//	def invokeMethod(String name, args) {
//		def metaClass = InvokerHelper.getMetaClass(this);
//		if(name == "process" && args[0] != EOS.instance) {
//			maxTimestamp = args[0].time > maxTimestamp ? args[0].time : maxTimestamp
//			canDispose = false
//			input = input ? input : args[0]; // first input 
//			Rectangle2D e = args[0].boundingBox //extent
//			Rectangle2D fe = input.boundingBox	// first extent
//			// use query spatial extent if input extents aren't consistent
//			queryExtent = !(e && fe) ||  !(e.x == fe.x && e.y == fe.y && e.width == fe.width && e.height == fe.height)
//		}
//		return metaClass.invokeMethod(this, name, args);	
//	}
	
	public void input(Tuple t) {
//		if(t.debugInfo) {
//			agResult.debugInfo.addAll(t.debugInfo)
//		} else {
//			agResult.debugInfo << t
//		}
		tupleCount++;
		maxTimestamp = t.time > maxTimestamp ? t.time : maxTimestamp
		winStart = Math.min(winStart, t.winStart ?: t.time)
		winEnd = Math.max(winEnd, t.winEnd ?: t.time)
		gaLevel = t.gaLevel > gaLevel ? t.gaLevel : gaLevel
		canDispose = false
		input = input ? input : t; // first input 
		Rectangle2D e = t.boundingBox //extent
		Rectangle2D fe = input.boundingBox	// first extent
		// use query spatial extent if input extents aren't consistent
		queryExtent = !(e && fe) ||  !(e.x == fe.x && e.y == fe.y && e.width == fe.width && e.height == fe.height);
		aggregator.call(t)
	}
	
	
	protected void sum(input, String inAttr, String outAttr) {
		if(!input) {
			agResult.setProperty(outAttr,0);

		} else {
			agResult.setProperty(outAttr, agResult.getProperty(outAttr) + input.getProperty(inAttr));
		}
	}
	
	protected void count(input, String outAttr) {
		if(!input) { 
			agResult.setProperty(outAttr,0);
		} else {
			agResult.setProperty(outAttr, agResult.getProperty(outAttr)+1)
		}
	}

	
	protected def partSum = [:]
	protected def partCount = [:]
	
	protected void avg(input, String inAttr, String outAttr) {
		if(!input) {
			agResult.setProperty(outAttr,Double.NaN);
			partSum[outAttr] = 0
			partCount[outAttr] = 0
		} else {
			partSum[outAttr]  += input.getProperty(inAttr);
			partCount[outAttr] += 1
			agResult.setProperty(outAttr,(double)partSum[outAttr]  / partCount[outAttr])
		}	
	}
	
	protected void avg(input, String inSumAttr, String inCountAttr, String outAttr) {
		if(!input) {
			agResult.setProperty(outAttr,Double.NaN);
			partSum[outAttr] = 0
			partCount[outAttr] = 0
		} else {
			partSum[outAttr]  += input.getProperty(inSumAttr);
			partCount[outAttr] += input.getProperty(inCountAttr);
			agResult.setProperty(outAttr, (double)(partSum[outAttr]) / partCount[outAttr]);
			agResult.setProperty(inSumAttr, partSum[outAttr]);
			agResult.setProperty(inCountAttr, partCount[outAttr]);
		}
	}

	protected Map m0 = [:]
	protected Map m1 = [:]
	protected Map s = [:]
	
	protected void std(input, String inAttr, String outAttr) {
		if(!input) {
			agResult.setProperty(outAttr,Double.NaN);
			m0[inAttr] = 0
			m1[inAttr] = 0
			s[inAttr] = 0
		} else {
			double v = input.getProperty(inAttr)
			double _m0, _m1, _s = 0
			if(tupleCount == 1) {
				_m1 = v
				_s = 0
			} else {
				_m0 = m1[inAttr]
				_m1 = _m0 + (v-_m0)/tupleCount
				_s = s[inAttr]
				_s = _s + (v-_m0)*(v-_m1)
				agResult.setProperty(outAttr,Math.sqrt(_s/(tupleCount-1)))
			}
			m0[inAttr] = _m0
			m1[inAttr] = _m1
			s[inAttr] = _s
		}
	}
	
	protected void min(input, String inAttr, String outAttr) {
		if(input == null) return
		if(tupleCount == 1) {
			agResult.setProperty(outAttr, input.getProperty(inAttr))
		} else {
			def min = agResult.getProperty(outAttr)
			def val = input.getProperty(inAttr)
			if(val < min) {
				agResult.setProperty(outAttr, val)
			}
		}
	}
	
	protected void max(input, String inAttr, String outAttr) {
		if(input == null) return
		if(tupleCount == 1) {
			agResult.setProperty(outAttr, input.getProperty(inAttr))
		} else {
			def max = agResult.getProperty(outAttr)
			def val = input.getProperty(inAttr)
			if(val > max) {
				agResult.setProperty(outAttr, val)
			}
		}
	}
	
	
	protected void initAgResult() {
		agResult = outputClass.newInstance();
		// initialize output attributes by calling aggregation closure with null input
//		Closure cAg = aggregator.clone();
//		cAg.delegate = this;
//		cAg.resolveStrategy = Closure.DELEGATE_FIRST
//		cAg.call(null);	
//		agResult.debugInfo = [];
		aggregator.call(null)
	}
	
	
	public input(EOS eos) {
		Tuple out = null
		if(input) { // output is possible only if at least one input occurred - in order to copy invariants
			out = agResult;
			pipeline.invariant?.each{ attr ->	
				try {
					out.setProperty(attr, input.getProperty(attr));
				} catch(MissingPropertyException e) { 
				}
			}
			if(queryExtent) {
				out.boundingBox = querycontext.query.aoi;
			} else {
				out.boundingBox = input.boundingBox			
			}
			out.time = maxTimestamp
			out.winStart = winStart
			out.winEnd = winEnd
			out.peerId = services.network.local.id
			out.level = pipeline.querycontext.level;
			if(pipeline.querycontext.context != DATASRC) {
				out.gaLevel = gaLevel+1
			}
			// 
			forward(out)
			initAgResult()
		}
		input = null;
		queryExtent = false;
		canDispose = true;
		maxTimestamp = -1;
		tupleCount = 0;
		winStart = Double.POSITIVE_INFINITY
		winEnd = 0;
		gaLevel = 0;
		forward(eos);
	}
	
	public boolean canDispose() {
		return canDispose
	}

}
