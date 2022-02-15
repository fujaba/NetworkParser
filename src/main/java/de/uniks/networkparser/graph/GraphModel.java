package de.uniks.networkparser.graph;

import java.util.Iterator;

import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.xml.HTMLEntity;

/**
 * The Class GraphModel.
 *
 * @author Stefan
 */
public abstract class GraphModel extends GraphEntity implements BaseItem {
	
	/** The Constant DEFAULTPACKAGE. */
	public static final String DEFAULTPACKAGE = "i.love.networkparser";
	
	/** The Constant PROPERTY_CLAZZ. */
	public static final String PROPERTY_CLAZZ = "clazz";
	
	/** The Constant PROPERTY_GENERATEDCLAZZ. */
	public static final String PROPERTY_GENERATEDCLAZZ = "generatedclazz";
	private String defaultAuthorName;
	protected String genPath;
	private boolean renameAttributes = true;

	/**
	 * get All GraphClazz.
	 *
	 * @param filters Can Filter the List of Clazzes
	 * @return all GraphClazz of a GraphModel
	 * 
	 *         <pre>
	 * 			  one					   many
	 * GraphModel ----------------------------------- GraphClazz
	 * 			  parent				   clazz
	 *         </pre>
	 */
	public ClazzSet getClazzes(Condition<?>... filters) {
		ClazzSet collection = new ClazzSet();
		if (children == null) {
			return collection;
		}
		if (children instanceof Clazz) {
			collection.add((Clazz) children);
		}
		if (children instanceof GraphSimpleSet) {
			GraphSimpleSet items = (GraphSimpleSet) children;
			for (GraphMember child : items) {
				if (child instanceof Clazz) {
					if (check(child, filters)) {
						collection.add((Clazz) child);
					}
				}
			}
		}
		return collection;
	}

	/**
	 * Gets the value.
	 *
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(String attribute) {
		if (PROPERTY_CLAZZ.equalsIgnoreCase(attribute)) {
			return getClazzes();
		}
		if (PROPERTY_GENERATEDCLAZZ.equalsIgnoreCase(attribute)) {
			ClazzSet collection = new ClazzSet();
			if (children == null) {
				return collection;
			}
			if (children instanceof Clazz && GraphUtil.isExternal((Clazz) children) == false) {
				collection.add(children);
			}
			if (children instanceof GraphSimpleSet) {
				GraphSimpleSet items = (GraphSimpleSet) children;
				for (GraphMember child : items) {
					if (child instanceof Clazz && GraphUtil.isExternal((Clazz) child) == false) {
						collection.add((Clazz) child);
					}
				}
			}
			return collection;
		}
		if (PROPERTY_PACKAGENAME.equalsIgnoreCase(attribute)) {
			return this.getName(false);
		}
		return super.getValue(attribute);
	}

	protected boolean clearAddOnClazzes() {
		if (this.children == null) {
			return true;
		}
		if (this.children instanceof GraphSimpleSet == false) {
			if (this.children instanceof Clazz) {
				Clazz clazz = (Clazz) this.children;
				if (Clazz.TYPE_CREATOR.equals(clazz.getType()) || Clazz.TYPE_PATTERNOBJECT.equals(clazz.getType())
						|| Clazz.TYPE_SET.equals(clazz.getType())) {
					clazz.setParentNode(null);
					this.children = null;
				}
			}
			return true;
		}

		GraphSimpleSet list = (GraphSimpleSet) this.children;
		Iterator<GraphMember> i = list.iterator();
		while (i.hasNext()) {
			GraphMember member = i.next();
			if (member instanceof Clazz) {
				Clazz clazz = (Clazz) member;
				if (Clazz.TYPE_CREATOR.equals(clazz.getType()) || Clazz.TYPE_PATTERNOBJECT.equals(clazz.getType())
						|| Clazz.TYPE_SET.equals(clazz.getType())) {
					clazz.setParentNode(null);
					list.remove(member);
				}
			}
		}
		return true;
	}

	/**
	 * Creates the clazz.
	 *
	 * @param name the name
	 * @return the clazz
	 */
	public Clazz createClazz(String name) {
		if (name == null || children == null
				|| (children instanceof Clazz && name.equals(((Clazz) children).getName()))) {
			Clazz clazz = createInstance(name);
			clazz.setClassModel(this);
			return clazz;
		}
		/* So its List */
		if (children instanceof GraphSimpleSet) {
			GraphSimpleSet items = (GraphSimpleSet) children;
			for (GraphMember child : items) {
				if (child instanceof Clazz) {
					Clazz clazz = (Clazz) child;
					if (name.equals(clazz.getName())) {
						return clazz;
					}
				}
			}
		}
		Clazz clazz = createInstance(name);
		clazz.setClassModel(this);
		return clazz;
	}

