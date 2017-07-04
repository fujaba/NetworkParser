package de.uniks.networkparser.ext;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.FeatureProperty;
import de.uniks.networkparser.graph.GraphModel;

public class ClassModel extends GraphModel {
	public static final String DEFAULTPACKAGE = "i.love.sdmlib";
	private ModelGenerator generator = new ModelGenerator().withDefaultModel(this);

	public ClassModel() {
		name = DEFAULTPACKAGE;
		setAuthorName(System.getProperty("user.name"));
	}

	/**
	 * Constructor
	 * 
	 * @param packageName
	 *            PackageName of ClassModel
	 */
	public ClassModel(String packageName) {
		this();
		with(packageName);
	}

	public ClassModel withoutFeature(Feature feature) {
		this.generator.withoutFeature(feature);
		return this;
	}
	
	public ModelGenerator getGenerator() {
		return generator;
	}

	public FeatureProperty getFeature(Feature feature, Clazz... clazzes) {
		return this.generator.getFeature(feature, clazzes);
	}
}
