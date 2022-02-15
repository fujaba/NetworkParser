package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

/**
 * The Class ParameterSet.
 *
 * @author Stefan
 */
public class ParameterSet extends SimpleSet<Parameter> {
	
	/**
	 * Gets the methods.
	 *
	 * @return the methods
	 */
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for (Parameter item : this) {
			collection.add(item.getMethod());
		}
		return collection;
	}

	/**
	 * Gets the data types.
	 *
	 * @return the data types
	 */
	public SimpleSet<DataType> getDataTypes() {
		SimpleSet<DataType> collection = new SimpleSet<DataType>();
		for (Parameter item : this) {
			collection.add(item.getType());
		}
		return collection;
	}

	/**
	 * Checks for name.
	 *
	 * @param otherValue the other value
	 * @return the parameter set
	 */
	public ParameterSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Parameter.PROPERTY_NAME, otherValue));
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public SimpleSet<Parameter> getNewList(boolean keyValue) {
		return new ParameterSet();
	}
}
