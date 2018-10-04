package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class AssociationChangeUpdateTypeCondition extends MatchCondition {

	public AssociationChangeUpdateTypeCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		if (matches.getMetaModel() == null) {
			return false;
		}
		if (match.isMetaMatch() == false && match.isMetaSourceMatch() == false) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Association oldAssociation = (Association) match.getMatch();
		Association newAssociation = (Association) match.getSourceMatch();

		if ((oldAssociation.getType().equals(AssociationTypes.EDGE)
				&& oldAssociation.getOther().getType().equals(AssociationTypes.UNDIRECTIONAL))
				|| oldAssociation.getType().equals(newAssociation.getType()) == false
				|| oldAssociation.getOther().getType().equals(newAssociation.getOther().getType()) == false) {
			Match remove = Match.create(newAssociation.getClazz(), this, Clazz.PROPERTY_ASSOCIATION, newAssociation,
					null);

			matches.addDiff(remove);

			if (oldAssociation.getClazz().getName() != newAssociation.getClazz().getName()
					|| (oldAssociation.getClazz().getName().equals(newAssociation.getClazz().getName())
							&& oldAssociation.getType().equals(AssociationTypes.EDGE))) {
//				GraphDiff removeOther = GraphDiff.create(newAssociation.getOtherClazz(), SendableEntityCreator.UPDATE, this, newAssociation.getOther(), null);
				Match removeOther = Match.create(newAssociation.getOtherClazz(), this, Clazz.PROPERTY_ASSOCIATION,
						newAssociation.getOther(), null);

				matches.addDiff(removeOther);
			}
		}
		Match add = Match.create(newAssociation.getClazz(), this, Clazz.PROPERTY_ASSOCIATION, null, oldAssociation);

		matches.addDiff(add);
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Association oldAssociation = (Association) match.getOtherMatch().getMatch();
		Association newAssociation = (Association) match.getMatch();

		if ((oldAssociation.getType().equals(AssociationTypes.EDGE)
				&& oldAssociation.getOther().getType().equals(AssociationTypes.UNDIRECTIONAL))
				|| (oldAssociation.getType().equals(AssociationTypes.ASSOCIATION)
						&& oldAssociation.getOther().getType().equals(AssociationTypes.ASSOCIATION))) {
			if ((oldAssociation.getType().equals(AssociationTypes.EDGE)
					&& oldAssociation.getOther().getType().equals(AssociationTypes.UNDIRECTIONAL))
					|| oldAssociation.getType().equals(newAssociation.getType()) == false
					|| oldAssociation.getOther().getType().equals(newAssociation.getOther().getType()) == false) {
				boolean removeAssoc = true;
				if (oldAssociation.getType().equals(AssociationTypes.ASSOCIATION)
						&& oldAssociation.getOther().getType().equals(AssociationTypes.ASSOCIATION)
						&& oldAssociation.getClazz() == oldAssociation.getOtherClazz()) {
					if (newAssociation.getType().equals(AssociationTypes.UNDIRECTIONAL)) {
						removeAssoc = false;
					}
				}
				if (removeAssoc) {
					Match remove = Match.create(oldAssociation.getClazz(), this, Clazz.PROPERTY_ASSOCIATION,
							oldAssociation, null);
					matches.addDiff(remove);

					if (oldAssociation.getClazz().getName() != newAssociation.getClazz().getName()
							|| (oldAssociation.getClazz().getName().equals(newAssociation.getClazz().getName())
									&& (oldAssociation.getType().equals(AssociationTypes.EDGE)
											|| newAssociation.getType().equals(AssociationTypes.EDGE)))) {
						Match removeOther = Match.create(oldAssociation.getOtherClazz(), this,
								Clazz.PROPERTY_ASSOCIATION, oldAssociation.getOther(), null);

						matches.addDiff(removeOther);
					}
				}
			}
		}

		Match add = Match.create(oldAssociation.getClazz(), this, Clazz.PROPERTY_ASSOCIATION, null, newAssociation);
		matches.addDiff(add);
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.UPDATE;
	}
}
