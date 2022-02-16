package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class AddCondition.
 *
 * @author Stefan
 */
public class AddCondition extends MatchCondition {

	/**
	 * Instantiates a new adds the condition.
	 */
	public AddCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return checkCondition(matches, match);
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		GraphMember member = match.getMatch();
		if (member == null) {
			return false;
		}
		Clazz clazz = member.getClazz();
		if (member instanceof Clazz) {
			Match addToCode = Match.create(model, this, GraphModel.PROPERTY_CLAZZ, null, clazz);

			matches.addDiff(addToCode);

			for (String modifier : clazz.getModifier().toString().split(" ")) {
				if (!modifier.equals(Modifier.PUBLIC.getName())) {
					Match addModifierInCode = Match.create(clazz, this, Clazz.PROPERTY_MODIFIERS, null, modifier);
					matches.addDiff(addModifierInCode);
				}
			}

			if (clazz.getType().equals(Clazz.TYPE_INTERFACE)) {
				Match updateTypeInCode = Match.create(clazz, this, Clazz.PROPERTY_TYPE, Clazz.TYPE_CLASS,
						Clazz.TYPE_INTERFACE);
				matches.addDiff(updateTypeInCode);
			}
			return true;
		}

		Match clazzMatch = matches.getClazzMatch(clazz);
		if (member instanceof Association) {
			Clazz destination = clazz;
			Match otherMatch = matches.getClazzMatch(clazz);
			if (otherMatch.isMetaMatch()) {
				destination = (Clazz) otherMatch.getMetaMatch();
			}

			Match addToCode = Match.create(destination, this, Clazz.PROPERTY_ASSOCIATION, null, member);
			matches.addDiff(addToCode);
			return true;
		}
		if (clazzMatch.isMetaMatch()) {
			Clazz destination = (Clazz) clazzMatch.getMetaMatch();

			Match addToCode = Match.create(destination, this, GraphMember.PROPERTY_CHILD, null, member);
			matches.addDiff(addToCode);
		}
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		if (match.getMatch() instanceof Association) {
			return checkCondition(matches, match);
		}
		return true;
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		GraphMember member = match.getMatch();
		Clazz clazz = member.getClazz();
		if (member instanceof Clazz) {
			Match add = Match.create(model, this, GraphModel.PROPERTY_CLAZZ, null, clazz);
			matches.addDiff(add);

			for (String modifier : clazz.getModifier().toString().split(" ")) {
				if (!modifier.equals(Modifier.PUBLIC.getName())) {
					Match addModifier = Match.create(clazz, this, Clazz.PROPERTY_MODIFIERS, null, modifier);
					matches.addDiff(addModifier);
				}
			}

			if (clazz.getType().equals(Clazz.TYPE_INTERFACE)) {
				Match updateType = Match.create(clazz, this, Clazz.PROPERTY_TYPE, Clazz.TYPE_CLASS,
						Clazz.TYPE_INTERFACE);
				matches.addDiff(updateType);
			}
			return true;
		}

		if (member instanceof Association) {
			Clazz destination = clazz;
			Match otherMatch = matches.getClazzMatch(clazz);
			if (otherMatch.isMetaMatch()) {
				destination = (Clazz) otherMatch.getMetaMatch();
			}
			if (otherMatch != null) {
				destination = (Clazz) otherMatch.getMatch();
			}

			Match add = Match.create(destination, this, Clazz.PROPERTY_ASSOCIATION, null, member);
			matches.addDiff(add);
			return true;
		}

		Match clazzMatch = matches.getClazzMatch(clazz).getOtherMatch();
		if (clazzMatch != null) {
			Clazz destination = (Clazz) clazzMatch.getMatch();
			Match add = Match.create(destination, this, GraphMember.PROPERTY_CHILD, null, member);
			matches.addDiff(add);
		}
		return true;
	}

	protected boolean checkCondition(GraphMatcher matches, Match match) {
		if (match == null || matches == null) {
			return false;
		}
		GraphMember member = match.getMatch();
		if (member instanceof Association) {
			Association association = (Association) match.getMatch();

			if (((association.getType().equals(AssociationTypes.EDGE)
					&& association.getOther().getType().equals(AssociationTypes.UNDIRECTIONAL))
					|| (association.getType().equals(AssociationTypes.ASSOCIATION)
							&& !association.getOther().getType().equals(AssociationTypes.ASSOCIATION)))) {
				return false;
			}
		}
		return super.checkCondition(matches, match);
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	@Override
	public String getAction() {
		return SendableEntityCreator.NEW;
	}

}
