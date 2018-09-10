package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class RemoveCondition extends MatchCondition {

	public RemoveCondition() {
		super(false);
	}

	protected boolean checkCondition(GraphMatcher matches, Match match) {
		if (matches.getMetaModel() == null) {
			return false;
		}
		
		if (match.isMetaMatch() == false) {
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		GraphMember member = match.getMatch();
		Match removeFromCode = Match.create(member.getClazz(), this, Clazz.PROPERTY_CHILD, member, null);

		matches.addDiff(removeFromCode);
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		GraphMember member = match.getMatch();
		Match remove = Match.create(member.getClazz(), this, Clazz.PROPERTY_CHILD, member, null);
		matches.addDiff(remove);
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.REMOVE;
	}
}
