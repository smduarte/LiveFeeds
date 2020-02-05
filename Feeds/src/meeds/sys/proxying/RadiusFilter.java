package meeds.sys.proxying;

import feeds.api.*;
import meeds.api.Meeds;
import meeds.sys.util.*;
import meeds.sys.homing.*;

public class RadiusFilter extends Criteria<Position> {

		final XY center;
		final double radiusSq;

		RadiusFilter(Position center, double radius) {
			this.center = center.xy;
			this.radiusSq = radius * radius;
		}

		public boolean accepts(Position pos) {
			return center.distanceSq(pos.xy) < radiusSq;
		}

		public String toString() {
			return String.format("R(%s,%.1f)", center, Math.sqrt( radiusSq ) );
		}
		
		private static final long serialVersionUID = 1L;
}
