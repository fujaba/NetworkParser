package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class AssociationChangeUpdateCondition extends MatchCondition {

	public AssociationChangeUpdateCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		Association sourceAssociation = (Association) match.getMatch();
		Association otherAssociation = (Association) match.getSourceMatch();

		if (matches.getMetaModel() == null) {
			return false;
		}
		if (match.isMetaMatch() == false && match.isSourceMatch() == false) {
			return false;
		}
		if (sourceAssociation.getType().equals(otherAssociation.getType()) == false) {
			return false;
		}
		if (sourceAssociation.getOther().getCardinality() == otherAssociation.getOther().getCardinality()) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if (match == null || matches == null || match.getOtherMatch() == null) {
			return false;
		}
		Association oldAssociation = (Association) match.getMatch();
		Association newAssociation = (Association) match.getOtherMatch().getMatch();

		Match update = Match.create(newAssociation.getOther(), this, Association.PROPERTY_CARDINALITY,
				newAssociation.getOther().getCardinality(), oldAssociation.getOther().getCardinality());
		matches.addDiff(update);
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		Association oldAssociation = (Association) match.getOtherMatch().getMatch();
		Association newAssociation = (Association) match.getMatch();

		if (oldAssociation.getType().equals(newAssociation.getType()) == false) {
			return false;
		}
		if (oldAssociation.getOther().getCardinality() == newAssociation.getOther().getCardinality()) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		Association oldAssociation = (Association) match.getOtherMatch().getMatch();
		Association newAssociation = (Association) match.getMatch();

		Match update = Match.create(oldAssociation.getOther(), this, Association.PROPERTY_CARDINALITY,
				oldAssociation.getOther().getCardinality(), newAssociation.getOther().getCardinality());

		matches.addDiff(update);
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.UPDATE;
	}

}
