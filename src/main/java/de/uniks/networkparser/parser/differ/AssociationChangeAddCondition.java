package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class AssociationChangeAddCondition extends MatchCondition {

	public AssociationChangeAddCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if(match == null || matches == null || match.getOtherMatch() == null) {
			return false;
		}
		Association oldAssociation = (Association) match.getMatch();
		Association newAssociation = (Association) match.getOtherMatch().getMatch();

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
		if(match == null || matches == null || match.getOtherMatch() == null) {
			return false;
		}
		Association oldAssociation = (Association) match.getOtherMatch().getMatch();
		Association newAssociation = (Association) match.getMatch();
		Match add = Match.create(oldAssociation.getClazz(), this, Clazz.PROPERTY_ASSOCIATION, null, newAssociation);

		matches.addDiff(add);
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.NEW;
	}
}
