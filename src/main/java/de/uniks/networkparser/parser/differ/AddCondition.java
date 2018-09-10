package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class AddCondition extends MatchCondition {

	public AddCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		GraphMember method = match.getMatch();
		Clazz clazz = method.getClazz();
		Match clazzMatch = matches.getClazzMatch(clazz);
		if(clazzMatch.isMetaMatch()) {
			Clazz destination = (Clazz) clazzMatch.getMetaMatch();

			Match addToCode = Match.create(destination, this, GraphMember.PROPERTY_CHILD, null, method);
			matches.addDiff(addToCode);
		}
		return true;
	}
	
	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		GraphMember method = match.getMatch();
		Clazz clazz = method.getClazz();
		Match clazzMatch = matches.getClazzMatch(clazz).getOtherMatch();
		if(clazzMatch != null) {
//		if(clazzMatch.isMetaMatch()) {
			Clazz destination = (Clazz) clazzMatch.getMatch();

			Match add = Match.create(destination, this, GraphMember.PROPERTY_CHILD, null, method);
			matches.addDiff(add);
		}
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.NEW;
	}

}
