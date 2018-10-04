package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class AssociationChangeCondition extends MatchCondition {
	public AssociationChangeCondition() {
		changeConditions.add(new AssociationChangeRenameCondition());
		changeConditions.add(new AssociationChangeAddCondition());
		changeConditions.add(new AssociationChangeAddModifierCondition());
		changeConditions.add(new AssociationChangeRemoveModifierCondition());
		changeConditions.add(new AssociationChangeUpdateTypeCondition());
		changeConditions.add(new AssociationChangeUpdateCondition());
	}

	@Override
	protected boolean checkCondition(GraphMatcher matches, Match match) {
		Association association = (Association) match.getMatch();

		if (association.getType().equals(AssociationTypes.UNDIRECTIONAL)
				&& association.getOther().getType().equals(AssociationTypes.EDGE)) {
			if (association.getClazz() != association.getOtherClazz()) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected boolean calculateDiffs(GraphMatcher matches, Match match) {
		Association sourceAssociation = (Association) match.getMatch();
		Association otherAssociation = (Association) match.getOtherMatch().getMatch();

		if (addChange(matches, match, sourceAssociation, otherAssociation)) {
			MemberDiffer.executeCondition(SendableEntityCreator.NEW, changeConditions, match);
		} else {
			if (updateType(matches, match, sourceAssociation, otherAssociation)) {
				MemberDiffer.executeCondition(SendableEntityCreator.UPDATE, changeConditions, match);
			} else {
				MemberDiffer.executeCondition(SendableEntityCreator.UPDATE, changeConditions, match);
				MemberDiffer.executeCondition(SendableEntityCreator.REMOVE, changeConditions, match);
			}
		}
		return true;
	}

	private boolean addChange(GraphMatcher matches, Match match, Association sourceAssociation,
			Association otherAssociation) {
		if (matches.getMetaModel() != null && (match.isSourceMatch() || match.isMetaMatch())) {
			return false;
		}
		if (checkSimiliarNames(sourceAssociation, otherAssociation)) {
			return false;
		}

		return true;
	}

	private boolean updateType(GraphMatcher matches, Match match, Association sourceAssociation,
			Association otherAssociation) {
		return sourceAssociation.getOtherClazz().getName().equals(otherAssociation.getOtherClazz().getName()) == false
				|| sourceAssociation.getClazz().getName().equals(otherAssociation.getClazz().getName()) == false
				|| sourceAssociation.getType().equals(otherAssociation.getType()) == false;
	}

	private boolean checkSimiliarNames(Association sourceAssociation, Association otherAssociation) {
		if (sourceAssociation.getType().equals(AssociationTypes.UNDIRECTIONAL)
				|| otherAssociation.getType().equals(AssociationTypes.UNDIRECTIONAL)) {
			return sourceAssociation.getName().equals(otherAssociation.getName());
		}

		return sourceAssociation.getOther().getName().equals(otherAssociation.getOther().getName());
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.UPDATE;
	}
}
