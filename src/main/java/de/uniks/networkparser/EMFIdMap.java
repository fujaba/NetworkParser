package de.uniks.networkparser;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Clazz.ClazzType;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Literal;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLIdMap;
import de.uniks.networkparser.xml.XMLTokener;
import de.uniks.networkparser.xml.util.XMLEntityCreator;

public class EMFIdMap extends XMLIdMap {
	public static final String ECLASS = "ecore:EClass";
	public static final String ETYPE = "eType";
	public static final String EAttribute = "ecore:EAttribute";
	public static final String EReferences = "ecore:EReference";
	public static final String eSuperTypes = "eSuperTypes";
	public static final String EEnum = "ecore:EEnum";
	public static final String EOpposite = "eOpposite";
	public static final String UPPERBOUND = "upperBound";
	public static final String PARENT = "parent";


	public static final String XSI_TYPE = "xsi:type";
	public static final String XMI_ID = "xmi:id";
	public static final String NAME = "name";
	HashMap<String, Integer> runningNumbers = null;
	private GraphList model;

	@Override
	public XMLEntity encode(Object entity) {
		XMLEntity result = new XMLEntity();

		String typetag = entity.getClass().getName().replaceAll("\\.", ":");
		result.withTag(typetag);

		encodeChildren(entity, result);

		return result;
	}

	private void encodeChildren(Object entity, XMLEntity parent) {
		SendableEntityCreator creatorClass = this.getCreatorClass(entity);

		for (String propertyName : creatorClass.getProperties()) {
			Object propertyValue = creatorClass.getValue(entity, propertyName);

			if (EntityUtil.isPrimitiveType(EntityUtil.shortClassName(propertyValue.getClass().getName()))) {
				parent.put(propertyName, propertyValue);
			} else if (propertyValue instanceof Collection<?>) {
				for (Object childValue : (Collection<?>) propertyValue) {
					XMLEntity child = new XMLEntity();

					parent.withChild(child);

					child.withTag(propertyName);

					String typetag = childValue.getClass().getName().replaceAll("\\.", ":");

					child.put(XSI_TYPE, typetag);

					encodeChildren(childValue, child);
				}
			} else {
				XMLEntity child = new XMLEntity();

				parent.withChild(child);

				child.withTag(propertyName);

				String typetag = propertyValue.getClass().getName().replaceAll("\\.", ":");

				child.put(XSI_TYPE, typetag);

				encodeChildren(propertyValue, child);
			}
		}
	}

	public EMFIdMap withModel(GraphList model) {
		this.model = model;
		return this;
	}

	/**
	 * @param tokener XMLTokener
	 * @param defaultFactory DefaultTokener
	 * @return decoded Object
	 */
	public Object decode(XMLTokener tokener, SendableEntityCreatorTag defaultFactory) {
		if (defaultFactory == null) {
			defaultFactory = new XMLEntityCreator();
		}
		tokener.skipHeader();
		XMLEntity xmlEntity = new XMLEntity();
		xmlEntity.withValue(tokener);
		// build root entity
		String tag = xmlEntity.getTag();
		String[] splitTag = tag.split("\\:");
		String className = splitTag[1];
		SendableEntityCreator rootFactory = getCreator(className, false);

		Object rootObject = null;

		if (rootFactory != null) {
			rootObject = rootFactory.getSendableInstance(false);
		} else {
			// just use an ArrayList
			rootObject = new ArrayList<Object>();
		}

		runningNumbers = new HashMap<String, Integer>();

		addXMIIds(xmlEntity, null);

		addChildren(xmlEntity, rootFactory, rootObject);

		addValues(rootFactory, xmlEntity, rootObject);

		return rootObject;
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
		xmlEntity.put(XMI_ID, rootId);

		for (EntityList kid : xmlEntity.getChildren()) {
			if(kid  instanceof XMLEntity == false) {
				continue;
			}
			addXMIIds((XMLEntity)kid, rootId);
		}
	}

