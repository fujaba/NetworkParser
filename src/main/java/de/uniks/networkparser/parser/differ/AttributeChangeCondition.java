package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class AttributeChangeCondition extends MatchCondition {

	public AttributeChangeCondition() {
		changeConditions.add(new ChangeRenameCondition());
		changeConditions.add(new ChangeAddCondition());
		changeConditions.add(new ChangeAddModifierCondition());
		changeConditions.add(new ChangeRemoveModifierCondition());
		changeConditions.add(new AttributeChangeUpdateTypeCondition());
	}

	@Override
	protected boolean calculateDiffs(GraphMatcher matches, Match match) {
		Attribute sourceAttribute = (Attribute) match.getMatch();
		Attribute otherAttribute = (Attribute) match.getOtherMatch().getMatch();

		if (addChange(matches, match, sourceAttribute, otherAttribute)) {
			MemberDiffer.executeCondition(SendableEntityCreator.NEW, changeConditions, match);
		} else {
			MemberDiffer.executeCondition(SendableEntityCreator.UPDATE, changeConditions, match);
			MemberDiffer.executeCondition(SendableEntityCreator.REMOVE, changeConditions, match);
		}
		return true;
	}

	private boolean addChange(GraphMatcher matches, Match match, Attribute sourceAttribute, Attribute otherAttribute) {
//		if (matches.getMetaModel() != null && (match.getOtherMatch().isMetaMatch() || match.isMetaMatch())) {
		if (matches.getMetaModel() != null && (match.isMetaSourceMatch() || match.isMetaMatch())) {
			return false;
		}
		if (otherAttribute == null) {
			return false;
		}
		if (sourceAttribute.getName().equals(otherAttribute.getName())) {
			if (sourceAttribute.getClazz().getName().equals(otherAttribute.getClazz().getName())) {
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
