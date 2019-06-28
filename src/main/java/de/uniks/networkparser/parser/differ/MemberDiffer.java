package de.uniks.networkparser.parser.differ;

import java.util.List;

import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.parser.GraphMatcher;

public class MemberDiffer {

	protected SimpleList<MatchCondition> memberConditions = new SimpleList<MatchCondition>();

	public MemberDiffer(MatchCondition... memberMatches) {
		if (memberMatches != null) {
			for (MatchCondition condition : memberMatches) {
				this.memberConditions.add(condition);
			}
		}

	}

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
