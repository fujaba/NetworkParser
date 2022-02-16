package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class ClazzChangeRemoveModifierCondition.
 *
 * @author Stefan
 */
public class ClazzChangeRemoveModifierCondition extends MatchCondition {

	/**
	 * Instantiates a new clazz change remove modifier condition.
	 */
	public ClazzChangeRemoveModifierCondition() {
		super(true);
	}

	protected boolean checkCondition(GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		Clazz sourceClazz = (Clazz) match.getMatch();
		Clazz otherClazz = (Clazz) match.getSourceMatch();

		if (matches.getMetaModel() == null) {
			return false;
		}
		if (!match.isMetaMatch() && !match.isMetaSourceMatch()) {
			return false;
		}
		if (sourceClazz.getModifier().toString().equals(otherClazz.getModifier().toString())) {
			return false;
		}

		return true;
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

		for (String modifier : newClazz.getModifier().toString().split(" ")) {
			if (!oldClazz.getModifier().toString().contains(modifier)) {
				Match removeInFile = Match.create(newClazz, this, Clazz.PROPERTY_MODIFIERS, modifier, null);

				matches.addDiff(removeInFile);
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
		Clazz oldClazz = (Clazz) match.getSourceMatch();
		Clazz newClazz = (Clazz) match.getMatch();

		for (String modifier : oldClazz.getModifier().toString().split(" ")) {
			if (!newClazz.getModifier().toString().contains(modifier)) {
				Match addInFile = Match.create(oldClazz, this, Clazz.PROPERTY_MODIFIERS, modifier, null);
				matches.addDiff(addInFile);
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
		return SendableEntityCreator.REMOVE;
	}

}
