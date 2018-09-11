package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class MethodChangeCondition extends MatchCondition {

	public MethodChangeCondition() {
		super(	new ChangeRenameCondition(), 
				new ChangeAddCondition(), 
				new ChangeAddModifierCondition(),
				new ChangeRemoveModifierCondition(),
				new MethodChangeUpdateTypeCondition(),
				new MethodChangeUpdateCondition(),
				new MethodChangeUpdateBodyCondition());
	}

	@Override
	protected boolean calculateDiffs(GraphMatcher matches, Match match) {
		Method sourceMethod = (Method) match.getMatch();
		Method otherMethod = (Method) match.getOtherMatch().getMatch();

		if (addChange(matches, match, sourceMethod, otherMethod)) {
			MemberDiffer.executeCondition(SendableEntityCreator.NEW, changeConditions, match);
		} else {
			MemberDiffer.executeCondition(SendableEntityCreator.UPDATE, changeConditions, match);
			MemberDiffer.executeCondition(SendableEntityCreator.NEW, changeConditions, match);
			MemberDiffer.executeCondition(SendableEntityCreator.REMOVE, changeConditions, match);
			MemberDiffer.executeCondition(SendableEntityCreator.UPDATE, changeConditions, match);
			MemberDiffer.executeCondition(SendableEntityCreator.UPDATE, changeConditions, match);
			MemberDiffer.executeCondition(SendableEntityCreator.UPDATE, changeConditions, match);
		}
		return true;
	}

	private boolean addChange(GraphMatcher matches, Match match, Method sourceMethod, Method otherMethod) {
		if (matches.getMetaModel() != null && (match.isMetaSourceMatch() || match.isMetaMatch())) {
			return false;
		}
		if (sourceMethod.getName().equals(otherMethod.getName())) {
			if (sourceMethod.getClazz().getName().equals(otherMethod.getClazz().getName())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.UPDATE;
	}
	
}
