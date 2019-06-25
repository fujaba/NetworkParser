package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.GraphMatcher;

public class MatchCondition implements ObjectCondition {
	protected boolean isReverse;
	protected SimpleList<MatchCondition> changeConditions;

	public MatchCondition(boolean isReverse) {
		this.isReverse = isReverse;
	}

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
			if (checkModelCondition(matchData, match) == false) {
				return false;
			}
			return calculateModelDiffs(matchData.getOldModel(), matchData, match);
		}
		if (checkFileCondition(matchData, match) == false) {
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

	@Override
	public boolean update(Object value) {
		if (value instanceof Match == false) {
			return false;
		}
		Match match = (Match) value;
		if (changeConditions != null) {
			GraphMatcher matches = match.getOwner();

			if (checkCondition(matches, match) == false) {
				return false;
			}
			return calculateDiffs(matches, match);
		}
		if (isReverse) {
			return executeMatch(match, match.isFileMatch());
		}
		return executeMatch(match, match.isFileMatch() == false);
	}

}
