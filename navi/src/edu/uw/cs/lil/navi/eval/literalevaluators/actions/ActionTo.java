package edu.uw.cs.lil.navi.eval.literalevaluators.actions;

import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.navi.map.PositionSetSingleton;

public class ActionTo extends NaviInvariantLiteralEvaluator {
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 2 && args[0] instanceof Action
				&& args[1] instanceof PositionSet) {
			// The destination should be contained within the argument position
			// set, while non of the middle positions should be contained within
			// it
			final PositionSet ps1 = (PositionSet) args[1];
			final Action action = (Action) args[0];
			if (ps1.isIntersective(PositionSetSingleton.of(action.getEnd()))
					&& !ps1.isIntersective(PositionSetSingleton.of(action
							.getStart()))) {
				for (final Position intermediatePosition : action
						.getIntermediatePositions()) {
					if (ps1.isIntersective(PositionSetSingleton
							.of(intermediatePosition))) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return null;
		}
	}
}