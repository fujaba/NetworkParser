package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class ClazzChangeUpdateTypeCondition.
 *
 * @author Stefan
 */
public class ClazzChangeUpdateTypeCondition extends MatchCondition {

	/**
	 * Instantiates a new clazz change update type condition.
	 */
	public ClazzChangeUpdateTypeCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		Clazz oldClazz = (Clazz) match.getMatch();
		Clazz newClazz = (Clazz) match.getSourceMatch();

		if (matches.getMetaModel() == null) {
			return false;
		}
		if (!match.isMetaMatch() && !match.isMetaSourceMatch()) {
			return false;
		}
		if (oldClazz.getType().equals(newClazz.getType())) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Clazz oldClazz = (Clazz) match.getMatch();
		Clazz newClazz = (Clazz) match.getSourceMatch();
		Match updateTypeInCode = Match.create(newClazz, this, Clazz.PROPERTY_TYPE, newClazz.getType(),
				oldClazz.getType());
		matches.addDiff(updateTypeInCode);
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		Clazz oldClazz = (Clazz) match.getOtherMatch().getMatch();
		Clazz newClazz = (Clazz) match.getMatch();

		if (oldClazz.getType().equals(newClazz.getType())) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Clazz oldClazz = (Clazz) match.getOtherMatch().getMatch();
		Clazz newClazz = (Clazz) match.getMatch();

		Match updateType = Match.create(oldClazz, this, Clazz.PROPERTY_TYPE, oldClazz.getType(), newClazz.getType());
		matches.addDiff(updateType);
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
