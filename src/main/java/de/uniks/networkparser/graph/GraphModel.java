package de.uniks.networkparser.graph;
/*
NetworkParser
Copyright (c) 2011 - 2015, Stefan Lindel
All rights reserved.

Licensed under the EUPL, Version 1.1 or (as soon they
will be approved by the European Commission) subsequent
versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl5

Unless required by applicable law or agreed to in
writing, software distributed under the Licence is
distributed on an "AS IS" basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied.
See the Licence for the specific language governing
permissions and limitations under the Licence.
*/
import de.uniks.networkparser.list.SimpleSet;

public abstract class GraphModel extends GraphEntity {
	private String defaultAuthorName;

	/**
	 * get All GraphClazz
	 * 
	 * @return all GraphClazz of a GraphModel
	 * 
	 *		 <pre>
	 *			  one					   many
	 * GraphModel ----------------------------------- GraphClazz
	 *			  parent				   clazz
	 *		 </pre>
	 */
	public SimpleSet<Clazz> getClazzes() {
		SimpleSet<Clazz> collection = new SimpleSet<Clazz>();
		if (children == null) {
			return collection;
		}
		if(children instanceof Clazz) {
			collection.add((Clazz)children);
		}
		if(children instanceof GraphSimpleSet) {
			GraphSimpleSet items = (GraphSimpleSet)children;
			for (GraphMember child : items) {
				if (child instanceof Clazz)  {
					collection.add((Clazz) child);
				}
			}
		}
		return collection;
	}

	public Clazz createClazz(String name) {
		Clazz clazz = new Clazz().with(name);
		clazz.setClassModel(this);
		return clazz;
	}

	public GraphModel with(Clazz... values) {
		super.withChildren(true, values);
		return this;
	}

	public GraphModel without(Clazz... values) {
		super.without(values);
		return this;
	}

	public GraphModel with(Association... values) {
		super.with(values);
		return this;
	}
	
	@Override
	public GraphModel with(String name) {
		super.with(name);
		return this;
	}

	public String getAuthorName() {
		return defaultAuthorName;
	}

	/**
	 * Set the Default Author
	 * @param value The Authorname
	 * @return State for change the Autorname
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
	 * @return GraphModel Instance
	 */
	public GraphModel withAuthorName(String value) {
		setAuthorName(value);
		return this;
	}
	
	public GraphModel generate() {
		return this;
	}

	public GraphModel generate(String rootDir) {
		return this;
	}
	
	public boolean dumpHTML(String diagramName){
		return false;
	}
}
