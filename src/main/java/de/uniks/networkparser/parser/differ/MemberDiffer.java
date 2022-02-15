package de.uniks.networkparser.parser.differ;

import java.util.List;

import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class MemberDiffer.
 *
 * @author Stefan
 */
public class MemberDiffer {

	protected SimpleList<MatchCondition> memberConditions = new SimpleList<MatchCondition>();

	/**
	 * Instantiates a new member differ.
	 *
	 * @param memberMatches the member matches
	 */
	public MemberDiffer(MatchCondition... memberMatches) {
		if (memberMatches != null) {
			for (MatchCondition condition : memberMatches) {
				this.memberConditions.add(condition);
			}
		}

	}

	/**
	 * Diff.
	 *
	 * @param matches the matches
	 * @param memberMatches the member matches
	 */
	public void diff(GraphMatcher matches, List<Match> memberMatches) {
		for (Match match : memberMatches) {
			if (match.isMetaMatch()) {
				MemberDiffer.executeCondition(SendableEntityCreator.REMOVE, memberConditions, match);
			} else {
				if (match.isOtherMatch() == false) {
					MemberDiffer.executeCondition(SendableEntityCreator.NEW, memberConditions, match);
				} else {
					MemberDiffer.executeCondition(SendableEntityCreator.UPDATE, memberConditions, match);
				}
			}
		}
	}

	/**
	 * Execute condition.
	 *
	 * @param action the action
	 * @param memberConditions the member conditions
	 * @param match the match
	 */
	public static void executeCondition(String action, List<MatchCondition> memberConditions, Match match) {
		if (memberConditions != null && action != null) {
			for (int i = 0; i < memberConditions.size(); i++) {
				MatchCondition condition = memberConditions.get(i);
				if (condition != null && action.equals(condition.getAction())) {
					if (condition.update(match)) {
						break;
					}
				}
			}
		}
	}
}
