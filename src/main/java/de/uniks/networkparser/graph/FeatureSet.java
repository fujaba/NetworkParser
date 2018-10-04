package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

public class FeatureSet extends SimpleSet<Feature> {
	public FeatureSet with(Feature... features) {
		if (features == null) {
			return this;
		}
		for (Feature feature : features) {
			this.add(feature.create());
		}
		return this;
	}

	public Feature getFeature(Feature name) {
		for (Feature item : this) {
			Feature feature = (Feature) item;
			if (feature.equals(name) == false) {
				continue;
			}
			return feature;
		}
		return null;
	}

	public boolean match(Feature feature, Clazz clazz) {
		Feature property = getFeature(feature);
		if (property != null) {
			return property.match(clazz);
		}
		return false;
	}
}
