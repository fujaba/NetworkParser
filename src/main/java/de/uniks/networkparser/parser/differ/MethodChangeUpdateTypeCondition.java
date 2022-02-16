package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class MethodChangeUpdateTypeCondition.
 *
 * @author Stefan
 */
public class MethodChangeUpdateTypeCondition extends MatchCondition {

	/**
	 * Instantiates a new method change update type condition.
	 */
	public MethodChangeUpdateTypeCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		Method oldMethod = (Method) match.getMatch();
		Method newMethod = (Method) match.getOtherMatch().getMatch();

		if (matches.getMetaModel() == null) {
			return false;
		}
		if (!match.isMetaMatch() && !match.isMetaSourceMatch()) {
			return false;
		}
		if (oldMethod.getReturnType().equals(newMethod.getReturnType())) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Method oldMethod = (Method) match.getMatch();
		Method newMethod = (Method) match.getOtherMatch().getMatch();

		Match updateInCode = Match.create(newMethod, this, Method.PROPERTY_RETURNTYPE, newMethod.getReturnType(),
				oldMethod.getReturnType());
		matches.addDiff(updateInCode);
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		Method oldMethod = (Method) match.getOtherMatch().getMatch();
		Method newMethod = (Method) match.getMatch();

		return !oldMethod.getReturnType().equals(newMethod.getReturnType());
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Method oldMethod = (Method) match.getOtherMatch().getMatch();
		Method newMethod = (Method) match.getMatch();
		Match update = Match.create(oldMethod, this, Method.PROPERTY_RETURNTYPE, oldMethod.getReturnType(),
				newMethod.getReturnType());

		matches.addDiff(update);
		return true;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	@Override
	public String getAction() {
		return SendableEntityCreator.UPDATE;
	}

}
