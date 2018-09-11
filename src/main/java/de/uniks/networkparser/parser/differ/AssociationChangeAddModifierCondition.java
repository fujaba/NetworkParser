package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class AssociationChangeAddModifierCondition extends MatchCondition {

	public AssociationChangeAddModifierCondition() {
		super(true);
	}

	protected boolean checkCondition(GraphMatcher matches, Match match) {
		Association sourceAssociation = (Association) match.getMatch();
		Association otherAssociation = (Association) match.getOtherMatch().getMatch();
		
		return checkAssociationModifiers(sourceAssociation, otherAssociation) == false;
	}
	
	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Association oldAssociation = (Association) match.getMatch();
		Association newAssociation = (Association) match.getOtherMatch().getMatch();
		
		for (String modifier : newAssociation.getModifier().toString().split(" ")) {
			if (oldAssociation.getModifier().toString().contains(modifier) == false) {
				Match addModifier = Match.create(newAssociation, this, Association.PROPERTY_MODIFIERS, null, modifier);
				matches.addDiff(addModifier);
			}
		}
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
		
		for (String modifier : newAssociation.getModifier().toString().split(" ")) {
			if (oldAssociation.getModifier().toString().contains(modifier) == false) {
				Match addModifier = Match.create(oldAssociation, this, Association.PROPERTY_MODIFIERS, null, modifier);
				matches.addDiff(addModifier);
			}
		}
		return true;
	}
	
	private boolean checkAssociationModifiers(Association oldAssociation, Association newAssociation) {
		Modifier oldModifier = oldAssociation.getModifier();
		Modifier newModifier = newAssociation.getModifier();
		
		if (oldModifier == null && newModifier == null) {
			return true;
		} else if (oldModifier == null || newModifier == null) {
			return false;
		} else {
			return oldModifier.toString().equals(newModifier.toString());
		}
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.UPDATE;
	}
}
