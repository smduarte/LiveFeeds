package simsim.core;

import static simsim.core.Simulation.Gui;
import static simsim.core.Simulation.Scheduler;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;

/**
 * The core engine of the simulation engine. The Scheduler manages a priority
 * queue of tasks, issuing them when simulation time reaches their time of
 * execution.
 * 
 * New tasks and re-scheduled tasks are inserted into the priority queue and
 * sorted according to the task execution deadlines. In each iteration, the
 * scheduler picks the next task to execute and advances simulation time
 * accordingly. Therefore, simulation time advances in discrete steps and is
 * decoupled from real time. Depending on the number of tasks in the queue and
 * the time spent in their execution, simulation time can run faster or
 * slower than real time.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 */
public class Scheduler extends simsim.scheduler.VT_Scheduler<Task> implements Displayable {

	Displayable time, tasks, threads ;
	
	protected Scheduler() {
		Scheduler = this ;
		time = new TimeDisplay() ;
		tasks = new TasksDisplay() ;
		threads = new ThreadsDisplay() ;
	}
	
	
	protected void mainLoop() {
		int n = 0 ;
		while (! queue.isEmpty() && !stopped) {
			processNextTask() ;
			if( n++ % 10 == 0 ) 
				Gui.redraw() ;
		}		
		stopped = true ;
	}	
	
	class TasksDisplay implements Displayable {
		public void displayOn( Canvas canvas ) {
			final double R = 500 ;
			for( simsim.scheduler.Task i : queue ) {
				Task t = (Task)i ;
				double q = 100*(t.due - now) ;			
				double r = R * Math.exp( -0.1*q );
				double x = r * Math.sin(q) ;
				double y = r * Math.cos(q) ;
				canvas.gs.setColor( t.color ) ;
				canvas.sFill( new Circle( 500+x, 500-y, 8.0) ) ;
			}
		}
	}
	
	class TimeDisplay implements Displayable {
		final Pen pen = new Pen( RGB.GRAY ) ;
		public void displayOn( Canvas canvas) {	
			double rt = (System.nanoTime() - rt0) * 1e-9 ;
			String time = String.format("Time:%.2fh, %.2fs / %.1fs (%.1fx) : %d tasks", now/3600, now, rt, now / rt, queue.size());	
			canvas.uDraw( pen, time, 1, 16);
		}
	}

	class ThreadsDisplay implements Displayable {
		final Pen pen = new Pen( RGB.GRAY ) ;
		public void displayOn( Canvas canvas) {	
			canvas.uDraw( pen, threadManager.toString(), 1, 32) ;
		}
	}

	public void displayOn( Canvas canvas ) {
		time.displayOn( canvas ) ;
		tasks.displayOn( canvas ) ;
		threads.displayOn( canvas ) ;
	}
	
	public void sleep( double s ) {
		new Task( s ) {
			public void run() {
				this.release() ;
			}
		}.block() ;
	}
}
