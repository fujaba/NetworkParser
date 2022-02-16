package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class MatchCondition.
 *
 * @author Stefan
 */
public class MatchCondition implements ObjectCondition {
	protected boolean isReverse;
	protected SimpleList<MatchCondition> changeConditions;

	/**
	 * Instantiates a new match condition.
	 *
	 * @param isReverse the is reverse
	 */
	public MatchCondition(boolean isReverse) {
		this.isReverse = isReverse;
	}

	/**
	 * Instantiates a new match condition.
	 *
	 * @param conditions the conditions
	 */
	public MatchCondition(MatchCondition... conditions) {
		if (conditions != null) {
			this.changeConditions = new SimpleList<MatchCondition>();
			for (MatchCondition condition : conditions) {
				this.changeConditions.add(condition);
			}
		}
	}

	protected boolean executeMatch(Match match, boolean isModelCheck) {
		GraphMatcher matchData = match.getOwner();
		if (isModelCheck) {
			if (!checkModelCondition(matchData, match)) {
				return false;
			}
			return calculateModelDiffs(matchData.getOldModel(), matchData, match);
		}
		if (!checkFileCondition(matchData, match)) {
			return false;
		}
		return calculateFileDiffs(matchData.getNewModel(), matchData, match);
	}

	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return true;
	}

	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return true;
	}

	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		return false;
	}

	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		return false;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return null;
	}

	/** For Groups
	 * @param matches The Matches
	 * @param match The Current Match
	 * @return success
	 */
	protected boolean checkCondition(GraphMatcher matches, Match match) {
		return true;
	}

	protected boolean calculateDiffs(GraphMatcher matches, Match match) {
		return false;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (!(value instanceof Match)) {
			return false;
		}
		Match match = (Match) value;
		if (changeConditions != null) {
			GraphMatcher matches = match.getOwner();

			if (!checkCondition(matches, match)) {
				return false;
			}
			return calculateDiffs(matches, match);
		}
		if (isReverse) {
			return executeMatch(match, match.isFileMatch());
		}
		return !executeMatch(match, match.isFileMatch());
	}

}
