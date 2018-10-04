package de.uniks.networkparser.ext;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;

public class ClassModelBuilder {
	private ClassModel model;
	private Clazz lastClazz;

	/**
	 * Builds a classmodel builder for the given packageName
	 * 
	 * @param packageName the PackageName
	 */
	public ClassModelBuilder(String packageName) {
		model = new ClassModel(packageName);
	}

	public Clazz buildClass(String className) {
		lastClazz = model.createClazz(className);
		return lastClazz;
	}

	public ClassModelBuilder createClass(String className) {
		lastClazz = model.createClazz(className);
		return this;
	}

	public ClassModel build(String... params) {
		if (params == null) {
			model.generate();
		} else {
			// Second Parameter is Author
			if (params.length > 1) {
				model.setAuthorName(params[1]);
			}
			if (params.length > 0) {
				model.generate(params[0]);
			} else {
				model.generate();
			}
		}
		return model;
	}

	/**
	 * Create a Attribute for a Clazz
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
	 * Create a Bidirectional Association
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
		// Validate Paramter
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
		// Now Create Assoc
		my.createBidirectional(other, otherRoleName, otherCardinality, myRoleName, myCardinality);
		return this;
	}
}
