package de.uniks.networkparser.xml;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzType;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.Literal;
import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class EMFTokener extends Tokener{
	public static final String ECORE = "ecore";
	public static final String EPACKAGE = "ecore:EPackage";
	public static final String EAttribute = "eAttributes";
	public static final String ECLASS = "eClassifiers";
	public static final String EREFERENCE = "eReferences";
	public static final String ETYPE = "eType";
	public static final String EDATATYPE ="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//";
	public static final String TYPE_ECLASS = "ecore:EClass";
	public static final String TYPE_EAttribute = "ecore:EAttribute";
	public static final String TYPE_EReferences = "ecore:EReference";
	public static final String TYPE_ESUPERTYPE = "eSuperTypes";
	public static final String TYPE_EEnum = "ecore:EEnum";
	public static final String EOpposite = "eOpposite";
	public static final String ATTRIBUTE_URL = "http://www.eclipse.org/emf/2002/Ecore#//";
	public static final String UPPERBOUND = "upperBound";
	public static final String XSI_TYPE = "xsi:type";
	public static final String XMI_ID = "xmi:id";
	public static final String NAME = "name";
	HashMap<String, Integer> runningNumbers = null;

	/**
	 * Skip the Current Entity to &gt;.
	 */
	protected void skipEntity() {
		skipTo('>', false);
		// Skip >
		nextClean(false);
	}

	public String skipHeader() {
		boolean skip=false;
		CharacterBuffer tag;
		do {
			tag = this.getString(2);
			if(tag == null) {
				break;
			}
			if(tag.equals("<?")) {
				skipEntity();
				skip = true;
			} else if(tag.equals("<!")) {
				skipEntity();
				skip = true;
			} else {
				skip = false;
			}
		}while(skip);
		if(tag != null) {
			String item = tag.toString();
			this.buffer.withLookAHead(item);
			return item;
		}
		return "";
	}

	public XMLEntity encode(Object entity, MapEntity map) {
		if(entity instanceof GraphList) {
			return  encodeClassModel((GraphList)entity, map);
		}
		XMLEntity result = new XMLEntity();

		String typetag = entity.getClass().getName().replaceAll("\\.", ":");
		result.setType(typetag);

		encodeChildren(entity, result, map);

		return result;
	}

	public XMLEntity encodeClassModel(GraphList entity, MapEntity map) {
		XMLContainer container = new XMLContainer();
		container.withStandardPrefix();

		XMLEntity root = container.createChild();
		root.setType(EPACKAGE);
		root.withKeyValue("xmi:version", "2.0");
		root.withKeyValue("xmlns:xmi", "http://www.omg.org/XMI");
		root.withKeyValue("xmlns:ecore", "http://www.eclipse.org/emf/2002/Ecore");
		String id, name = "model";
		if(entity.getName()!= null) {
			id = EntityUtil.shortClassName(entity.getName());
			name = entity.getName();
		} else {
			id = name;
		}
		root.withKeyValue(NAME, id);
		root.withKeyValue("nsURI", "http:///"+name.replace(".", "/")+".ecore");
		root.withKeyValue("nsPrefix", name);

		for(Clazz child : entity.getClazzes()) {
			XMLEntity ecoreClass = root.createChild();
			ecoreClass.setType(ECLASS);
			ecoreClass.withKeyValue(XSI_TYPE, TYPE_ECLASS);
			ecoreClass.withKeyValue(NAME, child.getName());
			for(Attribute attribute : child.getAttributes()) {
				DataType type = attribute.getType();
				if(EntityUtil.isPrimitiveType(type.getName(false))) {
					XMLEntity ecoreAttribute = ecoreClass.createChild();
					ecoreAttribute.setType(EAttribute);
					ecoreAttribute.withKeyValue(NAME, attribute.getName());
					ecoreAttribute.withKeyValue(ETYPE, EDATATYPE + "E" + EntityUtil.upFirstChar(type.getName(true)));
				}
			}

			for(Association assoc : child.getAssociations()) {
				XMLEntity ecoreAssociation = ecoreClass.createChild();
				ecoreAssociation.setType(EREFERENCE);
				ecoreAssociation.withKeyValue(NAME, assoc.getOther().getName());
				ecoreAssociation.withKeyValue(ETYPE, "#//"+assoc.getOtherClazz().getName());
				ecoreAssociation.withKeyValue(EOpposite, "#//"+assoc.getOtherClazz().getName()+"/"+assoc.getName());
				if(Cardinality.MANY.equals(assoc.getCardinality())) {
					ecoreAssociation.withKeyValue(UPPERBOUND, "-1");
				} else {
					ecoreAssociation.withKeyValue(UPPERBOUND, "1");
				}
			}
		}
		return container;
	}

	private void encodeChildren(Object entity, XMLEntity parent, MapEntity map) {
		SendableEntityCreator creatorClass = getCreatorClass(entity);
		if(creatorClass == null) {
			return;
		}

		for (String propertyName : creatorClass.getProperties()) {
			Object propertyValue = creatorClass.getValue(entity, propertyName);

			if (EntityUtil.isPrimitiveType(EntityUtil.shortClassName(propertyValue.getClass().getName()))) {
				parent.put(propertyName, propertyValue);
			} else if (propertyValue instanceof Collection<?>) {
				for (Object childValue : (Collection<?>) propertyValue) {
					XMLEntity child = new XMLEntity();

					parent.withChild(child);

					child.setType(propertyName);

					String typetag = childValue.getClass().getName().replaceAll("\\.", ":");

					child.put(XSI_TYPE, typetag);

					encodeChildren(childValue, child, map);
				}
			} else {
				XMLEntity child = new XMLEntity();

				parent.withChild(child);

				child.setType(propertyName);

				String typetag = propertyValue.getClass().getName().replaceAll("\\.", ":");

				child.put(XSI_TYPE, typetag);

				encodeChildren(propertyValue, child, map);
			}
		}
	}

	/**
	 * Decode a Element from EMF
	 *
	 * @param map decoding runtime values
	 * @param root The Root Element of Returnvalue
	 * @return decoded Object
	 */
	public Object decode(MapEntity map, Object root) {
		skipHeader();
		XMLEntity xmlEntity = new XMLEntity();
		xmlEntity.withValue(this.buffer);
		if(EPACKAGE.equals(xmlEntity.getTag())) {
			return decoding(xmlEntity);
		}
		// build root entity
		String tag = xmlEntity.getTag();
		String[] splitTag = tag.split("\\:");
		if(splitTag.length<2) {
			return null;
		}
		if(ECORE.equalsIgnoreCase(splitTag[0]) || root instanceof GraphModel) {
			GraphModel model;
			if(root == null || root instanceof GraphModel == false) {
				model = new GraphList();
			} else {
				model = (GraphModel) root;
			}
			return decodingClassModel(xmlEntity, model);
		}
		Object rootObject = null;
		SendableEntityCreator rootFactory;
		if(root == null) {
			String className = splitTag[1];
			rootFactory = getCreator(className, false);
			if (rootFactory != null) {
				rootObject = rootFactory.getSendableInstance(false);
			} else {
				// just use an ArrayList
				rootObject = new ArrayList<Object>();
			}
		}else {
			rootObject = root;
			rootFactory = getCreatorClass(root);
		}
		runningNumbers = new HashMap<String, Integer>();

		addXMIIds(xmlEntity, null);

		addChildren(xmlEntity, rootFactory, rootObject);

		addValues(rootFactory, xmlEntity, rootObject);

		return rootObject;
	}

	private Object decodingClassModel(XMLEntity values, GraphModel model) {
		SimpleKeyValueList<String, Clazz> items = new SimpleKeyValueList<String, Clazz>();
		for(int c=0;c<values.sizeChildren();c++) {
			EntityList item = values.getChild(c);
			if(item instanceof XMLEntity == false) {
				continue;
			}
			XMLEntity child = (XMLEntity) item;
			String[] splitTag = child.getTag().split("\\:");
			String className = splitTag[1];
			Clazz clazz = items.get(className);
			if(clazz == null) {
				// Create New One
				clazz = new Clazz(className);
				items.add(className, clazz);
				model.with(clazz);
			}
			for(int i = 0;i < child.size();i++) {
				String key = child.get(i);
				String value = (String) child.getValueByIndex(i);
				if(value == null) {
					value = "";
				}
				if(value.startsWith("/")) {
					// Association
					AssociationSet associations = clazz.getAssociations();
					Association found = null;
					for(Association assoc : associations) {
						if(key.equals(assoc.getName())) {
							found = assoc;
							break;
						}
					}
					if(found == null ) {
						found = new Association(clazz);
						found.with(key);
						SimpleList<String> refs = getRef(key, child, null);
						for (String ref : refs)
                  {
                     Association back = new Association(items.get(ref));
                     found.with(back);
                  }
					}

					if(value.indexOf("/", 1) > 0) {
						// To Many
						found.with(Cardinality.MANY);
					}
				}
			}
		}
		//TODO CREATING METHOD BODY
		return model;
	}

	private void addXMIIds(XMLEntity xmlEntity, String rootId) {
		if (xmlEntity.has(XMI_ID)) {
			return;
		}
		String tag = xmlEntity.getTag();
		
		if (rootId != null) {
			rootId += tag;
			Integer num = runningNumbers.get(rootId);
			if (num == null) {
				num = 0;
			} else {
				num++;
			}
			runningNumbers.put(rootId, num);
			rootId += num;
		} else {
			rootId = "$";
		}
		// kid.put(XMI_ID, "$" + tag + num);

		if (xmlEntity.has("href")) {
			// might point to another xml file already loaded
			String refString = xmlEntity.getString("href");
			String[] split = refString.split("#//");

			if (split.length == 2) {
				String objectId = split[1];
				objectId = objectId.replace('@', '$');
				objectId = objectId.replace(".", "");
				// Object object = this.getObject(objectId);
				xmlEntity.put(XMI_ID, objectId);

				return;
			}
		}
		if(xmlEntity.has(IdMap.ID)) {
			rootId = xmlEntity.getString(IdMap.ID);
		}
		xmlEntity.put(XMI_ID, rootId);

		for(int i=0;i<xmlEntity.sizeChildren();i++) {
			EntityList kid = xmlEntity.getChild(i);
			if(kid instanceof XMLEntity == false) {
				continue;
			}
			addXMIIds((XMLEntity)kid, rootId);
		}
	}

	private void addValues(SendableEntityCreator rootFactory, XMLEntity xmlEntity, Object rootObject) {
		if (rootFactory == null) {
			return;
		}
		// set plain attributes
		for (int i = 0; i < xmlEntity.size(); i++) {
			String key = xmlEntity.getKeyByIndex(i);
			String value = xmlEntity.getString(key);
			if (value == null) {
				continue;
			}
			value = value.trim();
			if ("".equals(value) || XMI_ID.equals(key)) {
				continue;
			}
			SimpleList<String> myRefs = getRef(value, xmlEntity, rootFactory);
			if (myRefs.size() == 0 && rootFactory != null) {
			   rootFactory.setValue(rootObject, key, value, "");
			}
			for (String myRef : myRefs) {
			   Object object = getObject(myRef);
			   if (object != null) {
			      rootFactory.setValue(rootObject, key, object, "");
			   }
			}
		}

		// recursive on kids
		for(int i=0;i<xmlEntity.sizeChildren();i++) {
			EntityList kidEntity = xmlEntity.getChild(i);
			String kidId = "";
			if(kidEntity instanceof Entity) {
				kidId = (String) ((Entity) kidEntity).getValue(XMI_ID);
			}
			if (kidId.startsWith("$")) {
				kidId = "_" + kidId.substring(1);
			}

			Object kidObject = getObject(kidId);

			SendableEntityCreator kidFactory = getCreatorClass(kidObject);

			addValues(kidFactory, (XMLEntity)kidEntity, kidObject);
		}
	}

	private SimpleList<String> getRef(String value, XMLEntity xmlEntity, SendableEntityCreator rootFactory) {
		SimpleList<String> result = new SimpleList<String>();
	   if (value.startsWith("//@")) {
			for (String ref : value.split(" ")) {
				String myRef = "_" + ref.substring(3);
				if (myRef.indexOf('.') > 0) {
					myRef = myRef.replaceAll("\\.|/@", "");
				} else {
					myRef = "_" + myRef.subSequence(0, 1) + "0";
				}
				result.add(myRef);
			}
		} else if (value.startsWith("/")) {
			// maybe multiple separated by blanks
			String tagChar = xmlEntity.getTag().substring(0, 1);
			for (String ref : value.split(" ")) {
				ref = "_" + tagChar + ref.substring(1);
				if (getObject(ref) != null) {
					result.add(ref);
				}
			}
		} else if (value.indexOf('_') > 0) {
			// maybe multiple separated by blanks
			for (String ref : value.split(" ")) {
				if (getObject(ref) != null) {
				   result.add(ref);
				}
			}
		} else if (value.startsWith("$")) {
			for (String ref : value.split(" ")) {
				String myRef = "_" + ref.substring(1);
				if (rootFactory != null && getObject(myRef) != null) {
					result.add(myRef);
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void addChildren(XMLEntity xmlEntity, SendableEntityCreator rootFactory, Object rootObject) {
		String id = (String) xmlEntity.getValue(XMI_ID);
		int pos;

		if (id.startsWith("$")) {
			id = "_" + id.substring(1);
		}

		this.map.put(id, rootObject);
//		String[] properties = rootFactory.getProperties();

		for(int i=0;i<xmlEntity.sizeChildren();i++) {
			EntityList child = xmlEntity.getChild(i);
			if(child instanceof XMLEntity == false) {
				continue;
			}
			XMLEntity kidEntity = (XMLEntity) child;
			String tag = kidEntity.getTag();
			String typeName = null;

			Collection<Object> rootCollection = null;

			// it might be a cross reference to an already loaded object
			if (kidEntity.has("href")) {
				// might point to another xml file already loaded
				String refString = kidEntity.getString("href");
				String[] split = refString.split("#//");

				if (split.length == 2) {
					String objectId = split[1];
					objectId = objectId.replace('@', '_');
					objectId = objectId.replace(".", "");
					Object object = getObject(objectId);

					if (object != null) {
						// yes we know it
						if (rootObject instanceof Collection<?>) {
							rootCollection = (Collection<Object>) rootObject;
						}

						if (rootCollection != null) {
							rootCollection.add(object);
						} else {
							rootFactory.setValue(rootObject, tag, object, "");
						}
						return;
					}
				}
			}

			// identify kid type
			if (rootObject instanceof Collection) {
				rootCollection = (Collection<Object>) rootObject;
				// take the type name from the tag
				pos = tag.indexOf(":");
				if(pos > 0) {
					typeName = tag.substring(pos+1);
				}else{
					typeName = tag;
				}
//			} else {
//				Clazz clazz = GraphUtil.getByObject(model, rootObject.getClass().getName(), false);
//				Association edge = model.getEdge(clazz, tag);
//				if (edge != null) {
//					typeName = edge.getOther().getClazz().getName(false);
//				}
			}

			if (kidEntity.has(XSI_TYPE)) {
				typeName = kidEntity.getString(XSI_TYPE);
				typeName = typeName.replaceAll(":", ".");
			}
			if(typeName == null) {
				Object value = rootFactory.getValue(rootObject, tag);
				if(value != null) {
					if(value instanceof SimpleSet<?>) {
						SimpleSet<?> set = (SimpleSet<?>) value;
						typeName = set.getTypClass().getName();
					}else {
						typeName = value.getClass().getName();
					}
				} else {
					typeName = tag;
				}
			}
			
			if (typeName != null) {
				SendableEntityCreator kidFactory = getCreator(typeName, false);
				if (kidFactory == null && typeName.endsWith("s")) {
					kidFactory = getCreator(typeName.substring(0, typeName.length() - 1), false);
				}
				if(kidFactory == null) {
					continue;
				}
				Object kidObject = kidFactory.getSendableInstance(false);

				addChildren(kidEntity, kidFactory, kidObject);
				if (rootCollection != null) {
					rootCollection.add(kidObject);
				} else {
					rootFactory.setValue(rootObject, tag, kidObject, "");
				}

			}
		}
	}

	public GraphList decoding(String content) {
		return decoding(new XMLEntity().withValue(content));
	}
	public GraphList decoding(Tokener content) {
		return decoding(new XMLEntity().withValue(this));
	}

	private GraphList decoding(XMLEntity ecore) {
		GraphList model = new GraphList();
		SimpleList<Entity> superClazzes = new SimpleList<Entity>();

		// add classes
		SimpleKeyValueList<Entity, EntityList> parentList=new SimpleKeyValueList<Entity, EntityList>();
		for(int i=0;i<ecore.sizeChildren();i++) {
			EntityList eClassifier = ecore.getChild(i);
			if(eClassifier instanceof XMLEntity == false) {
				continue;
			}
			XMLEntity xml = (XMLEntity) eClassifier;
			if (xml.has(EMFTokener.XSI_TYPE)== false) {
				continue;
			}

			if (xml.getString(EMFTokener.XSI_TYPE).equalsIgnoreCase(TYPE_ECLASS)) {
				Clazz clazz = new Clazz(xml.getString(EMFTokener.NAME));
				model.with(clazz);
				for(int c=0;c<xml.sizeChildren();c++) {
					EntityList child = xml.getChild(c);
					if(child instanceof Entity == false) {
						continue;
					}
					Entity childItem = (Entity) child;
					String typ = childItem.getString(EMFTokener.XSI_TYPE);
					if(typ.equals(TYPE_EAttribute)) {
						String etyp = EntityUtil.getId(childItem.getString(ETYPE));
						if (EntityUtil.isEMFType(etyp)) {
							etyp = etyp.substring(1);
						}
						if (EntityUtil.isPrimitiveType(etyp.toLowerCase())) {
							etyp = etyp.toLowerCase();
						}
						clazz.with(new Attribute(EntityUtil.toValidJavaId(childItem.getString(EMFTokener.NAME)), DataType.create(etyp)));
					}else if(typ.equals(TYPE_EReferences)) {
						parentList.add(childItem, eClassifier);
					}
				}
				if(xml.has(TYPE_ESUPERTYPE)) {
					superClazzes.add(xml);
				}
			} else if (xml.getString(EMFTokener.XSI_TYPE).equals(TYPE_EEnum)) {
				Clazz graphEnum = new Clazz(xml.getString(EMFTokener.NAME));
				graphEnum.with(ClazzType.ENUMERATION);
				for(int c=0;c<xml.sizeChildren();c++) {
					EntityList child = ecore.getChild(i);
					if(child instanceof Entity == false) {
						continue;
					}
					Entity childItem = (Entity) child;
					Literal literal = new Literal(childItem.getString(EMFTokener.NAME));
					for(int z=0;z<childItem.size();z++) {
						String key = childItem.getKeyByIndex(z);
						if(key.equals(EMFTokener.NAME)) {
							continue;
						}
						literal.withValue(childItem.getValue(key));
						graphEnum.with(literal);
					}
				}
			}
		}
		 // inheritance
		for(Entity eClass : superClazzes) {
			String id = EntityUtil.getId(eClass.getString(TYPE_ESUPERTYPE));
			 Clazz kidClazz = model.getNode(eClass.getString(EMFTokener.NAME));
			 if(kidClazz != null) {
				 Clazz superClazz = model.getNode(id);
				 kidClazz.withSuperClazz(superClazz);
			 }
		}
		// assocs
		SimpleKeyValueList<String, Association> items = new SimpleKeyValueList<String, Association>();
		for(int i=0;i<parentList.size();i++) {
			Entity eref = parentList.get(i);
			String tgtClassName = eref.getString(ETYPE);
			if(tgtClassName.indexOf("#")>=0) {
				tgtClassName = tgtClassName.substring(tgtClassName.indexOf("#") + 3);
			}
			String tgtRoleName = eref.getString(EMFTokener.NAME);

			Association tgtAssoc = getOrCreate(items, model, tgtClassName, tgtRoleName);

			if (eref.has(UPPERBOUND)) {
				Object upperValue = eref.getValue(UPPERBOUND);
				if (upperValue instanceof Number) {
					if (((Number) upperValue).intValue() != 1) {
						tgtAssoc.with(Cardinality.MANY);
					}
				}
			}

			String srcRoleName = null;
			XMLEntity parent = (XMLEntity) parentList.getValueByIndex(i);
			String srcClassName = parent.getString(EMFTokener.NAME);
			if (!eref.has(EOpposite)) {
//				srcRoleName = tgtRoleName+"_back";
			}else{
				srcRoleName = EntityUtil.getId(eref.getString(EOpposite));
			}
			Association srcAssoc = getOrCreate(items, model, srcClassName, srcRoleName);
			// Create as Unidirection
			tgtAssoc.with(srcAssoc);
			srcAssoc.with(AssociationTypes.EDGE);
			
			tgtAssoc.getClazz().with(tgtAssoc);
			srcAssoc.getClazz().with(srcAssoc);
			
			model.with(tgtAssoc);
		}
		return model;
	}

	private Association getOrCreate(SimpleKeyValueList<String, Association> items, GraphList model, String className, String roleName) {
		roleName = EntityUtil.toValidJavaId(roleName);
		String assocName = className+":"+roleName;
		Association edge = (Association) items.getValue(assocName);
		if(edge == null) {
			Clazz clazz = model.getNode(className);
			if(clazz != null) {
				edge = new Association(clazz).with(Cardinality.ONE).with(roleName);
				clazz.with(edge);
				if(roleName != null) {
					items.add(assocName, edge);
				}
			}
		}
		return edge;
	}
}