	/**
	 * Creates the association.
	 *
	 * @param fromClazzName the from clazz name
	 * @param targetClazzName the target clazz name
	 * @return the association
	 */
	public Association createAssociation(String fromClazzName, String targetClazzName) {
	   Clazz fromClazz = createClazz(fromClazzName);
	   Clazz targetClazz = createClazz(targetClazzName);

	   AssociationSet associations = fromClazz.getAssociations();

	   for (Association assoc : associations)
      {
	      if(assoc.getClazz() == targetClazz) {
	         return assoc;
         }
      }
	   return fromClazz.createBidirectional(targetClazz, targetClazzName, 1, fromClazzName, 1);
	}

	protected Clazz createInstance(String name) {
		return new Clazz(name);
	}

	/**
	 * With.
	 *
	 * @param name the name
	 * @return the graph model
	 */
	@Override
	public GraphModel with(String name) {
		super.with(name);
		return this;
	}

	/**
	 * Gets the author name.
	 *
	 * @return the author name
	 */
	public String getAuthorName() {
		return defaultAuthorName;
	}

	/**
	 * Set the Default Author.
	 *
	 * @param value The Authorname
	 * @return State for change the Autorname
	 */
	public boolean setAuthorName(String value) {
		if ((value != null && value.equals(this.defaultAuthorName) == false)
				|| (value == null && this.defaultAuthorName != null)) {
			this.defaultAuthorName = value;
			return true;
		}
		return false;
	}

	/**
	 * Generate.
	 *
	 * @param rootDir the root dir
	 * @return the graph model
	 */
	public GraphModel generate(String... rootDir) {
		return this;
	}

	/**
	 * Dump HTML.
	 *
	 * @param diagramName the diagram name
	 * @param write the write
	 * @return the HTML entity
	 */
	public HTMLEntity dumpHTML(String diagramName, boolean... write) {
		HTMLEntity html = new HTMLEntity();

		if(write != null && write.length>1 && write[1]) {
			// two boolean is shortName
			GraphCustomItem item = new GraphCustomItem().with(ClassModel.PROPERTY_PACKAGENAME);
			GraphUtil.setGenerate(item, true);
			GraphUtil.setChildren(this, item);
		}
		GraphCustomItem item = new GraphCustomItem().with(ClassModel.PROPERTY_EXTERNAL);
    GraphUtil.setGenerate(item, true);
    GraphUtil.setChildren(this, item);
		html.withGraph(this);
		return html;
	}

