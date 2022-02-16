package de.uniks.networkparser.parser.differ;

import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.ParameterSet;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.GraphMatcher;

/**
 * The Class MethodChangeUpdateCondition.
 *
 * @author Stefan
 */
public class MethodChangeUpdateCondition extends MatchCondition {

	/**
	 * Instantiates a new method change update condition.
	 */
	public MethodChangeUpdateCondition() {
		super(true);
	}

	@Override
	protected boolean checkFileCondition(GraphMatcher matches, Match match) {
		return true;
	}

	@Override
	protected boolean calculateFileDiffs(GraphModel model, GraphMatcher matches, Match match) {
		Method oldMethod = (Method) match.getMatch();
		Method newMethod = (Method) match.getOtherMatch().getMatch();

		boolean withMeta = matches.getMetaModel() != null;

		ParameterSet oldParameters = new ParameterSet();
		ParameterSet newParameters = new ParameterSet();
		for (Parameter oldParameter : oldMethod.getParameters()) {
			for (Parameter newParameter : newMethod.getParameters()) {
				if (newParameters.contains(newParameter)) {
					continue;
				}
				if (!oldParameter.getName().equals(newParameter.getName())) {
					continue;
				}
				if ((withMeta && (match.isMetaMatch() || match.isMetaSourceMatch())) && (!oldParameter.getType().equals(newParameter.getType()))) {
					continue;
				}
				oldParameters.add(oldParameter);
				newParameters.add(newParameter);
				break;
			}
		}
		for (Parameter oldParameter : oldMethod.getParameters()) {
			if (oldParameters.contains(oldParameter)) {
				continue;
			}
			Match update = Match.create(newMethod, this, Method.PROPERTY_PARAMETER, null, oldParameter);

			matches.addDiff(update);
		}
/*		if(
				(oldMethod.getBody() != null && !oldMethod.getBody().equals(newMethod.getBody())) ||
				(newMethod.getBody() != null && !newMethod.getBody().equals(oldMethod.getBody())) 
				) {
			// MethodBody changed
			Diff update = new Diff(this)
					.withAction(SendableEntityCreator.UPDATE)
					.withEntity(newMethod)
					.withOldValue(oldMethod.getBody())
					.withNewValue(newMethod.getBody());
			matches.addDiff(update);
		}
*/

		if (withMeta && (match.isMetaMatch() || match.isMetaSourceMatch())) {
			for (Parameter newParameter : newMethod.getParameters()) {
				if (newParameters.contains(newParameter)) {
					continue;
				}
				Match update = Match.create(newMethod, this, Method.PROPERTY_PARAMETER, newParameter, null);
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
		Method oldMethod = (Method) match.getOtherMatch().getMatch();
		Method newMethod = (Method) match.getMatch();

		ParameterSet oldParameters = new ParameterSet();
		ParameterSet newParameters = new ParameterSet();
		for (Parameter oldParameter : oldMethod.getParameters()) {
			for (Parameter newParameter : newMethod.getParameters()) {
				if (newParameters.contains(newParameter)) {
					continue;
				}
				if (!oldParameter.getName().equals(newParameter.getName())) {
					if (matches.getMetaModel() == null || (!match.isMetaMatch() && !match.isMetaSourceMatch())) {
						oldParameters.add(oldParameter);
						break;
					}
					continue;
				}
				if (!oldParameter.getType().equals(newParameter.getType())) {
					continue;
				}
				oldParameters.add(oldParameter);
				newParameters.add(newParameter);
				break;
			}
		}
/*		if(
				(oldMethod.getBody() != null && !oldMethod.getBody().equals(newMethod.getBody())) ||
				(newMethod.getBody() != null && !newMethod.getBody().equals(oldMethod.getBody())) 
				) {
			// MethodBody changed
			Diff update = new Diff(this)
					.withAction(SendableEntityCreator.UPDATE)
					.withEntity(oldMethod)
					.withOldValue(oldMethod.getBody())
					.withNewValue(newMethod.getBody());
			matches.addDiff(update);
		}
*/
		for (Parameter oldParameter : oldMethod.getParameters()) {
			if (oldParameters.contains(oldParameter)) {
				continue;
			}
			Match update = Match.create(oldMethod, this, Method.PROPERTY_PARAMETER, oldParameter, null);

			matches.addDiff(update);
		}
		for (Parameter newParameter : newMethod.getParameters()) {
			if (newParameters.contains(newParameter)) {
				continue;
			}
			Match update = Match.create(oldMethod, this, Method.PROPERTY_PARAMETER, null, newParameter);
			matches.addDiff(update);
		}
		return true;
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
