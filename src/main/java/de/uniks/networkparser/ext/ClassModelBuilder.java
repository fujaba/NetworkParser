package de.uniks.networkparser.ext;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Feature;

/**
 * The Class ClassModelBuilder.
 *
 * @author Stefan
 */
public class ClassModelBuilder {
	private ClassModel model;
	private Clazz lastClazz;
	
	/** The Constant NOGEN. */
	public static final String NOGEN = "NOGEN";
	
	/** The Constant ONE. */
	public static final int ONE = 1;
	
	/** The Constant MANY. */
	public static final int MANY = 42;

	/**
	 * Builds a classmodel builder for the given packageName.
	 *
	 * @param packageName the PackageName
	 */
	public ClassModelBuilder(String packageName) {
		model = new ClassModel(packageName);
	}

	/**
	 * Builds the class.
	 *
	 * @param className the class name
	 * @return the clazz
	 */
	public Clazz buildClass(String className) {
		lastClazz = model.createClazz(className);
		return lastClazz;
	}

	/**
	 * Creates the class.
	 *
	 * @param className the class name
	 * @return the class model builder
	 */
	public ClassModelBuilder createClass(String className) {
		lastClazz = model.createClazz(className);
		return this;
	}

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	public ClassModel getModel() {
		return model;
	}

	/**
	 * Builds the.
	 *
	 * @param params the params
	 * @return the class model
	 */
	public ClassModel build(String... params) {
		if (params == null) {
			model.generate();
		} else {
			/* Second Parameter is Author */
			if (params.length > 1) {
				model.setAuthorName(params[1]);
			}
			if (params.length > 0) {
				if (NOGEN.equals(params[0])) {
					return model;
				}
				model.generate(params[0]);
			} else {
				model.generate();
			}
		}
		return model;
	}

	/**
	 * Create a Attribute for a Clazz.
	 *
	 * @param name    name of Attribute
	 * @param type    name of Attribute
	 * @param clazzes in which Clazz yout want to Create Attribtue
	 * @return this classmodel builder
	 */
	public ClassModelBuilder createAttribute(String name, DataType type, Clazz... clazzes) {
		if (clazzes != null) {
			for (Clazz item : clazzes) {
				if (item == null) {
					continue;
				}
				item.createAttribute(name, type);
			}
		} else if (lastClazz != null) {
			lastClazz.createAttribute(name, type);
		}
		return this;
	}

	/**
	 * Create a Bidirectional Association.
	 *
	 * @param otherRoleName    The RoleName of Association
	 * @param otherCardinality The Cardinality one or many
	 * @param otherClazz       The otherClazz can be the ClassName as String or
	 *                         Clazz Object
	 * @param myRoleName       My RoleName
	 * @param myCardinality    My Cardinality
	 * @param clazz            The Clazz can be the ClassName as String or Clazz
	 *                         Object or lastClazz where used
	 * @return this classmodel builder
	 */
	public ClassModelBuilder createAssociation(String otherRoleName, int otherCardinality, Object otherClazz,
			String myRoleName, int myCardinality, Object... clazz) {
		/* Validate Paramter */
		if (otherClazz == null) {
			return this;
		}
		Clazz other = null;
		if (otherClazz instanceof Clazz) {
			other = (Clazz) otherClazz;
		} else if (otherClazz instanceof String) {
			other = model.createClazz((String) otherClazz);
		}
		Clazz my = null;
		if (clazz == null) {
			my = lastClazz;
		} else if (clazz.length > 0) {
			if (clazz[0] instanceof Clazz) {
				my = (Clazz) clazz[0];
			} else if (clazz[0] instanceof String) {
				my = model.createClazz((String) clazz[0]);
			}
		}
		if (my == null || other == null) {
			return this;
		}
		/* Now Create Assoc */
		my.createBidirectional(other, otherRoleName, otherCardinality, myRoleName, myCardinality);
		return this;
	}

	/**
	 * Sets the model set.
	 *
	 * @param type the type
	 * @return the class model builder
	 */
	public ClassModelBuilder setModelSet(Class<?> type) {
		if (model != null && type != null) {
			Feature feature = model.getFeature(Feature.SETCLASS);
			if (feature != null) {
				feature.withClazzValue(type);
			}
		}
		return this;
	}
}
