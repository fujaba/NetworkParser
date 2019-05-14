package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class RemoveCondition extends MatchCondition {

	public RemoveCondition() {
		super(false);
	}

	protected boolean checkCondition(GraphMatcher matches, Match match) {
		if (matches == null || matches.getMetaModel() == null) {
			return false;
		}
		GraphMember member = match.getMatch();
		if (member instanceof Association) {
			return checkConditionAssociation(matches, match, (Association) member);
		}
		if (match.isMetaMatch() == false) {
			return false;
		}
		if (member instanceof Clazz && match.isSourceMatch() == false) {
			return false;
		}

		return true;
	}

	protected boolean checkConditionAssociation(GraphMatcher matches, Match match, Association association) {
		if(association == null) {
			return false;
		}
		if (association.getClazz() == association.getOtherClazz()) {
			if (((association.getType().equals(AssociationTypes.UNDIRECTIONAL)
					&& association.getOther().getType().equals(AssociationTypes.EDGE))
					|| (association.getType().equals(AssociationTypes.ASSOCIATION)
							&& association.getOther().getType().equals(AssociationTypes.ASSOCIATION))) == false) {
				return false;
			}
		} else {
			if (((association.getType().equals(AssociationTypes.EDGE)
					&& association.getOther().getType().equals(AssociationTypes.UNDIRECTIONAL))
					|| (association.getType().equals(AssociationTypes.ASSOCIATION)
							&& association.getOther().getType().equals(AssociationTypes.ASSOCIATION))) == false) {
				return false;
			}
		}

		if (match.isMetaMatch()) {
			if (association.getType().equals(AssociationTypes.EDGE) == false) {
				if (association.getType().equals(AssociationTypes.ASSOCIATION) == false) {
					return false;
				}
			}
			Match otherMatch = matches.getAssociationMatch(association.getOther());
			if (otherMatch.isMetaMatch()) {
				if (otherMatch.isMetaMatch() == false || otherMatch.isMetaSourceMatch() == false) {
					return false;
				}
			} else if (association.getType().equals(AssociationTypes.EDGE)) {
				Association otherAssociation = (Association) match.getSourceMatch();
				if (Double.compare(
						GraphUtil.compareName(association.getOther().getName(), otherAssociation.getOther().getName()),
						3) == -1) {
					return false;
				}
			}
//			if (match.getMetaParent() == match.getOtherMatch().getMetaParent()) {
//				return false;
//			}
		}

		return true;
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if(match == null || matches == null) {
			return false;
		}
		GraphMember member = match.getMatch();
		Match remove;
		if (member instanceof Association) {
			Association association = (Association) member;

			if (association.getClazz() == association.getOtherClazz()) {
				remove = Match.create(association.getOther().getClazz(), this, Clazz.PROPERTY_ASSOCIATION,
						association.getOther(), null);
			} else {
				remove = Match.create(association.getClazz(), this, Clazz.PROPERTY_ASSOCIATION, association, null);

				if ((association.getType().equals(AssociationTypes.EDGE)
						&& association.getOther().getType().equals(AssociationTypes.UNDIRECTIONAL))) {
					matches.addDiff(remove);
					remove = Match.create(association.getOtherClazz(), this, Clazz.PROPERTY_ASSOCIATION,
							association.getOther(), null);
				}
			}
			matches.addDiff(remove);
			return true;
		}
		if (member instanceof Clazz) {
			remove = Match.create(((Clazz) member).getClassModel(), this, GraphModel.PROPERTY_CLAZZ, member, null);
		} else {
			remove = Match.create(member.getClazz(), this, Clazz.PROPERTY_CHILD, member, null);
		}
		matches.addDiff(remove);
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if(match == null) {
			return false;
		}
		GraphMember member = match.getMatch();
		Match remove;
		if (member instanceof Association) {
			Association association = (Association) member;
			if (association.getClazz() == association.getOtherClazz()) {
				remove = Match.create(association.getOtherClazz(), this, Clazz.PROPERTY_ASSOCIATION,
						association.getOther(), null);
			} else {
				remove = Match.create(association.getClazz(), this, Clazz.PROPERTY_ASSOCIATION, association, null);

				if ((association.getType().equals(AssociationTypes.EDGE)
						&& association.getOther().getType().equals(AssociationTypes.UNDIRECTIONAL))) {
					matches.addDiff(remove);
					remove = Match.create(association.getOtherClazz(), this, Clazz.PROPERTY_ASSOCIATION,
							association.getOther(), null);
				}
			}
			matches.addDiff(remove);
			return true;
		}
		if (member instanceof Clazz) {
			remove = Match.create(((Clazz) member).getClassModel(), this, GraphModel.PROPERTY_CLAZZ, member, null);
		} else {
			remove = Match.create(member.getClazz(), this, Clazz.PROPERTY_CHILD, member, null);
		}
		matches.addDiff(remove);
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.REMOVE;
	}
}
