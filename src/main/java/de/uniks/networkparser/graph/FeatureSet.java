package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class FeatureSet.
 *
 * @author Stefan
 */
public class FeatureSet extends SimpleSet<Feature> {
	
	/**
	 * With.
	 *
	 * @param features the features
	 * @return the feature set
	 */
	public FeatureSet with(Feature... features) {
		if (features == null) {
			return this;
		}
		for (Feature feature : features) {
			if (feature != null) {
				this.add(feature.create());
			}
		}
		return this;
	}

	/**
	 * Gets the feature.
	 *
	 * @param name the name
	 * @return the feature
	 */
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

	/**
	 * Match.
	 *
	 * @param feature the feature
	 * @param clazz the clazz
	 * @return true, if successful
	 */
	public boolean match(Feature feature, Clazz clazz) {
		Feature property = getFeature(feature);
		if (property != null) {
			return property.match(clazz);
		}
		return false;
	}
}
