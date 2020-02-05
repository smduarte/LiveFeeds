package feeds.sys.catadupa;

import static feeds.sys.catadupa.Catadupa.BROADCAST_MAX_FANOUT;
import static feeds.sys.catadupa.Catadupa.JOIN_ATTEMPT_PERIOD;
import static feeds.sys.catadupa.Catadupa.SL_BROADCAST_PERIOD;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import feeds.api.Feeds;
import feeds.sys.FeedsNode;
import feeds.sys.FeedsRegistry;
import feeds.sys.backbone.containers.BackboneNodes;
import feeds.sys.catadupa.packets.CatadupaLeafCast;
import feeds.sys.catadupa.packets.CatadupaTreeCast;
import feeds.sys.catadupa.packets.ControlPacketRouter;
import feeds.sys.catadupa.packets.Join_Request;
import feeds.sys.catadupa.packets.Repair_Reply;
import feeds.sys.catadupa.packets.Repair_Request;
import feeds.sys.core.Container;
import feeds.sys.core.ContainerListener;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;
import feeds.sys.membership.containers.CriteriaDB;
import feeds.sys.pipeline.BasicTemplate0;
import feeds.sys.tasks.Task;
import feeds.sys.transports.containers.DefaultIncomingTransport;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version
 */
public class CatadupaChannel extends BasicTemplate0 {

	public void init() {
		super.init();
		switch (FeedsNode.type()) {
		case cNODE:
			pipeline.setTemplate(new c_Catadupa(channel));
			break;
		case sNODE:
			pipeline.setTemplate(new s_Catadupa(channel));
			break;
		case pNODE:
			pipeline.setTemplate(new p_Catadupa(channel));
			break;
		}
	}
}

// --------------------------------------------------------------------------------------------------
class CatadupaNodeChannel extends ControlPacketRouter {

	CatadupaNodeChannel(ID channel) {
		super(channel);
	}

	public void init() {
		super.init();

		final DefaultIncomingTransport dit = Container.byClass(DefaultIncomingTransport.class);

		thisNode = new Node(dit.url());
		catadupa.key = thisNode.key;
		catadupa.db.store(thisNode);
		catadupa.thisNode = thisNode;

		Container.monitor(CriteriaDB.class, new ContainerListener<CriteriaDB>() {
			public void handleContainerUpdate(CriteriaDB c) {
				thisNode.data = new SubscriptionData(c.pData(), c.fData());
				catadupa.joined = false;
				Feeds.err.printf("Catadupa: [Joining with key: %s]\n", thisNode);
				joinCatadupa();
			}
		});

		new Task(10) {
			public void run() {
				doEpidemicRepairRound();
				this.reSchedule(60.0 + 5 * FeedsNode.rnd().nextDouble());
			}
		};
		FeedsRegistry.put("Catadupa", catadupa) ;
	}


	public void cRoute(CatadupaTreeCast x) {
		
		JoinBatch b0 = x.batch;
		Range r0 = x.range;

		processBatch(b0);
		
		JoinBatch b1 = new JoinBatch(b0.stamp, catadupa.gView, b0.nodes);


		if (x.level > 0)
			r0 = r0.advancePast(thisNode.key);


		Collection<Node> N0 = r0.nodes(catadupa.db);
		if (N0.size() <= BROADCAST_MAX_FANOUT) {

			for (Node i : N0)
				sendTo(i.url, new CatadupaLeafCast(b1));

		} else {

			for (Range j : r0.slice(x.level == 0 ? 2 : BROADCAST_MAX_FANOUT, catadupa.db)) {
				for (Node i : j.nodes(catadupa.db)) {
					sendTo(i.url, new CatadupaTreeCast(x.level + 1, j, b1));
					break;
				}

			}
		}
	}

	public void cRoute(CatadupaLeafCast x) {
		processBatch(x.batch);
	}

	public void cRoute(Repair_Request x) {
		Set<JoinBatch> res = catadupa.jbdb.sets(x.oView.getMissingBatches(catadupa.gView, 1));
		if (!res.isEmpty())
			sendTo(x.url, new Repair_Reply(res));
		else
			if( ! catadupa.oView.getMissingBatches( catadupa.gView).isEmpty() )
				soq.send( x.cPacket(channel)) ;
	}

	public void cRoute(Repair_Reply r) {
		for (JoinBatch i : r.jbs)
			processBatch(i);

		if (catadupa.oView.missingBatches(catadupa.gView) > 5)
			doEpidemicRepairRound();
	}