	/**
	 * Fix class model.
	 *
	 * @return true, if successful
	 */
	public boolean fixClassModel() {
		Clazz[] classes = getClazzes().toArray(new Clazz[getClazzes().size()]);
		SimpleSet<Clazz> visited = new SimpleSet<Clazz>();
		String packageName = null;
		for (Clazz item : classes) {
			String className = item.getName();
			/* Check for First is UpperCase */
			if (className != null && className.length() > 0) {
				if (className.indexOf('.') > 0) {
					if (packageName == null) {
						packageName = className.substring(0, className.lastIndexOf("."));
					} else if (className.startsWith(packageName) == false) {
						packageName = "";
					}
				}
				if (renameAttributes) {
					int no = className.charAt(0);
					if (no < 'A' || no > 'Z') {
						item.setName(StringUtil.upFirstChar(className));
					}
					/* Attributes */
					AttributeSet attributes = item.getAttributes();
					for (Attribute attribute : attributes) {
						String name = attribute.getName();
						if (name == null || name.length() < 1) {
							item.remove(attribute);
							continue;
						}
						no = name.charAt(0);
						if (no < 'a' || no > 'z') {
							if (name.equals(name.toUpperCase())) {
								attribute.setName(name.toLowerCase());
							} else {
								attribute.setName(StringUtil.downFirstChar(name));
							}
						}
					}
				}
			}
			if (fixClassModel(item, visited) == false) {
				return false;
			}
		}
		/* CHECK PACKAGE */
		if (getDefaultPackage().equals(this.name) && packageName != null && packageName.length() > 0) {
			/* Its valid all Clazz has the same PackageName */
			this.name = packageName;
			packageName += ".";
			for (Clazz item : classes) {
				String className = item.getName();
				if (className != null && className.startsWith(packageName)) {
					item.setName(className.substring(packageName.length()));
				}
			}
		}

		return true;
	}

	/**
	 * Gets the default package.
	 *
	 * @return the default package
	 */
	public String getDefaultPackage() {
		return DEFAULTPACKAGE;
	}

	private boolean fixClassModel(Clazz item, SimpleSet<Clazz> visited) {
		/* Run over Interfaces, SuperClazzes, KidClazzes, Associations */
		AssociationSet assocs = item.getAssociations();
		for (Association role : assocs) {
			if (item.repairAssociation(role, this.renameAttributes) == false) {
				return false;
			}
			Clazz clazz = role.getOtherClazz();
			if (clazz.getClassModel() == null) {
				clazz.setClassModel(this);
				if (visited.add(clazz)) {
					fixClassModel(clazz, visited);
				}
			}
			this.with(role);
		}

		/* Fix the Clazz */
		if (Clazz.TYPE_ENUMERATION.equals(item.getType())) {
			SimpleSet<Literal> literals = item.getValues();
			SimpleSet<Attribute> attributes = item.getAttributes();
			for (Literal literal : literals) {
				int no = 0;
				SimpleList<Object> values = literal.getValues();
				if (values != null) {
					for (Object value : values) {
						if (value != null) {
							String type = StringUtil.shortClassName(value.getClass().getName());
							if (attributes.size() > no) {
								Attribute attribute = attributes.get(no);
								if (attribute.getType().getName(true).equals(type)) {
									/* Everthing is ok */
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

		/* FIX Attribute and Methods */
		for (Attribute attribute : item.getAttributes()) {
			fixDataType(attribute.getType());
		}

		for (Method method : item.getMethods()) {
			fixDataType(method.getReturnType());
			for (Parameter param : method.getParameters()) {
				fixDataType(param.getType());
			}
		}
		return true;
	}

	private void fixDataType(DataType dataType) {
		if (dataType == null) {
			return;
		}
		Clazz clazz = dataType.getClazz();
		if (clazz.isExternal() == false && StringUtil.isPrimitiveType(clazz.getName()) == false) {
			GraphMember byObject = this.getByObject(clazz.getName(), true);
			if (byObject == null) {
				this.add(clazz);
			} else if (byObject instanceof Clazz) {
				GraphUtil.setClazz(dataType, (Clazz) byObject);
			}
		}
	}

	protected GraphModel with(GraphModel model) {
		if (model == null) {
			return this;
		}
		GraphSimpleSet allChildren = model.getChildren();
		for (GraphMember child : allChildren) {
			withChildren(child);
		}
		/* Add Properties */
		if (this.defaultAuthorName == null) {
			this.defaultAuthorName = model.getAuthorName();
		}
		return this;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return toString(new YUMLConverter());
	}

	/**
	 * To string.
	 *
	 * @param converter the converter
	 * @return the string
	 */
	@Override
   public String toString(Converter converter) {
		if (converter == null) {
			return null;
		}
		return converter.encode(this);
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
   public int size() {
		if (this.children == null) {
			return 0;
		}
		if (this.children instanceof GraphSimpleSet) {
			return ((GraphSimpleSet) this.children).size();
		}
		return 1;
	}
}
