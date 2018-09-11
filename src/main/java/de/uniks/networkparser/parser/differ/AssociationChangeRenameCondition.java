package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class AssociationChangeRenameCondition extends MatchCondition {

	public AssociationChangeRenameCondition() {
		super(true);
	}

	protected boolean checkCondition(GraphMatcher matches, Match match) {
		Association sourceAssociation = (Association) match.getMatch();
		Association otherAssociation = (Association) match.getOtherMatch().getMatch();
		
		return sourceAssociation.getOther().getName().equals(otherAssociation.getOther().getName()) == false;
	}
	
	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Association oldAssociation = (Association) match.getMatch();
		Association newAssociation = (Association) match.getOtherMatch().getMatch();
		Match rename = Match.create(newAssociation.getOther(), this, Association.PROPERTY_NAME, newAssociation.getOther().getName(), oldAssociation.getOther().getName());
		matches.addDiff(rename);
		return true;
	}
	
	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Association oldAssociation = (Association) match.getOtherMatch().getMatch();
		Association newAssociation = (Association) match.getMatch();
		
		Match rename = Match.create(oldAssociation.getOther(), this, Association.PROPERTY_NAME, oldAssociation.getOther().getName(), newAssociation.getOther().getName());
		matches.addDiff(rename);
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.UPDATE;
	}
}