	private void processBatch(JoinBatch batch) {
		catadupa.oView.add(batch.stamp);
		catadupa.gView.merge(batch.view);

		boolean joined = catadupa.joined;

		catadupa.joined |= (!joined && batch.containsNewer(thisNode));
		if (joined != catadupa.joined)
			Feeds.err.printf("Catadupa: [Joined with key: %s]\n", thisNode);

		catadupa.jbdb.store(batch);
		catadupa.db.storeAll(batch.nodes);
		
	}





	private void joinCatadupa() {
		if (joinCatadupaTask == null) {
			joinCatadupaTask = new Task(0) {
				public void run() {
					if (!catadupa.joined) 
							soq.send(new Join_Request(thisNode).cPacket(channel));
						this.reSchedule(JOIN_ATTEMPT_PERIOD);
					}
			};
		} else
			joinCatadupaTask.reSchedule(1);
	}
	
	private void doEpidemicRepairRound() {
		if (catadupa.joined) {
			Repair_Request rr = new Repair_Request(thisNode.url, catadupa.oView);
			Node target = catadupa.db.randomNode(thisNode.key);
			if (target != null)
				sendTo(target.url, rr);
			else
				soq.send(rr.cPacket(channel));
		}
		
	}

	private Task joinCatadupaTask;

	Node thisNode = null;
	Catadupa catadupa = new Catadupa();
}

class c_Catadupa extends CatadupaNodeChannel {
	c_Catadupa(ID channel) {
		super(channel);
	}

	public void init() {
		super.init();
	}
}

class s_Catadupa extends CatadupaNodeChannel {
	s_Catadupa(ID channel) {
		super(channel);
	}

	public void init() {
		super.init();
	}
	
	public void cRoute(Join_Request p) {
		soq.send( p.cPacket(channel) ) ;
	}
}

// --------------------------------------------------------------------------------------------------
class p_Catadupa extends ControlPacketRouter {

	p_Catadupa(ID channel) {
		super(channel);
	}

	public void init() {
		super.init();

		final DefaultIncomingTransport dit = Container.byClass(DefaultIncomingTransport.class);

		thisNode = new Node(FeedsNode.id(), dit.url());
		catadupa.key = thisNode.key;
		catadupa.thisNode = thisNode;

		Container.monitor(BackboneNodes.class, new ContainerListener<BackboneNodes>() {
			public void handleContainerUpdate(BackboneNodes bn) {
				for (Transport i : bn.transports().values()) {
					if (!pNodes.contains(-i.dst().longValue()))
						pNodes.store(new Node(i.dst(), i.url()));
				}
			}
		});

		initpNodeTask();

		new Task(10) {
			public void run() {
				doEpidemicRepairRound();
				this.reSchedule(60.0 + 5 * FeedsNode.rnd().nextDouble());
			}
		};
	}

	private void initpNodeTask() {
		if (pNodeTask == null) {
			pNodeTask = new Task(0) {
				public void run() {
					if (!arrivals.isEmpty()) {
						JoinBatch jb = new JoinBatch(catadupa.issueStamp(), catadupa.gView, arrivals);
						Node target = catadupa.db.randomNode( thisNode.key);
						if( target == null ) {
							processBatch(jb);
							target = catadupa.db.randomNode( thisNode.key);
							sendTo(target.url, new CatadupaTreeCast(target, jb));
						} else {
							sendTo(target.url, new CatadupaTreeCast(target, jb));
							processBatch(jb);
						}
						arrivals.clear();
					}
				}
			};
		}
	}

	private Set<Node> arrivals = new TreeSet<Node>();

	public void cRoute(Join_Request p) {
		arrivals.add(p.node);
		if (!pNodeTask.isScheduled())
			pNodeTask.reSchedule(SL_BROADCAST_PERIOD/2);
		
	}

	private void processBatch(JoinBatch batch) {
		catadupa.jbdb.store(batch);
		catadupa.oView.add(batch.stamp);
		catadupa.gView.merge(batch.view);
		catadupa.db.storeAll(batch.nodes);
	}

	public void cRoute(Repair_Reply r) {
		for (JoinBatch i : r.jbs)
			processBatch(i);

		if (catadupa.oView.missingBatches(catadupa.gView) > 5)
			doEpidemicRepairRound();
	}
	
	public void cRoute(Repair_Request x) {
		Set<JoinBatch> res = catadupa.jbdb.sets(x.oView.getMissingBatches(catadupa.gView));
		if (!res.isEmpty())
			sendTo(x.url, new Repair_Reply(res));
	}

	private void doEpidemicRepairRound() {
		Node target = pNodes.randomNode(thisNode.key);
		
		if (target != null)
			sendTo(target.url, new Repair_Request(thisNode.url, catadupa.oView));

	}

	private Node thisNode;
	private Task pNodeTask;
	private NodeDB pNodes = new NodeDB();
	private Catadupa catadupa = new Catadupa();
}