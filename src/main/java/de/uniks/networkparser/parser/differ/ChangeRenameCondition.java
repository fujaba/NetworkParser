package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class ChangeRenameCondition.
 *
 * @author Stefan
 */
public class ChangeRenameCondition extends MatchCondition {

	/**
	 * Instantiates a new change rename condition.
	 */
	public ChangeRenameCondition() {
		super(true);
	}

	protected boolean checkCondition(GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		GraphMember sourceAttribute = match.getMatch();
		GraphMember otherAttribute = match.getSourceMatch();
		if (otherAttribute == null) {
			return false;
		}
		return sourceAttribute.getName().equals(otherAttribute.getName()) == false;
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

		Match rename = Match.create(newAttribute, this, Attribute.PROPERTY_NAME, newAttribute.getName(),
				oldAttribute.getName());
		matches.addDiff(rename);
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
		Match rename = Match.create(oldAttribute, this, Attribute.PROPERTY_NAME, oldAttribute.getName(),
				newAttribute.getName());
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
