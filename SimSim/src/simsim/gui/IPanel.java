package simsim.gui;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import simsim.core.Displayable;
import simsim.gui.geom.XY;

@SuppressWarnings("serial")
class IPanel extends JPanel {

	private ImgPanel panel;
	private double requestedFrameRate;

	IPanel(final GuiDesktop g, String title, double fps) {
		this(g, title, fps, false);
	}

	IPanel(final GuiDesktop g, String title, double fps, boolean isMap) {
		panel = isMap ? new TileMapPanel() : new ImgPanel();

		requestedFrameRate = Math.min(50.0, fps);
			
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));
		add(panel);

		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent evt) {
				g.setMousePosition(Integer.MIN_VALUE, Integer.MIN_VALUE);
			}

			@Override
			public void mouseClicked(MouseEvent evt) {
				XY pu = new XY(evt.getX(), evt.getY());
				panel.onMouseClick(evt.getButton(), pu, null);
			}
		});

		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent evt) {
				g.setMousePosition(evt.getX(), evt.getY());
				XY pu = new XY(evt.getX(), evt.getY());
				panel.onMouseDragged(evt.getButton(), pu, null);
			}

			@Override
			public void mouseMoved(MouseEvent evt) {
				g.setMousePosition(evt.getX(), evt.getY());

				XY pu = new XY(evt.getX(), evt.getY());
				panel.onMouseMove(pu, null);
			}
		});

		panel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				g.setMousePosition(e.getX(), e.getY());
				XY pu = new XY(e.getX(), e.getY());
				panel.mouseWheelMoved(pu, null, e.getUnitsToScroll());
			}
		});
		panel.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent evt) {
				forceRedraw();
			}
		});
		
		this.setVisible(true);
		super.doLayout();
	}

	void addDisplayable(Displayable d) {
		panel.addDisplayable(d);
	}

	void removeDisplayable(Displayable d) {
		panel.removeDisplayable(d);
	}

	void addInputHandler(InputHandler ih) {
		panel.addInputHandler(ih);
	}

	public void setBounds(Rectangle r) {
		super.setBounds(r);
		panel.setSize(r.getSize());
		this.invalidate();
	}

	void setFrameTransform(double virtualWidth, double virtualHeight, double offset, boolean keepRatios) {
		panel.setTransform(virtualWidth, virtualHeight, offset, keepRatios);
	}

	private void forceRedraw() {
		nextRedraw = 0;
	}

	public void reDraw() {
		double t0 = System.nanoTime() * 1e-9;
		panel.reDraw();
		double t1 = System.nanoTime() * 1e-9;
		avgRedrawDuration = 0.5 * avgRedrawDuration + 0.5 * (t1 - t0);
		double targetFrameRate = Math.min(requestedFrameRate, 0.75 / avgRedrawDuration);
		nextRedraw = t1 + 1.0 / targetFrameRate;
	}

	TileMapPanel mapPanel() {
		if (panel instanceof TileMapPanel)
			return (TileMapPanel) panel;
		else
			throw new RuntimeException("Frame name already used...");
	}

	double nextRedraw() {
		return nextRedraw;
	}

	private double nextRedraw = -1;
	private double avgRedrawDuration = 1.0;
}
