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
		if (match == null || matches == null) {
			return false;
		}
		GraphMember oldMatch = match.getMatch();
		GraphMember newMatch = match.getSourceMatch();
		if (newMatch == null) {
			return false;
		}
		Clazz newClazz = newMatch.getClazz();
		if (oldMatch instanceof Clazz) {
			Match addToCode = Match.create(model, this, GraphModel.PROPERTY_CLAZZ, null, oldMatch);
			matches.addDiff(addToCode);

			for (String modifier : oldMatch.getModifier().toString().split(" ")) {
				if (modifier.equals("public") == false) {
					Match addModifierInCode = Match.create(oldMatch, this, Clazz.PROPERTY_MODIFIERS, null, modifier);
					matches.addDiff(addModifierInCode);
				}
			}
			Clazz oldClazz = (Clazz) oldMatch;
			if (oldClazz.getType().equals(Clazz.TYPE_CLASS) == false) {
				Match updateType = Match.create(oldMatch, this, Clazz.PROPERTY_TYPE, newClazz.getType(),
						oldClazz.getType());
				matches.addDiff(updateType);
			}
			return true;
		}
		Match clazzMatch = matches.getClazzMatch(newClazz);

		if (clazzMatch.isOtherMatch() == false
				|| GraphUtil.compareName(newClazz.getName(), clazzMatch.getSourceMatch().getName()) >= 1) {
			newClazz = oldMatch.getClazz();
		}
		Match addToCode = Match.create(newClazz, this, Clazz.PROPERTY_ASSOCIATION, null, oldMatch);
		matches.addDiff(addToCode);
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if (match == null || matches == null || match.getOtherMatch() == null) {
			return false;
		}
//		GraphMember oldAttribute = match.getSourceMatch();
		GraphMember oldMatch = match.getOtherMatch().getMatch();
		GraphMember newMatch = match.getMatch();
		Clazz oldClazz = oldMatch.getClazz();
		if (oldMatch instanceof Clazz) {
			Match add = Match.create(model, this, GraphModel.PROPERTY_CLAZZ, null, newMatch);
			matches.addDiff(add);

			for (String modifier : newMatch.getModifier().toString().split(" ")) {
				if (modifier.equals("public") == false) {
					Match addModifier = Match.create(newMatch, this, Clazz.PROPERTY_MODIFIERS, null, modifier);
					matches.addDiff(addModifier);
				}
			}
			Clazz newClazz = (Clazz) newMatch;
			if (newClazz.getType().equals(Clazz.TYPE_CLASS) == false) {
				Match updateType = Match.create(newMatch, this, Clazz.PROPERTY_MODIFIERS, oldClazz.getType(),
						newClazz.getType());
				matches.addDiff(updateType);
			}
			return true;
		}

		Match clazzMatch = matches.getClazzMatch(oldClazz);

		if (clazzMatch.isOtherMatch() == false
				|| GraphUtil.compareName(oldClazz.getName(), clazzMatch.getSourceMatch().getName()) >= 1) {
			oldClazz = newMatch.getClazz();
		}
		Match add = Match.create(oldClazz, this, Clazz.PROPERTY_ASSOCIATION, null, newMatch);
		matches.addDiff(add);
		return true;
	}

	@Override
	public String getAction() {
		return SendableEntityCreator.NEW;
	}
}