	private void addValues(SendableEntityCreator rootFactory, XMLEntity xmlEntity, Object rootObject) {
		if (rootFactory == null) {
			return;
		}
		// add to map
		String id = (String) xmlEntity.getValue(XMI_ID);

		if (id.startsWith("$")) {
			id = "_" + id.substring(1);
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

			if (value.startsWith("//@")) {
				for (String ref : value.split(" ")) {
					String myRef = "_" + ref.substring(3);
					if (myRef.indexOf('.') > 0) {
						myRef = myRef.replaceAll("\\.|/@", "");
					} else {
						myRef = "_" + myRef.subSequence(0, 1) + "0";
					}
					Object object = getObject(myRef);
					if (object != null) {
						rootFactory.setValue(rootObject, key, object, "");
					}
				}
			} else if (value.startsWith("/")) {
				// maybe multiple separated by blanks
				String tagChar = xmlEntity.getTag().substring(0, 1);
				for (String ref : value.split(" ")) {
					ref = "_" + tagChar + ref.substring(1);
					if (getObject(ref) != null) {
						rootFactory.setValue(rootObject, key, getObject(ref), "");
					}
				}
			} else if (value.indexOf('_') > 0) {
				// maybe multiple separated by blanks
				for (String ref : value.split(" ")) {
					if (getObject(ref) != null) {
						rootFactory.setValue(rootObject, key, getObject(ref), "");
					}
				}
			} else if (value.startsWith("$")) {
				for (String ref : value.split(" ")) {
					String myRef = "_" + ref.substring(1);
					if (getObject(myRef) != null && rootFactory != null) {
						rootFactory.setValue(rootObject, key, getObject(myRef), "");
					}
				}
			} else {
				if (rootFactory != null) {
					rootFactory.setValue(rootObject, key, value, "");
				}
			}
		}

		// recursive on kids
		for (Iterator<EntityList> iterator = xmlEntity.getChildren().iterator(); iterator.hasNext();) {
			EntityList kidEntity = iterator.next();
			String kidId = "";
			if(kidEntity instanceof Entity) {
				kidId = (String) ((Entity) kidEntity).getValue(XMI_ID);
			}
			if (kidId.startsWith("$")) {
				kidId = "_" + kidId.substring(1);
			}

			Object kidObject = this.getObject(kidId);

			SendableEntityCreator kidFactory = this.getCreatorClass(kidObject);

			addValues(kidFactory, (XMLEntity)kidEntity, kidObject);
		}
	}

	@SuppressWarnings("unchecked")
	private void addChildren(XMLEntity xmlEntity, SendableEntityCreator rootFactory, Object rootObject) {
		String id = (String) xmlEntity.getValue(XMI_ID);
		int pos;

		if (id.startsWith("$")) {
			id = "_" + id.substring(1);
		}

		this.put(id, rootObject);

		Iterator<EntityList> iterator = xmlEntity.getChildren().iterator();
		while (iterator.hasNext()) {
			EntityList child = iterator.next();
			if(child  instanceof XMLEntity == false) {
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
					Object object = this.getObject(objectId);

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
			} else {
				Clazz clazz = GraphUtil.getByObject(model, rootObject.getClass().getName(), false);
				Association edge = model.getEdge(clazz, tag);
				if (edge != null) {
					typeName = edge.getOther().getClazz().getName(false);
				}
			}

			if (kidEntity.has(XSI_TYPE)) {
				typeName = kidEntity.getString(XSI_TYPE);
				typeName = typeName.replaceAll(":", ".");
			}

			if (typeName != null) {
				SendableEntityCreator kidFactory = getCreator(typeName, false);
				if (kidFactory == null && typeName.endsWith("s")) {
					kidFactory = getCreator(typeName.substring(0, typeName.length() - 1), false);
				}
				Object kidObject = kidFactory.getSendableInstance(false);

				if (rootCollection != null) {
					rootCollection.add(kidObject);
				} else {
					rootFactory.setValue(rootObject, tag, kidObject, "");
				}

				addChildren(kidEntity, kidFactory, kidObject);
			}
		}
	}

