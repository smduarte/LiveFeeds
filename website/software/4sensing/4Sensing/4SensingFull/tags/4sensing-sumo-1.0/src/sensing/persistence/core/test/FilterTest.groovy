package sensing.persistence.core.test;


public class FilterTest {

	public static void main(String[] args) {
		def f = new Filter();
		
		def emc = new ExpandoMetaClass(Filter, false);
		emc.process = {int i -> increase()};
		emc.initialize();
		f.metaClass = emc;

		println "int: " + f.metaClass.respondsTo(f, "process", 10);
		println "string: " + f.metaClass.respondsTo(f, "process","hello");
		
		println f.process(25);
		println f.process(5);
	}

}
