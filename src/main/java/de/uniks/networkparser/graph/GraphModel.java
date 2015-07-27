package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

public class GraphModel extends GraphNode {
	private String defaultAuthorName;

	/**
	 * get All GraphClazz
	 * 
	 * @return all GraphClazz of a GraphModel
	 * 
	 *         <pre>
	 *              one                       many
	 * GraphModel ----------------------------------- GraphClazz
	 *              parent                   clazz
	 *         </pre>
	 */
	public SimpleSet<GraphClazz> getAttributes() {
		SimpleSet<GraphClazz> collection = new SimpleSet<GraphClazz>();
		if (children == null) {
			return collection;
		}
		for (GraphMember child : children) {
			if ((child instanceof GraphClazz) == false) {
				continue;
			}
			collection.add((GraphClazz) child);
		}
		return collection;
	}

	public GraphClazz createClazz(String name) {
		GraphClazz clazz = new GraphClazz().withId(name);
		clazz.with(this);
		return clazz;
	}

	public GraphModel with(GraphClazz... values) {
		super.with(values);
		return this;
	}

	public GraphModel without(GraphClazz... values) {
		super.without(values);
		return this;
	}

	@Override
	public GraphModel withId(String id) {
		super.withId(id);
		return this;
	}

	public String getAuthorName() {
		return defaultAuthorName;
	}

	/**
	 * Set the Default Author
	 * @param value The Authorname
	 * @return Success for Setting the Autorname true if success
	 */
	public boolean setAuthorName(String value) {
		if(this.defaultAuthorName != value) {
			this.defaultAuthorName = value;
			return true;
		}
		return false;
	}
	
	/**
	 * Set the Default Author
	 * @param value The Authorname
	 * @return Success for Setting the Autorname true if success
	 */
	public void withAuthorName(String value) {
		setAuthorName(value);
	}
}