	public static GraphList decoding(String content) {
		GraphList model = new GraphList();

		XMLEntity ecore = new XMLEntity().withValue(content);
		SimpleList<Entity> refs = new SimpleList<Entity>();
		SimpleList<Entity> superClazzes = new SimpleList<Entity>();

		// add classes
		for (EntityList eClassifier : ecore.getChildren()) {
			if(eClassifier instanceof XMLEntity == false) {
				continue;
			}
			XMLEntity xml = (XMLEntity) eClassifier;
			if (xml.has(EMFIdMap.XSI_TYPE)== false) { 
				continue;
			}
			
			if (xml.getString(EMFIdMap.XSI_TYPE).equalsIgnoreCase(ECLASS)) {
				Clazz clazz = new Clazz().with(xml.getString(EMFIdMap.NAME));
				model.with(clazz);
				for(EntityList child : xml.getChildren()) {
					if(child instanceof Entity == false) {
						continue;
					}
					Entity childItem = (Entity) child;
					String typ = childItem.getString(EMFIdMap.XSI_TYPE);
					if(typ.equals(EAttribute)) {
						String etyp = EntityUtil.getId(childItem.getString(ETYPE));
						if (EntityUtil.isEMFType(etyp)) {
							etyp = etyp.substring(1);
						}
						if (EntityUtil.isPrimitiveType(etyp.toLowerCase())) {
							etyp = etyp.toLowerCase();
						}
						clazz.with(new Attribute(EntityUtil.toValidJavaId(childItem.getString(EMFIdMap.NAME)), DataType.create(etyp)));
					}else if(typ.equals(EReferences)) {
						childItem.put(PARENT, eClassifier);
						refs.add(childItem);
					}
				}
				if(xml.has(eSuperTypes)) {
					superClazzes.add(xml);
				}
			} else if (xml.getString(EMFIdMap.XSI_TYPE).equals(EEnum)) {
				Clazz graphEnum = new Clazz().with(ClazzType.ENUMERATION);
				graphEnum.with(xml.getString(EMFIdMap.NAME));
				for(EntityList child : xml.getChildren()) {
					if(child instanceof Entity == false) {
						continue;
					}
					Entity childItem = (Entity) child;
					Literal literal = new Literal(childItem.getString(EMFIdMap.NAME));
					for(String key : childItem.keySet()) {
						if(key.equals(EMFIdMap.NAME)) {
							continue;
						}
						literal.withValue(childItem.getValue(key));
//						literal.withKeyValue(key, child.get(key));
						graphEnum.with(literal);
					}
				}
			}
		}
		 // inheritance
		for(Entity eClass : superClazzes) {
			String id = EntityUtil.getId(eClass.getString(eSuperTypes));
			 Clazz kidClazz = model.getNode(eClass.getString(EMFIdMap.NAME));
			 Clazz superClazz = model.getNode(id);
			 kidClazz.withoutSuperClazz(superClazz);
		}
		// assocs
		SimpleKeyValueList<String, Association> items = new SimpleKeyValueList<String, Association>();
		for(Entity eref : refs) {
			String tgtClassName = eref.getString(ETYPE);
			if(tgtClassName.indexOf("#")>=0) {
				tgtClassName = tgtClassName.substring(tgtClassName.indexOf("#") + 3);
			}
			String tgtRoleName = eref.getString(EMFIdMap.NAME);

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
			XMLEntity parent =(XMLEntity) eref.getValue(PARENT);
			String srcClassName = parent.getString(EMFIdMap.NAME);
			if (!eref.has(EOpposite)) {
//				srcRoleName = tgtRoleName+"_back";
			}else{
				srcRoleName = EntityUtil.getId(eref.getString(EOpposite));
			}
			Association srcAssoc = getOrCreate(items, model, srcClassName, srcRoleName);
			// Create as Unidirection
			tgtAssoc.with(srcAssoc);
			srcAssoc.with(AssociationTypes.EDGE);
			model.with(tgtAssoc);
		}
		return model;
	}

	private static Association getOrCreate(SimpleKeyValueList<String, Association> items, GraphList model, String className, String roleName) {
		roleName = EntityUtil.toValidJavaId(roleName);
		String assocName = className+":"+roleName;
		Association edge = (Association) items.getValue(assocName);
		if(edge == null) {
			Clazz clazz = model.getNode(className);
			edge = new Association(clazz).with(Cardinality.ONE).with(roleName);
			clazz.with(edge);
			if(roleName != null) {
				items.add(assocName, edge);
			}
		}
		return edge;
	}
}
