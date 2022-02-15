package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class ClazzChangeUpdateCondition.
 *
 * @author Stefan
 */
public class ClazzChangeUpdateCondition extends MatchCondition {

	/**
	 * Instantiates a new clazz change update condition.
	 */
	public ClazzChangeUpdateCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Clazz oldClazz = (Clazz) match.getMatch();
		Clazz newClazz = (Clazz) match.getOtherMatch().getMatch();

		ClazzSet oldSuperClazzes = new ClazzSet();
		ClazzSet newSuperClazzes = new ClazzSet();
		ClazzSet oldInheritance = oldClazz.getSuperClazzes(false);
		oldInheritance.addAll(oldClazz.getInterfaces(false));
		ClazzSet newInheritance = newClazz.getSuperClazzes(false);
		newInheritance.addAll(newClazz.getInterfaces(false));
		for (Clazz oldSuperClazz : oldInheritance) {
			for (Clazz newSuperClazz : newInheritance) {
				if (newSuperClazzes.contains(newSuperClazz)) {
					continue;
				}
				if (oldSuperClazz.getName().equals(newSuperClazz.getName()) == false) {
					if (matches.getMetaModel() != null) {
						Match superMatch = matches.getClazzMatch(newSuperClazz);
						if (superMatch.isMetaMatch() == false) {
							continue;
						}
						if (superMatch.getSourceMatch() == oldSuperClazz == false) {
							continue;
						}
						if (superMatch.isMetaMatch() == false && superMatch.isSourceMatch() == false) {
							continue;
						}
					} else {
						continue;
					}
				}

				oldSuperClazzes.add(oldSuperClazz);
				newSuperClazzes.add(newSuperClazz);
				break;
			}
		}
		for (Clazz oldSuperClazz : oldInheritance) {
			if (oldSuperClazzes.contains(oldSuperClazz)) {
				continue;
			}
			if (determineAddableSuperclazz(matches, match, oldSuperClazz)) {
				continue;
			}

			oldSuperClazzes.add(oldSuperClazz);
		}
		for (Clazz newSuperClazz : newInheritance) {
			if (newSuperClazzes.contains(newSuperClazz)) {
				continue;
			}
			if (determineNonRemovableSuperClazz(matches, match, newSuperClazz) == false) {
				continue;
			}

			newSuperClazzes.add(newSuperClazz);
		}

		for (Clazz oldSuperClazz : oldInheritance) {
			if (oldSuperClazzes.contains(oldSuperClazz)) {
				continue;
			}
			Match update = Match.create(newClazz, this, Clazz.PROPERTY_SUPERCLAZZ, null, oldSuperClazz);

			matches.addDiff(update);
		}
		if (matches.getMetaModel() != null) {
			for (Clazz newSuperClazz : newInheritance) {
				if (newSuperClazzes.contains(newSuperClazz)) {
					continue;
				}
				Match update = Match.create(newClazz, this, Clazz.PROPERTY_SUPERCLAZZ, newSuperClazz, null);
				matches.addDiff(update);
			}
		}
		return true;
	}

	@Override
	protected boolean checkModelCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateModelDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Clazz oldClazz = (Clazz) match.getOtherMatch().getMatch();
		Clazz newClazz = (Clazz) match.getMatch();

		boolean withMeta = matches.getMetaModel() != null;

		ClazzSet oldSuperClazzes = new ClazzSet();
		ClazzSet newSuperClazzes = new ClazzSet();
		ClazzSet oldInheritance = oldClazz.getSuperClazzes(false);
		oldInheritance.addAll(oldClazz.getInterfaces(false));
		ClazzSet newInheritance = newClazz.getSuperClazzes(false);
		newInheritance.addAll(newClazz.getInterfaces(false));
		for (Clazz oldSuperClazz : oldInheritance) {
			for (Clazz newSuperClazz : newInheritance) {
				if (newSuperClazzes.contains(newSuperClazz)) {
					continue;
				}
				if (oldSuperClazz.getName().equals(newSuperClazz.getName()) == false) {
					if (withMeta) {
						Match superMatch = matches.getClazzMatch(oldSuperClazz);
						if (superMatch.isMetaMatch() == false) {
							continue;
						}
						if (superMatch.getSourceMatch() == newSuperClazz == false) {
							continue;
						}
						if (superMatch.isMetaMatch() == false && superMatch.isSourceMatch() == false) {
							continue;
						}
					} else {
						continue;
					}
				}
				oldSuperClazzes.add(oldSuperClazz);
				newSuperClazzes.add(newSuperClazz);
				break;
			}
		}
		for (Clazz oldSuperClazz : oldInheritance) {
			if (oldSuperClazzes.contains(oldSuperClazz)) {
				continue;
			}
			if (determineNonRemovableSuperClazz(matches, match, oldSuperClazz) == false) {
				continue;
			}

			oldSuperClazzes.add(oldSuperClazz);
		}
		for (Clazz newSuperClazz : newInheritance) {
			if (newSuperClazzes.contains(newSuperClazz)) {
				continue;
			}
			if (determineAddableSuperclazz(matches, match, newSuperClazz)) {
				continue;
			}

			newSuperClazzes.add(newSuperClazz);
		}

		for (Clazz oldSuperClazz : oldInheritance) {
			if (oldSuperClazzes.contains(oldSuperClazz)) {
				continue;
			}
			Match update = Match.create(oldClazz, this, Clazz.PROPERTY_SUPERCLAZZ, oldSuperClazz, null);
			matches.addDiff(update);
		}
		for (Clazz newSuperClazz : newInheritance) {
			if (newSuperClazzes.contains(newSuperClazz)) {
				continue;
			}
			Match superMatch = matches.getClazzMatch(newSuperClazz);
			Clazz destination = newSuperClazz;
			if (superMatch.isMetaMatch() && superMatch.isMetaMatch() && superMatch.isSourceMatch() == false) {
				if (oldInheritance.contains(superMatch.getSourceMatch())) {
					continue;
				}
				destination = (Clazz) superMatch.getSourceMatch();
			}
			Match update = Match.create(oldClazz, this, Clazz.PROPERTY_SUPERCLAZZ, null, destination);
			matches.addDiff(update);
		}
		return true;
	}

	private boolean determineAddableSuperclazz(GraphMatcher matches, Match match, Clazz superClazz) {
		Match superMatch = matches.getClazzMatch(superClazz);

		if (superMatch.isMetaMatch() == false) {
			return true;
		}
		if (superMatch.isMetaMatch()) {
			return true;
		}

		return false;
	}

	private boolean determineNonRemovableSuperClazz(GraphMatcher matches, Match match, Clazz superClazz) {
		if (matches.getMetaModel() == null) {
			return true;
		}
		if (match.isSourceMatch() == false) {
			Match superMatch = matches.getClazzMatch(superClazz);

			if (superMatch.isMetaMatch() == false) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	@Override
	public String getAction() {
		return SendableEntityCreator.UPDATE;
	}

}
