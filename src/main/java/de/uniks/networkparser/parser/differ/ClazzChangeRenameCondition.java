package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class ClazzChangeRenameCondition.
 *
 * @author Stefan
 */
public class ClazzChangeRenameCondition extends MatchCondition {

	/**
	 * Instantiates a new clazz change rename condition.
	 */
	public ClazzChangeRenameCondition() {
		super(true);
	}

	protected boolean checkCondition(GraphMatcher matches, Match match) {
		Clazz sourceClazz = (Clazz) match.getMatch();
		Clazz otherClazz = (Clazz) match.getSourceMatch();

		return sourceClazz.getName().equals(otherClazz.getName()) == false;
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Clazz oldClazz = (Clazz) match.getMatch();
		Clazz newClazz = (Clazz) match.getSourceMatch();
		Match rename = Match.create(newClazz, this, Clazz.PROPERTY_NAME, newClazz.getName(), oldClazz.getName());
		matches.addDiff(rename);
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Clazz oldClazz = (Clazz) match.getSourceMatch();
		Clazz newClazz = (Clazz) match.getMatch();

		Match rename = Match.create(oldClazz, this, Clazz.PROPERTY_NAME, oldClazz.getName(), newClazz.getName());
		matches.addDiff(rename);
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
