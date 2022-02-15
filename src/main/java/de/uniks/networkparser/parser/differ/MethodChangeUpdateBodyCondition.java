package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class MethodChangeUpdateBodyCondition.
 *
 * @author Stefan
 */
public class MethodChangeUpdateBodyCondition extends MatchCondition {

	/**
	 * Instantiates a new method change update body condition.
	 */
	public MethodChangeUpdateBodyCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Method oldMethod = (Method) match.getMatch();
		Method newMethod = (Method) match.getOtherMatch().getMatch();

		if ((oldMethod.getBody() != null && oldMethod.getBody().equals(newMethod.getBody()) == false)
				|| (newMethod.getBody() != null && newMethod.getBody().equals(oldMethod.getBody()) == false)) {
			/* MethodBody changed */
			Match update = Match.create(newMethod, this, Method.PROPERTY_BODY, newMethod.getBody(),
					oldMethod.getBody());

			matches.addDiff(update);
		}
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Method oldMethod = (Method) match.getOtherMatch().getMatch();
		Method newMethod = (Method) match.getMatch();

		if ((oldMethod.getBody() != null && oldMethod.getBody().equals(newMethod.getBody()) == false)
				|| (newMethod.getBody() != null && newMethod.getBody().equals(oldMethod.getBody()) == false)) {
			/* MethodBody changed */
			Match update = Match.create(oldMethod, this, Method.PROPERTY_BODY, oldMethod.getBody(),
					newMethod.getBody());

			matches.addDiff(update);
		}
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
