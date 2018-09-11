package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class ClazzChangeCondition extends MatchCondition {

	public ClazzChangeCondition() {
		changeConditions.add(new ClazzChangeRenameCondition());
		changeConditions.add(new ChangeAddCondition());
		changeConditions.add(new ClazzChangeAddModifierCondition());
		changeConditions.add(new ClazzChangeRemoveModifierCondition());
		changeConditions.add(new ClazzChangeUpdateTypeCondition());
		changeConditions.add(new ClazzChangeUpdateCondition());
	}

	@Override
	protected boolean calculateDiffs(GraphMatcher matches, Match match) {
		Clazz sourceClazz = (Clazz) match.getMatch();
		Clazz otherClazz = (Clazz) match.getSourceMatch();
		
		if (addChange(matches, match, sourceClazz, otherClazz)) {
			MemberDiffer.executeCondition(SendableEntityCreator.NEW, changeConditions, match);
		} else {
			MemberDiffer.executeCondition(SendableEntityCreator.UPDATE, changeConditions, match);
			MemberDiffer.executeCondition(SendableEntityCreator.REMOVE, changeConditions, match);
		}
		return true;
	}
	
	private boolean addChange(GraphMatcher matches, Match match, Clazz sourceClazz, Clazz otherClazz) {
		if (matches.getMetaModel() != null && (match.isMetaMatch() || match.isMetaSourceMatch())) {
			return false;
		}
		if (sourceClazz.getName().equals(otherClazz.getName())) {
			return false;
		}
		
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.UPDATE;
	}
	
}
