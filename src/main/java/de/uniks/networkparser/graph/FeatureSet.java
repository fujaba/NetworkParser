package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

public class FeatureSet extends SimpleSet<FeatureProperty>{
	public FeatureSet with(Feature... features) {
		if(features ==null) {
			return this;
		}
		for(Feature feature : features) {
			this.add(feature.create());
		}
		return this;
	}

	public FeatureProperty getFeatureProperty(Feature name) {
		for(FeatureProperty item : this) {
			FeatureProperty feature = (FeatureProperty) item;
			if(feature.getName() != name) {
				continue;
			}
			return feature;
		}
		return null;
	}

	public boolean match(Feature feature, Clazz clazz) {
		FeatureProperty property = getFeatureProperty(feature);
		if(property != null) {
			return property.match(clazz);
		}
		return false;
	}
}
