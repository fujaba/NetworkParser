package de.uniks.networkparser.graph;

import java.util.HashSet;
import java.util.LinkedHashSet;

import de.uniks.networkparser.list.SimpleSet;

public enum Feature {
	PROPERTYCHANGESUPPORT, PATTERNOBJECT, SERIALIZATION, SETCLASS, REMOVEYOUMETHOD, STANDALONE, EMFSTYLE, CODESTYLE;
	public static final String CODESTYLE_STANDARD = "standard";
	public static final String CODESTYLE_DIVIDED = "divided";

	public static final HashSet<FeatureProperty> getNone() {
		return new HashSet<FeatureProperty>();
	}

	public static SimpleSet<FeatureProperty> getAll() {
		SimpleSet<FeatureProperty> result = new SimpleSet<FeatureProperty>();
		result.add(PROPERTYCHANGESUPPORT.create());
		result.add(PATTERNOBJECT.create());
		result.add(SERIALIZATION.create());
		result.add(SETCLASS.create().withClazzValue(SimpleSet.class));
		result.add(REMOVEYOUMETHOD.create());
		result.add(CODESTYLE.create().withStringValue(CODESTYLE_STANDARD));
		return result;
	}

	public static SimpleSet<FeatureProperty> getStandAlone() {
		SimpleSet<FeatureProperty> result = new SimpleSet<FeatureProperty>();
		result.add(PROPERTYCHANGESUPPORT.create());
		// result.add(SERIALIZATION.create());
		result.add(SETCLASS.create().withClazzValue(LinkedHashSet.class));
		result.add(REMOVEYOUMETHOD.create());
		result.add(STANDALONE.create());
		result.add(CODESTYLE.create().withStringValue(CODESTYLE_STANDARD));
		return result;
	}

	public final FeatureProperty create() {
		return new FeatureProperty(this);
	}
}
