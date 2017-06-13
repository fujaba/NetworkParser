package de.uniks.networkparser.graph;

import java.util.HashSet;
import java.util.LinkedHashSet;

import de.uniks.networkparser.graph.util.FeatureSet;
import de.uniks.networkparser.list.SimpleSet;

public enum Feature {
	PROPERTYCHANGESUPPORT, PATTERNOBJECT, SERIALIZATION, SETCLASS, REMOVEYOUMETHOD, STANDALONE, EMFSTYLE, CODESTYLE;
	public static final String CODESTYLE_STANDARD = "standard";
	public static final String CODESTYLE_DIVIDED = "divided";

	public static final HashSet<FeatureProperty> getNone() {
		return new HashSet<FeatureProperty>();
	}

	public static FeatureSet getAll() {
		FeatureSet result = new FeatureSet().with(PROPERTYCHANGESUPPORT, PATTERNOBJECT, SERIALIZATION, REMOVEYOUMETHOD, SETCLASS);
		result.add(CODESTYLE.create().withStringValue(CODESTYLE_STANDARD));
		return result;
	}

	public static FeatureSet getStandAlone() {
		FeatureSet result = new FeatureSet().with(PROPERTYCHANGESUPPORT, STANDALONE, REMOVEYOUMETHOD);
		result.add(SETCLASS.create().withClazzValue(LinkedHashSet.class));
		result.add(CODESTYLE.create().withStringValue(CODESTYLE_STANDARD));
		return result;
	}

	public final FeatureProperty create() {
		if(this==Feature.SETCLASS) {
			return new FeatureProperty(this).withClazzValue(SimpleSet.class);
		}
		return new FeatureProperty(this);
	}
}
