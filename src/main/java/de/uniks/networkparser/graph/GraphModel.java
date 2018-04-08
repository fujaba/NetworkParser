package de.uniks.networkparser.graph;

import java.util.Iterator;

import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.graph.util.ClazzSet;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
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

public abstract class GraphModel extends GraphEntity implements BaseItem {
	public static final String DEFAULTPACKAGE2 = "i.love.networkparser";
	private String defaultAuthorName;

	/**
	 * get All GraphClazz
	 * @param filters Can Filter the List of Clazzes
	 * @return all GraphClazz of a GraphModel
	 *
	 *		 <pre>
	 *			  one					   many
	 * GraphModel ----------------------------------- GraphClazz
	 *			  parent				   clazz
	 *		 </pre>
	 */
	public ClazzSet getClazzes(Condition<?>... filters) {
	   ClazzSet collection = new ClazzSet();
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
					if(check(child, filters) ) {
						collection.add((Clazz) child);
					}
				}
			}
		}
		return collection;
	}

	protected boolean clearAddOnClazzes() {
		if(this.children == null) {
			return true;
		}
		if(this.children instanceof GraphSimpleSet == false) {
			if(this.children instanceof Clazz) {
				Clazz clazz = (Clazz) this.children;
				if(Clazz.TYPE_CREATOR.equals(clazz.getType())
						|| Clazz.TYPE_PATTERNOBJECT.equals(clazz.getType())
						|| Clazz.TYPE_SET.equals(clazz.getType())) {
					clazz.setParentNode(null);
					this.children = null;
				}
			}
			return true;
		}

		GraphSimpleSet list = (GraphSimpleSet) this.children;
		Iterator<GraphMember> i = list.iterator();
		while(i.hasNext()) {
			GraphMember member = i.next();
			if(member instanceof Clazz) {
				Clazz clazz = (Clazz) member;
				if(Clazz.TYPE_CREATOR.equals(clazz.getType())
						|| Clazz.TYPE_PATTERNOBJECT.equals(clazz.getType())
						|| Clazz.TYPE_SET.equals(clazz.getType())) {
					clazz.setParentNode(null);
					list.remove(member);
				}
			}
		}
		return true;
	}


	/**
	 * Constructor
	 * <p>Storyboard <a href='./src/test/java/org/sdmlib/test/examples/studyrightWithAssignments/StudyRightWithAssignmentsModel.java' type='text/x-java'>StudyRightWithAssignmentsClassGeneration</a></p>
	 * <p>1. generate class University</p>
	 * <pre>      	  ClassModel model = new ClassModel(&quot;org.sdmlib.test.examples.studyrightWithAssignments.model&quot;);
	 *
	 *       Clazz universityClass = model.createClazz(&quot;University&quot;)
	 *             .withAttribute(&quot;name&quot;, DataType.STRING);
	 * </pre>
	 * <img src="doc-files/StudyRightWithAssignmentsClassGenerationStep2.png"></img>
	 * <p>2. generate class Student</p>
	 * <pre>            Clazz studentClass = model.createClazz(&quot;Student&quot;)
	 *             .withAttribute(&quot;name&quot;, DataType.STRING)
	 *             .withAttribute(&quot;id&quot;, DataType.STRING)
	 *             .withAttribute(&quot;assignmentPoints&quot;, DataType.INT)
	 *             .withAttribute(&quot;motivation&quot;, DataType.INT)
	 *             .withAttribute(&quot;credits&quot;, DataType.INT);
	 * </pre>
	 * <img src="doc-files/StudyRightWithAssignmentsClassGenerationStep5.png"></img>
	 * <p>3. add University --> Student association</p>
	 * <pre>            universityClass.withBidirectional(studentClass, &quot;students&quot;, Cardinality.MANY, &quot;university&quot;, Cardinality.ONE);
	 * </pre>
	 * <img src="doc-files/StudyRightWithAssignmentsClassGenerationStep8.png"></img>
	 * <p>4. add University --> Room association</p>
	 * <pre>            Clazz roomClass = model.createClazz(&quot;Room&quot;)
	 *             .withAttribute(&quot;name&quot;, DataType.STRING)
	 *             .withAttribute(&quot;topic&quot;, DataType.STRING)
	 *             .withAttribute(&quot;credits&quot;, DataType.INT);
	 *
	 *       roomClass.withMethod(&quot;findPath&quot;, DataType.STRING, new Parameter(DataType.INT).with(&quot;motivation&quot;));
	 *
	 *       &#x2F;&#x2F;Association universityToRoom =
	 *       universityClass.createBidirectional(roomClass, &quot;rooms&quot;, Cardinality.MANY, &quot;university&quot;, Cardinality.ONE).with(AssociationTypes.AGGREGATION);
	 *
	 *       &#x2F;&#x2F; Association doors =
	 *       roomClass.withBidirectional(roomClass, &quot;doors&quot;, Cardinality.MANY, &quot;doors&quot;, Cardinality.MANY);
	 *
	 *       &#x2F;&#x2F; Association studentsInRoom =
	 *       studentClass.withBidirectional(roomClass, &quot;in&quot;, Cardinality.ONE, &quot;students&quot;, Cardinality.MANY);
	 *       studentClass.withBidirectional(studentClass, &quot;friends&quot;, Cardinality.MANY, &quot;friends&quot;, Cardinality.MANY);
	 *
	 * </pre>
	 * <img src="doc-files/StudyRightWithAssignmentsClassGenerationStep11.png"></img>
	 * <p>5. add assignments:</p>
	 * <pre>            Clazz assignmentClass = model.createClazz(&quot;Assignment&quot;)
	 *                .withAttribute(&quot;content&quot;, DataType.STRING)
	 *                .withAttribute(&quot;points&quot;, DataType.INT)
	 *                .withBidirectional(roomClass, &quot;room&quot;, Cardinality.ONE, &quot;assignments&quot;, Cardinality.MANY);
	 *
	 *       studentClass.withBidirectional(assignmentClass, &quot;done&quot;, Cardinality.MANY, &quot;students&quot;, Cardinality.MANY);
	 * </pre>
	 * <img src="doc-files/StudyRightWithAssignmentsClassGenerationStep14.png"></img>
	 * <p>6. generate class source files.</p>
	 * <pre>            model.generate(&quot;src&#x2F;test&#x2F;java&quot;); &#x2F;&#x2F; usually don&#x27;t specify anything here, then it goes into src
	 * </pre>
	 * @param name short class name
	 * @see org.sdmlib.test.examples.studyrightWithAssignments.StudyRightWithAssignmentsModel#testStudyRightWithAssignmentsClassGeneration
	 * @see org.sdmlib.test.examples.groupaccount.GroupAccountClassModel#testGroupAccountCodegen
	 * @return new Clazz
	 */
	public Clazz createClazz(String name) {
		if (name == null || children == null || (children instanceof Clazz && name.equals(((Clazz)children).getName()))) {
			Clazz clazz = new Clazz(name);
			clazz.setClassModel(this);
			return clazz;
		}
		// So its List
		if(children instanceof GraphSimpleSet) {
			GraphSimpleSet items = (GraphSimpleSet)children;
			for (GraphMember child : items) {
				if (child instanceof Clazz)  {
					Clazz clazz=(Clazz) child;
					if(name.equals(clazz.getName())) {
						return clazz;
					}
				}
			}
		}
		Clazz clazz = new Clazz(name);
		clazz.setClassModel(this);
		return clazz;
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
		if((value != null && value.equals(this.defaultAuthorName) == false)
				|| (value==null && this.defaultAuthorName != null)) {
			this.defaultAuthorName = value;
			return true;
		}
		return false;
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

	public boolean fixClassModel() {
		Clazz[] classes = getClazzes().toArray(new Clazz[getClazzes().size()]);
		SimpleSet<Clazz> visited = new SimpleSet<Clazz>();
		String packageName = null;
		for (Clazz item : classes) {
			String className = item.getName();
			if(className != null && className.indexOf('.')>0) {
				if(packageName == null) {
					packageName = className.substring(0, className.lastIndexOf("."));
				}else if(className.startsWith(packageName) == false) {
					packageName = "";
				}
			}
			fixClassModel(item, visited);
		}
		// CHECK PACKAGE
		if(getDefaultPackage().equals(this.name) && packageName != null && packageName.length()>0) {
			// Its valid all Clazz has the same PackageName
			this.name = packageName;
			packageName += ".";
			for (Clazz item : classes) {
				String className = item.getName();
				if(className != null && className.startsWith(packageName)) {
					item.setName(className.substring(packageName.length()));
				}
			}
		}

		return true;
	}

	public String getDefaultPackage() {
		return DEFAULTPACKAGE2;
	}

	private void fixClassModel(Clazz item, SimpleSet<Clazz> visited) {
		// Run over Interfaces, SuperClazzes, KidClazzes, Associations
		AssociationSet assocs = item.getAssociations();
		for (Association role : assocs) {
			item.repairAssociation(role);
			Clazz clazz = role.getOtherClazz();
			if (clazz.getClassModel() == null) {
				clazz.setClassModel(this);
				if (visited.add(clazz)) {
					fixClassModel(clazz, visited);
				}
			}
			this.with(role);
		}

		// Fix the Clazz
		if (Clazz.TYPE_ENUMERATION.equals(item.getType())) {
			SimpleSet<Literal> literals = item.getValues();
			SimpleSet<Attribute> attributes = item.getAttributes();
			for (Literal literal : literals) {
				int no = 0;
				SimpleList<Object> values = literal.getValues();
				if (values != null) {
					for (Object value : values) {
						if (value != null) {
							String type = value.getClass().getName();
							if (attributes.size() > no) {
								Attribute attribute = attributes.get(no);
								if (attribute.getType().getName(false).equals(type)) {
									// Everthing is ok
								} else {
									attribute.with(DataType.OBJECT);
								}
							} else {
								Attribute attribute = new Attribute("value" + no, DataType.create(type));
								attributes.add(attribute);
								item.with(attribute);
							}
						}
						no++;
					}
				}
			}
		}
	}
	protected GraphModel with(GraphModel model) {
		if(model == null) {
			return this;
		}
		GraphSimpleSet allChildren = model.getChildren();
		for(GraphMember child :allChildren) {
			withChildren(child);
		}
		// Add Properties
		if(this.defaultAuthorName == null) {
			this.defaultAuthorName = model.getAuthorName();
		}
		return this;
	}

	public String toString(Converter converter) {
		if (converter == null) {
			return null;
		}
		return converter.encode(this);
	}

	public int size() {
		if(this.children == null) {
			return 0;
		}
		if(this.children instanceof GraphSimpleSet) {
			return ((GraphSimpleSet)this.children).size();
		}
		return 1;
	}
}
