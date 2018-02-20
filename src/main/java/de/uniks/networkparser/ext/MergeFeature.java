package de.uniks.networkparser.ext;

import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.interfaces.SimpleEventCondition;

public class MergeFeature extends FeatureProperty{
	public static final String IGNORE="ignore";
	public static final String OVERRIDE="override";
	public static final String CONFLICT="conflict";
	public static final String CUSTOM="custom";
	private SimpleEventCondition condition;

	protected MergeFeature(Feature name) {
		super(name);
	}

	/**
	 * @return the condition
	 */
	public SimpleEventCondition getCondition() {
		return condition;
	}
	/**
	 * @param condition the condition to set
	 * @return ThisComponent
	 */
	public MergeFeature withCondition(SimpleEventCondition condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public MergeFeature withStringValue(String value) {
		super.withStringValue(value);
		return this;
	}

	public static MergeFeature createIgnore() {
		return new MergeFeature(Feature.DIFFERENCE_BEHAVIOUR).withStringValue(IGNORE);
	}
	public static MergeFeature createOverride() {
		return new MergeFeature(Feature.DIFFERENCE_BEHAVIOUR).withStringValue(OVERRIDE);
	}
	public static MergeFeature createConflict() {
		return new MergeFeature(Feature.DIFFERENCE_BEHAVIOUR).withStringValue(CONFLICT);
	}
	public static MergeFeature createCustom() {
		return new MergeFeature(Feature.DIFFERENCE_BEHAVIOUR).withStringValue(CUSTOM);
	}
}
