package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class ClazzChangeAddModifierCondition.
 *
 * @author Stefan
 */
public class ClazzChangeAddModifierCondition extends MatchCondition {

	/**
	 * Instantiates a new clazz change add modifier condition.
	 */
	public ClazzChangeAddModifierCondition() {
		super(true);
	}

	protected boolean checkCondition(GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		Clazz sourceClazz = (Clazz) match.getMatch();
		Clazz otherClazz = (Clazz) match.getSourceMatch();

		return sourceClazz.getModifier().toString().equals(otherClazz.getModifier().toString()) == false;
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		Clazz oldClazz = (Clazz) match.getMatch();
		Clazz newClazz = (Clazz) match.getSourceMatch();

		for (String modifier : oldClazz.getModifier().toString().split(" ")) {
			if (newClazz.getModifier().toString().contains(modifier) == false) {
				Match addInFile = Match.create(newClazz, this, Clazz.PROPERTY_MODIFIERS, null, modifier);
				matches.addDiff(addInFile);
			}
		}
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		Clazz oldClazz = (Clazz) match.getSourceMatch();
		Clazz newClazz = (Clazz) match.getMatch();

		for (String modifier : newClazz.getModifier().toString().split(" ")) {
			if (oldClazz.getModifier().toString().contains(modifier) == false) {
				Match removeInFile = Match.create(oldClazz, this, Clazz.PROPERTY_MODIFIERS, null, modifier);

				matches.addDiff(removeInFile);
			}
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
