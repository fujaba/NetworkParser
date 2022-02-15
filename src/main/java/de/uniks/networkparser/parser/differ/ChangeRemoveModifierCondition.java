package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class ChangeRemoveModifierCondition.
 *
 * @author Stefan
 */
public class ChangeRemoveModifierCondition extends MatchCondition {

	/**
	 * Instantiates a new change remove modifier condition.
	 */
	public ChangeRemoveModifierCondition() {
		super(true);
	}

	protected boolean checkCondition(GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		GraphMember sourceAttribute = match.getMatch();
		GraphMember otherAttribute = match.getSourceMatch();

		if (matches.getMetaModel() == null) {
			return false;
		}
		if (match.isMetaMatch() == false && match.isSourceMatch() == false) {
			return false;
		}
		if (sourceAttribute.getModifier().toString().equals(otherAttribute.getModifier().toString())) {
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
		GraphMember oldAttribute = match.getMatch();
		GraphMember newAttribute = match.getSourceMatch();

		for (String modifier : newAttribute.getModifier().toString().split(" ")) {
			if (oldAttribute.getModifier().toString().contains(modifier) == false) {
				Match addModifier = Match.create(oldAttribute, this, Attribute.PROPERTY_MODIFIERS, modifier, null);

				matches.addDiff(addModifier);
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
		GraphMember oldAttribute = match.getSourceMatch();
		GraphMember newAttribute = match.getMatch();

		for (String modifier : oldAttribute.getModifier().toString().split(" ")) {
			if (newAttribute.getModifier().toString().contains(modifier) == false) {
				Match addModifier = Match.create(oldAttribute, this, Attribute.PROPERTY_MODIFIERS, modifier, null);
				matches.addDiff(addModifier);
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
