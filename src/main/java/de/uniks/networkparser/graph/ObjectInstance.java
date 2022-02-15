package de.uniks.networkparser.graph;

/**
 * The Class ObjectInstance.
 *
 * @author Stefan
 */
public class ObjectInstance extends Clazz {

	/**
	 * Instantiates a new object instance.
	 *
	 * @param name the name
	 */
	public ObjectInstance(String name) {
		super(name);
	}

	/**
	 * With link.
	 *
	 * @param tgtInstance the tgt instance
	 * @return the object instance
	 */
	public ObjectInstance withLink(ObjectInstance tgtInstance) {
		super.withAssoc(tgtInstance, Association.ONE);
		return this;
	}
}
