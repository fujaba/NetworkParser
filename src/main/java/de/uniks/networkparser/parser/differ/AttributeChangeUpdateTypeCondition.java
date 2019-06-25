package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class AttributeChangeUpdateTypeCondition extends MatchCondition {
	public AttributeChangeUpdateTypeCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		Attribute oldAttribute = (Attribute) match.getMatch();
		Attribute newAttribute = (Attribute) match.getOtherMatch().getMatch();

		if (match.isMetaMatch() == false && match.isSourceMatch() == false) {
			return false;
		}
		if (oldAttribute.getType().equals(newAttribute.getType())) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if (match == null || matches == null || match.getOtherMatch() == null) {
			return false;
		}
		Attribute oldAttribute = (Attribute) match.getMatch();
		Attribute newAttribute = (Attribute) match.getOtherMatch().getMatch();

		Match rename = Match.create(newAttribute, this, Attribute.PROPERTY_TYPE, newAttribute.getType(),
				oldAttribute.getType());
		matches.addDiff(rename);
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		Attribute oldAttribute = (Attribute) match.getOtherMatch().getMatch();
		Attribute newAttribute = (Attribute) match.getMatch();
		if (oldAttribute == null || newAttribute == null) {
			return false;
		}
		return oldAttribute.getType().equals(newAttribute.getType()) == false;
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if (match == null || matches == null || match.getOtherMatch() == null) {
			return false;
		}
		Attribute oldAttribute = (Attribute) match.getOtherMatch().getMatch();
		Attribute newAttribute = (Attribute) match.getMatch();

		Match update = Match.create(oldAttribute, this, Attribute.PROPERTY_TYPE, oldAttribute.getType(),
				newAttribute.getType());
		matches.addDiff(update);
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.UPDATE;
	}
}
