package simsim.gui;

import static simsim.core.Simulation.Scheduler;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import simsim.core.Displayable;
import simsim.core.Globals;
import simsim.gui.geom.XY;
import simsim.utils.Threading;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 */
@SuppressWarnings("serial")
public class GuiDesktop extends javax.swing.JDesktopPane implements Runnable, Gui {

	private Component parent;
	static GuiDesktop gd;

	private XY mouse = new XY(-1, -1);
	private volatile boolean needsRedraw = false;

	private Dimension desktopSize = new Dimension(1024, 768);

	private Map<String, IFrame> frames = new HashMap<String, IFrame>();
	private Map<String, IPanel> panels = new HashMap<String, IPanel>();

	public GuiDesktop(Component dad) {
		gd = this;
		parent = dad;
		this.setPreferredSize(desktopSize);

		super.setDesktopManager(new DefaultDesktopManager() {
			public void activateFrame(JInternalFrame f) {
				if (f != null)
					super.activateFrame(f);
			}

			// Control drag to force internal frames to appear inside the
			// desktop
			public void dragFrame(JComponent f, int x, int y) {
				// Only internal frames
				if (f instanceof JInternalFrame) {
					Dimension d = getSize();
					Dimension r = f.getSize();
					Insets s = f.getInsets();
					x = Math.max(0 - s.left, x);
					x = Math.min(x, d.width - r.width + s.right);
					y = Math.max(0, y);
					y = Math.min(y, d.height - r.height + s.bottom);
				}
				super.dragFrame(f, x, y - 3);
			}
		});
	}

	public void init() {
		Threading.newThread(this, true).start();
	}

	public XY getMouseXY() {
		return mouse;
	}

	TileMapPanel createMapFrame(String frame, double fps, boolean titleBar) {
		return panel(frame, fps, true, titleBar && !Gui_NoTitleBars || Gui_ForceTitleBars).mapPanel();
	}

	public void addDisplayable(String frame, Displayable d, double fps, boolean titleBar) {
		if (d != null) {
			panel(frame, fps, false, titleBar && !Gui_NoTitleBars || Gui_ForceTitleBars).addDisplayable(d);
		}
	}

	public void addInputHandler(String frame, InputHandler h) {
		if (h != null) {
			panel(frame, 10, false, false).addInputHandler(h);
		}
	}

	private IPanel panel(String name, double fps, boolean isMap, boolean inFrame ) {
		IPanel p = panels.get(name);
		if (p == null) {
			p = new IPanel(this, name, fps, isMap);
			panels.put(name, p);
			if( inFrame ) { 
				IFrame f = new IFrame(name, p) ;
				frames.put( name, f ) ;
				super.add(f, javax.swing.JLayeredPane.DEFAULT_LAYER);
			} 
			else
				super.add(p, javax.swing.JLayeredPane.DEFAULT_LAYER);
		}
		return p;
	}

	
	public void setFrameRectangle(String frame, int x, int y, int w, int h) {
		JComponent p = frames.get(frame);
		if( p == null ) 
			p = panels.get(frame) ;
		
		if (p != null) 
			if( p instanceof IPanel ) {
				p.setBounds(new Rectangle(x, y, w, h));				
			} else {
				Insets is = p.getInsets();
				p.setBounds(new Rectangle(x - is.left, y - 5, w + is.left + is.right, h + is.top + is.bottom));
			}
	}

	public void setFrameTransform(String frame, double virtualWidth, double virtualHeight, double offset, boolean keepRatios) {
		IPanel p = panels.get(frame);
		if (p != null) {
			p.setFrameTransform(virtualWidth, virtualHeight, offset, keepRatios);
		}
	}

	public void maximizeFrame(String frame) {
		final IFrame f = frames.get(frame);
		if (f != null) {
			try {
				f.moveToFront();
				f.setMaximum(true);
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}

	void setMousePosition(int x, int y) {
		mouse = new XY(x, y);
	}

	public void redraw() {
		if (needsRedraw) {
			synchronized (this) {
				double now = System.nanoTime() * 1e-9;
				for (IPanel i : panels.values())
					if (i.nextRedraw() < now)
						i.reDraw();

				needsRedraw = false;
				Threading.notifyOn(this);
			}
		}
	}

	public void run() {
		Threading.sleep(50);
		for (;;) {
			synchronized (this) {
				while (needsRedraw)
					Threading.waitOn(this);
			}

			double now = System.nanoTime() * 1e-9;
			double nextDeadline = now + 0.1;
			for (IPanel i : panels.values())
				nextDeadline = Math.min(nextDeadline, i.nextRedraw());

			int delay = (int) (1000 * (nextDeadline - now));
			if (delay > 0)
				Threading.sleep(delay);
			else
				needsRedraw = true;

			if (Scheduler.isStopped())
				redraw();
		}
	}

	public void setDesktopSize(int w, int h) {
		parent.setSize(desktopSize = new Dimension(w, h));
	}

	@Override
	public void setDesktopSize(String title, int w, int h) {
		parent.setSize(desktopSize = new Dimension(w, h));		
		if( parent instanceof JFrame) 
			((JFrame)parent).setTitle(title) ;
	}

	@Override
	public void addDisplayable(String frame, Displayable d, double fps) {
		addDisplayable(frame, d, fps, true);
	}

	private boolean Gui_NoTitleBars = Globals.get("Gui_NoTitleBars", false);
	private boolean Gui_ForceTitleBars = Globals.get("Gui_ForceTitleBars", false);
}
