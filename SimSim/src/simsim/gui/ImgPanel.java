package simsim.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JPanel;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.geom.XY;

@SuppressWarnings("serial")
class ImgPanel extends JPanel implements InputHandler {

	private boolean keepRatios = true;
	private double v_width = 1000, v_height = 1000, offset = 0.2;
	private boolean isOverlay = false;

	ImgPanel() {
		this(false);
	}

	ImgPanel(boolean isOverlay) {
		super();
		this.isOverlay = isOverlay;
		this.buffers = new VImage[2];
		for (int i = 0; i < buffers.length; i++)
			buffers[i] = new VImage(this);

		this.displayables = new ArrayList<Displayable>();
		this.inputHandlers = new HashSet<InputHandler>();
	}

	public void reDraw() {
		this.renderDisplayables(canvas());
		this.swapBuffers();
	}

	public void addDisplayable(Displayable d) {
		if (d != null) {
			displayables.add(d);
			if (d instanceof InputHandler)
				inputHandlers.add((InputHandler) d);
		}
	}

	public void removeDisplayable(Displayable d) {
		if (d != null) {
			displayables.remove(d);
			if (d instanceof InputHandler)
				inputHandlers.remove((InputHandler) d);
		}
	}

	public void addInputHandler(InputHandler h) {
		if (h != null) {
			inputHandlers.add(h);
		}
	}

	public void renderDisplayables(Canvas canvas) {
		for (Displayable i : displayables) {
			i.displayOn(canvas);
		}
	}

	public void setTransform(double vWidth, double vHeight, double offset,
			boolean keepRatios) {
		this.v_width = vWidth;
		this.v_height = vHeight;
		this.offset = offset;
		this.keepRatios = keepRatios;

		for (VImage i : buffers)
			i.setTransform();
	}

	public void paintComponent(Graphics g) {
		backImage().drawTo((Graphics2D) g);
	}

	protected void swapBuffers() {
		cb = (cb + 1) % 2;
		repaint();
	}

	protected Canvas canvas() {
		buffers[cb].validate().clear();
		return buffers[cb].canvas;
	}

	protected VImage backImage() {
		return buffers[(cb + 1) % 2].validate();
	}

	int cb = 0;
	VImage[] buffers;

	class VImage {

		Canvas canvas;
		ImgPanel panel;
		BufferedImage img;

		VImage(ImgPanel panel) {
			this.panel = panel;
		}

		VImage validate() {
			if( invalid() ) {
				int w = Math.max( 16, panel.getWidth() ) ;
				int h = Math.max( 16, panel.getHeight() ) ;
				img = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB) ;				
				if (img != null)
					setTransform();
			}
			return this;
		}

		void drawTo(Graphics2D g) {
			g.drawImage(img, 0, 0, null);
		}

		void clear() {
			if (img != null) {
				if (isOverlay )
					canvas.gu.setBackground(new Color(255, 255, 255, 64) );
				else
					canvas.gu.setBackground( Color.WHITE );
				canvas.gu.clearRect(0, 0, img.getWidth(), img.getHeight());
			}
		}

		private boolean invalid() {
			return img == null || img.getWidth() != panel.getWidth()
					|| img.getHeight() != panel.getHeight();
		}

		void setTransform() {
			
			if (img != null) {
				double p_width = img.getWidth();
				double p_height = img.getHeight();

				double tx, ty, sx, sy, ww, hh;
				double p_aspectRatio = p_width / p_height;
				double v_aspectRatio = v_width / v_height;

				if (p_aspectRatio > v_aspectRatio) {
					hh = p_height * (1 - offset);
					ww = hh * (keepRatios ? v_aspectRatio : p_aspectRatio);
				} else {
					ww = p_width * (1 - offset);
					hh = ww / (keepRatios ? v_aspectRatio : p_aspectRatio);
				}

				sx = ww / v_width;
				sy = hh / v_height;
				tx = (p_width - ww) / 2;
				ty = (p_height - hh) / 2;

				Graphics2D gu, gs;
				gu = img.createGraphics();
				gu.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				gu.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				gu.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
						RenderingHints.VALUE_FRACTIONALMETRICS_ON);

				gs = (Graphics2D) gu.create();
				gs.setTransform(I);
				gs.setTransform(I);
				gs.translate(tx, ty);
				gs.scale(sx, sy);

				canvas = new Canvas(gu, gs);
			}
		}
	}

	protected Collection<Displayable> displayables;
	protected Collection<InputHandler> inputHandlers;
	private static final AffineTransform I = new AffineTransform();

	public void onMouseMove(XY pu, XY ps) {
		ps = updateScaledMousePos(pu);
		for (InputHandler i : inputHandlers)
			i.onMouseMove(pu, ps);
	}

	public void onMouseClick(int button, XY pu, XY ps) {
		ps = updateScaledMousePos(pu);
		for (InputHandler i : inputHandlers)
			i.onMouseClick(button, pu, ps);

	}

	public void onMouseDragged(int button, XY pu, XY ps) {
		ps = updateScaledMousePos(pu);
		for (InputHandler i : inputHandlers)
			i.onMouseDragged(button, pu, ps);
	}

	public XY updateScaledMousePos(XY pu) {
		XY ps;
		try {
			Point2D tmp = new Point2D.Double();
			buffers[0].canvas.gs.getTransform().inverseTransform(pu.point2D(),
					tmp);
			ps = new XY(tmp.getX(), tmp.getY());

			buffers[0].canvas.updateMousePosition(pu, ps);
			buffers[1].canvas.updateMousePosition(pu, ps);
		} catch (Exception e) {
			ps = new XY(-1, -1);
		}
		return ps;
	}

	@Override
	public void mouseWheelMoved(XY pu, XY ps, double units) {
		ps = updateScaledMousePos(pu);
		for (InputHandler i : inputHandlers)
			i.mouseWheelMoved(pu, ps, units);
	}
}
