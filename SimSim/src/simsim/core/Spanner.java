package simsim.core;

import java.util.*;

import simsim.graphs.*;
import simsim.gui.geom.XY;
import simsim.gui.geom.Line;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.canvas.Canvas;
import static simsim.core.Simulation.*;

/**
 * 
 * Creates a simplified graph covering all the nodes in the network. Can only be
 * used with small sized systems, up to about 200 nodes.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 */
public class Spanner implements Displayable {

	/**
	 * Controls how the complete graph connecting all nodes will be simplified
	 * to create a cover graph (spanner)
	 * 
	 * @param T
	 *            - Is the maximum cost degradation allowed between any pair of
	 *            nodes in the simplified graph, relative to the cost in the
	 *            original complete graph. Any value larger than 1 is allowed,
	 *            the greater the value, the sparser the resulting graph/tree
	 *            is.
	 * 
	 */
	public void setThreshold(double T) {
		if (K != T && T > 1) {
			K = (float) T;
			spanner = null;
			trees.clear();
		}
		if (T <= 1)
			throw new RuntimeException("Invalid Argument. Use a value greater than 1.");
	}

	/**
	 * Given a node, chosen as the root node of a shortest paths tree, this
	 * method returns the children of that node.
	 * 
	 * The shortest paths tree is computed as needed from the spanner graph
	 * produced by the current Threshold value, which can be changed using
	 * setThreshold().
	 * 
	 * @param root
	 *            - The root node of the shortest paths tree.
	 * @param parent
	 *            - A node in the network.
	 * @return the children nodes of node selected as parent.
	 */
	public Set<NetAddress> children(NetAddress root, NetAddress parent) {
		Set<NetAddress> res = shortestPathsTree(root).children(parent);
		assert res != null;
		return res;
	}

	/**
	 * Given a node, chosen as the root node of a shortest paths tree, this
	 * method returns the parent node of a given child node.
	 * 
	 * The shortest paths tree is computed as needed from the spanner graph
	 * produced by the current Threshold value, which can be changed using
	 * setThreshold().
	 * 
	 * @param root
	 *            - The root node of the shortest paths tree.
	 * @param node
	 *            - the child node .
	 * @return the parent node of the given child node, according to the
	 *         shortest paths tree in question.
	 */
	public NetAddress parent(NetAddress root, NetAddress child) {
		return root.equals(child) ? null : shortestPathsTree(root).parent(child);
	}

	/**
	 * If the network membership is dynamic, with nodes coming and going, the
	 * spanner has to be recomputed accordingly.
	 * 
	 * This method discard previously computed and cached spanner and tree
	 * topologies.
	 */
	public void invalidate() {
		spanner = null;
		trees.clear();
	}

	public void add(NetAddress addr) {
		addresses.put(addr, 0);
		invalidate();
	}

	public void remove(NetAddress addr) {
		addresses.remove(addr);
		trees.remove(addr);
		invalidate();
	}

	public Graph<NetAddress> spanner() {
		if (spanner == null) {
			spanner = spanner(K);
		}
		return spanner;
	}

	final Pen pen1 = new Pen( RGB.GRAY, 1) ;
	final Pen pen2 = new Pen( RGB.gray, 5) ;
	// ------------------------------------------------------------------------------------------------------------------
	public void displayOn( Canvas canvas ) {

		pen1.useOn( canvas.gs ) ;
		for (Link<NetAddress> i : spanner().links()) {
			canvas.sDraw( new Line(i.v.pos, i.w.pos));
		}

		if (dpySpannerSPT) {
			XY mouse = canvas.sMouse() ;
			NetAddress closest = null;
			for (NetAddress i : Network.addresses())
				if (closest == null || mouse.distance(i.pos) < mouse.distance(closest.pos))
					closest = i;

			pen2.useOn( canvas.gs ) ;
			if (closest != null && mouse.distance(closest.pos) < 10) {
				for (Link<NetAddress> i : shortestPathsTree(closest).edges()) {
					canvas.sDraw(new Line(i.v.pos, i.w.pos));
				}
			}
		}
	}

	ShortestPathsTree<NetAddress> shortestPathsTree(NetAddress root) {
		ShortestPathsTree<NetAddress> res = trees.get(root);
		if (res == null) {
			res = new ShortestPathsTree<NetAddress>(root, spanner());
			trees.put(root, res);
		}
		return res;
	}

	

	private kSpanner<NetAddress> spanner(float f) {
		ArrayList<Link<NetAddress>> n2n = new ArrayList<Link<NetAddress>>();

		ArrayList<NetAddress> eal = new ArrayList<NetAddress>();
		for (NetAddress i : addresses.keySet())
			if (i != null)
				eal.add(i);

		int N = eal.size();
		for (int i = 0; i < N; i++) {
			NetAddress eI = eal.get(i);
			for (int j = 0; j < i; j++) {
				NetAddress eJ = eal.get(j);
				n2n.add(new Link<NetAddress>(eI, eJ, eI.latency(eJ)));
			}
		}
		return new kSpanner<NetAddress>(f, eal, n2n);
	}

	private float K = 5.0f;
	private kSpanner<NetAddress> spanner = null;

	private boolean dpySpannerSPT = Globals.get("Spanner_Display_SPT", true);

	private WeakHashMap<NetAddress, Integer> addresses = new WeakHashMap<NetAddress, Integer>();
	private WeakHashMap<NetAddress, ShortestPathsTree<NetAddress>> trees = new WeakHashMap<NetAddress, ShortestPathsTree<NetAddress>>();
}
