package livefeeds.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import simsim.utils.Threading;

public class BatchExec {
	String old_classpath = "/Users/smd/Desktop/GitProjects/LiveFeeds/bin:/Users/smd/Desktop/GitProjects/SimSim/bin:/Users/smd/Desktop/GitProjects/SimSimScheduler/bin:/Users/smd/Desktop/GitProjects/zLIBS/colt.jar:/Users/smd/Desktop/GitProjects/zLIBS/event-1.6.5.jar:/Users/smd/Desktop/GitProjects/zLIBS/gluegen-rt.jar:/Users/smd/Desktop/GitProjects/zLIBS/language-1.6.7.jar:/Users/smd/Desktop/GitProjects/zLIBS/logger-1.6.4.jar:/Users/smd/Desktop/GitProjects/zLIBS/interpreter-1.6.8.jar:/Users/smd/Desktop/GitProjects/zLIBS/iText-2.1.7.jar:/Users/smd/Desktop/GitProjects/zLIBS/jcommon-1.0.16.jar:/Users/smd/Desktop/GitProjects/zLIBS/jfreechart-1.0.13.jar:/Users/smd/Desktop/GitProjects/zLIBS/optimization.jar:/Users/smd/Desktop/GitProjects/zLIBS/Blas.jar:/Users/smd/Desktop/GitProjects/zLIBS/tcode.jar:/Users/smd/Desktop/GitProjects/zLIBS/ssj.jar:/Users/smd/Desktop/GitProjects/zLIBS/swing-worker.jar:/Users/smd/Desktop/GitProjects/zLIBS/swingx-bean.jar:/Users/smd/Desktop/GitProjects/zLIBS/swingx-ws-2009_06_14.jar:/Users/smd/Desktop/GitProjects/zLIBS/swingx.jar:/Users/smd/Desktop/GitProjects/zLIBS/vecmath.jar:/Users/smd/Desktop/GitProjects/zLIBS/xpp3_min-1.1.4c.jar:/Users/smd/Desktop/GitProjects/zLIBS/xstream-1.3.1.jar";	
	String classpath = "/Users/smd/Dropbox/workspace/LiveFeeds/bin:/Users/smd/Dropbox/workspace/SimSim/bin:/Users/smd/Dropbox/workspace/SimSimScheduler/bin:/Users/smd/Dropbox/workspace/zLIBS/colt.jar:/Users/smd/Dropbox/workspace/zLIBS/event-1.6.5.jar:/Users/smd/Dropbox/workspace/zLIBS/gluegen-rt.jar:/Users/smd/Dropbox/workspace/zLIBS/language-1.6.7.jar:/Users/smd/Dropbox/workspace/zLIBS/logger-1.6.4.jar:/Users/smd/Dropbox/workspace/zLIBS/interpreter-1.6.8.jar:/Users/smd/Dropbox/workspace/zLIBS/iText-2.1.7.jar:/Users/smd/Dropbox/workspace/zLIBS/jcommon-1.0.16.jar:/Users/smd/Dropbox/workspace/zLIBS/jfreechart-1.0.13.jar:/Users/smd/Dropbox/workspace/zLIBS/optimization.jar:/Users/smd/Dropbox/workspace/zLIBS/Blas.jar:/Users/smd/Dropbox/workspace/zLIBS/tcode.jar:/Users/smd/Dropbox/workspace/zLIBS/ssj.jar:/Users/smd/Dropbox/workspace/zLIBS/swing-worker.jar:/Users/smd/Dropbox/workspace/zLIBS/swingx-bean.jar:/Users/smd/Dropbox/workspace/zLIBS/swingx-ws-2009_06_14.jar:/Users/smd/Dropbox/workspace/zLIBS/swingx.jar:/Users/smd/Dropbox/workspace/zLIBS/vecmath.jar:/Users/smd/Dropbox/workspace/zLIBS/xpp3_min-1.1.4c.jar:/Users/smd/Dropbox/workspace/zLIBS/xstream-1.3.1.jar";
	String command = "/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/bin/java";

	List<Class<?>> classes;

	BatchExec(List<Class<?>> classes) throws Exception {
		this.classes = classes;

		System.out.println(classes);
		exec();
	}

	private List<ProcessBuilder> running = new ArrayList<ProcessBuilder>();

	public void exec() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;

		File binDir = new File("/tmp");
		Enumeration<URL> resources = classLoader.getResources("livefeeds");
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			binDir = new File(resource.getFile()).getParentFile();
			break;
		}
		File baseDir = binDir.getParentFile();

		int cores = Math.min(2, Runtime.getRuntime().availableProcessors() / 2);
		
		while( classes.size() > 0 || running.size() > 0 ) {
			synchronized( running ) {			
				while( running.size() >= cores )
					Threading.waitOn( running ) ;
				
				while( running.size() < cores && classes.size() > 0 ) {
					Class<?> next = classes.remove(0) ;
					running.add( execute( baseDir, next ) ) ;					
				}
			}
			
		}
		System.out.println("Finished...");

	}

	ProcessBuilder execute( final File baseDir, final Class<?> c) throws Exception {
	
		final ProcessBuilder pb = new ProcessBuilder(command, "-Xmx3000m", "-cp", classpath,  c.getName() );
		pb.directory(baseDir);
		pb.redirectErrorStream(true) ;
		try {
			Threading.newThread( new Runnable() {
				public void run() {
					try {
						System.out.println("Executing:" + c.getSimpleName() ) ;
						Process p = pb.start();
						InputStream in = p.getInputStream();
						FileOutputStream fos = new FileOutputStream("/tmp/" + c.getSimpleName() + ".out") ;
						int n;
						byte[] tmp = new byte[1024] ;
						while ((n = in.read(tmp)) != -1)
							fos.write(tmp, 0, n);
						
						p.waitFor() ;
						
					} catch (Exception x) {
						x.printStackTrace();
					}
					synchronized( running ) {
						running.remove( pb ) ;
						Threading.notifyAllOn( running ) ;						
					}
				}
			}, true).start();
		} catch (Exception x) {
			x.printStackTrace();
		}
		return pb ;
	}
	
	public static void main(String[] args) throws Exception {

		new BatchExec(getClasses("Catadupa_", "livefeeds.twister7.config"));
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	static List<Class<?>> getClasses(String prefix, String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);

		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		for (Iterator<Class<?>> i = classes.iterator(); i.hasNext();) {
			String sn = i.next().getSimpleName();
			if (!sn.startsWith(prefix))
				i.remove();
		}
		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}
}
