package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

public class ChangeAddCondition extends MatchCondition {

	public ChangeAddCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		GraphMember oldAttribute = match.getMatch();
		GraphMember newAttribute = match.getSourceMatch();

		Clazz clazz = newAttribute.getClazz();

		Match clazzMatch = matches.getClazzMatch(clazz);
		
		if (clazzMatch.isOtherMatch() == false
				|| GraphUtil.compareName(clazz.getName(), clazzMatch.getSourceMatch().getName()) >= 1) {
			clazz = oldAttribute.getClazz();
		}
		Match addToCode = Match.create(clazz, this, Clazz.PROPERTY_ASSOCIATION, null, oldAttribute);
		matches.addDiff(addToCode);
		return true;
	}
	
	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		GraphMember oldAttribute = match.getSourceMatch();
//		Attribute oldAttribute = (Attribute) match.getOtherMatch().getParent();
		GraphMember newAttribute = match.getMatch();
		
		Clazz clazz = oldAttribute.getClazz();

		Match clazzMatch = matches.getClazzMatch(clazz);
		
		if (clazzMatch.isOtherMatch() == false
				|| GraphUtil.compareName(clazz.getName(), clazzMatch.getSourceMatch().getName()) >= 1) {
			clazz = newAttribute.getClazz();
		}
		Match add = Match.create(clazz, this, Clazz.PROPERTY_ASSOCIATION, null, newAttribute);
		matches.addDiff(add);
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.NEW;
	}

}
