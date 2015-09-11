package de.uniks.networkparser.emf;

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
import de.uniks.networkparser.graph.GraphAttribute;
import de.uniks.networkparser.graph.GraphCardinality;
import de.uniks.networkparser.graph.GraphClazz;
import de.uniks.networkparser.graph.GraphDataType;
import de.uniks.networkparser.graph.GraphEdge;
import de.uniks.networkparser.graph.GraphEnumeration;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphLiteral;
import de.uniks.networkparser.graph.GraphNode;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLEntity;

public class EMFUtil {
	public static final String ECLASS = "ecore:EClass";
	public static final String ETYPE = "eType";
	public static final String EAttribute = "ecore:EAttribute";
	public static final String EReferences = "ecore:EReference";
	public static final String eSuperTypes = "eSuperTypes";
	public static final String EEnum = "ecore:EEnum";
	public static final String EOpposite = "eOpposite";
	public static final String UPPERBOUND = "upperBound";
	public static final String PARENT = "parent";

	public static final String emfTypes = " EOBJECT EBIG_DECIMAL EBOOLEAN EBYTE EBYTE_ARRAY ECHAR EDATE EDOUBLE EFLOAT EINT EINTEGER ELONG EMAP ERESOURCE ESHORT ESTRING ";

	public static boolean isEMFType(String tag) {
		return emfTypes.indexOf(" " + tag.toUpperCase() + " ") >= 0;
	}

	public static boolean isPrimitiveType(String type) {
		String primitiveTypes = " String long Long int Integer char Char boolean Boolean byte Byte float Float double Double Object java.util.Date ";

		if (type == null)
			return false;

		return primitiveTypes.indexOf(" " + type + " ") >= 0;
	}

	public static final String javaKeyWords = " abstract assert boolean break byte case catch char class const continue default do double else enum extends final finally float for if goto implements import instanceof int interface long native new package private protected public return short static strictfp super switch synchronized this throw throws transient try void volatile while ";

	public static String toValidJavaId(String tag) {
		if (javaKeyWords.indexOf(" " + tag + " ") >= 0) {
			tag = "_" + tag;
		}

		return tag;
	}

	public static String getId(String name) {
		if (name.indexOf("/") >= 0) {
			return name.substring(name.lastIndexOf("/") + 1);
		}
		if (name.indexOf("#") >= 0) {
			return name.substring(name.indexOf("#") + 2);
		}
		return name;
	}

	public static String shortClassName(String name) {
		int pos = name.lastIndexOf('.');
		name = name.substring(pos + 1);
		pos = name.lastIndexOf('$');
		if (pos >= 0) {
			name = name.substring(pos + 1);
		}
		return name;
	}
	
	public static String upFirstChar(String name) {
		if(name == null || name.length()<1) {
			return name;
		}
		return name.substring(0, 1).toUpperCase()+name.substring(1);
	}

	public static GraphList decode(String content) {
		GraphList model = new GraphList();
		
		XMLEntity ecore = new XMLEntity().withValue(content);
		SimpleList<XMLEntity> refs = new SimpleList<XMLEntity>();
		SimpleList<XMLEntity> superClazzes = new SimpleList<XMLEntity>();

		// add classes
		for (XMLEntity eClassifier : ecore.getChildren()) {
			if (!eClassifier.containsKey(EMFIdMap.XSI_TYPE)) {
				continue;
			}
			if (eClassifier.getString(EMFIdMap.XSI_TYPE).equalsIgnoreCase(ECLASS)) {
				GraphClazz clazz = new GraphClazz().withId(eClassifier.getString(EMFIdMap.NAME));
				model.with(clazz);
				for(XMLEntity child : eClassifier.getChildren()) {
					String typ = child.getString(EMFIdMap.XSI_TYPE);
					if(typ.equals(EAttribute)) {
						String etyp = EMFUtil.getId(child.getString(ETYPE));
						if (EMFUtil.isEMFType(etyp)) {
							etyp = etyp.substring(1);
						}
						if (EMFUtil.isPrimitiveType(etyp.toLowerCase())) {
							etyp = etyp.toLowerCase();
						}
						clazz.with(new GraphAttribute(EMFUtil.toValidJavaId(child.getString(EMFIdMap.NAME)), GraphDataType.ref(etyp)));						
					}else if(typ.equals(EReferences)) {
						child.put(PARENT, eClassifier);
						refs.add(child);
					}
				}
				if(eClassifier.containsKey(eSuperTypes)) {
					superClazzes.add(eClassifier);
				}
			} else if (eClassifier.getString(EMFIdMap.XSI_TYPE).equals(EEnum)) {
				GraphEnumeration graphEnum = new GraphEnumeration();
				graphEnum.withId(eClassifier.getString(EMFIdMap.NAME));
				for(XMLEntity child : eClassifier.getChildren()) {
					GraphLiteral literal = new GraphLiteral().withId(child.getString(EMFIdMap.NAME));
					for(String key : child.keySet()) {
						if(key.equals(EMFIdMap.NAME)) {
							continue;
						}
						literal.withKeyValue(key, child.get(key));
						graphEnum.with(literal);
					}
				}
			}
		}
		 // inheritance
		for(XMLEntity eClass : superClazzes) {
			String id = EMFUtil.getId(eClass.getString(eSuperTypes));
			 GraphNode kidClazz = model.getNode(eClass.getString(EMFIdMap.NAME));
			 GraphNode superClazz = model.getNode(id);
			 kidClazz.withParent(superClazz);
		}
		// assocs
		SimpleKeyValueList<String, GraphEdge> items = new SimpleKeyValueList<String, GraphEdge>(); 
		for(XMLEntity eref : refs) {
			String tgtClassName = eref.getString(ETYPE);
			if(tgtClassName.indexOf("#")>=0) {
				tgtClassName = tgtClassName.substring(tgtClassName.indexOf("#") + 3);
			}
			String tgtRoleName = EMFUtil.toValidJavaId(eref.getString(EMFIdMap.NAME));
			GraphNode tgtClazz = model.getNode(tgtClassName);
			GraphEdge tgtAssoc = items.getValue(tgtClassName+":"+tgtRoleName);
			if(tgtAssoc == null) {
				tgtAssoc = new GraphEdge().with(tgtClazz, GraphCardinality.ONE, tgtRoleName);
			}

			if (eref.containsKey(UPPERBOUND)) {
				Object upperValue = eref.get(UPPERBOUND);
				if (upperValue instanceof Number) {
					if (((Number) upperValue).intValue() != 1) {
						tgtAssoc.with(GraphCardinality.MANY);
					}
				}
			}
			
			String srcRoleName = null;
			GraphNode srcClazz;
			XMLEntity parent =(XMLEntity) eref.get(PARENT);
			String srcClassName = parent.getString(EMFIdMap.NAME);
			srcClazz = model.getNode(srcClassName);
			GraphEdge srcAssoc = new GraphEdge().with(srcClazz);
			tgtAssoc.with(srcAssoc);
			if (!eref.containsKey(EOpposite)) {
//				srcRoleName = tgtRoleName+"_back";
			}else{
				srcRoleName = EMFUtil.getId(eref.getString(EOpposite));
			}
			if(srcRoleName != null && items.getValue(srcClassName+":"+srcRoleName) == null) {
				srcAssoc.with(srcRoleName);
//				srcAssoc = new GraphEdge(srcClazz, GraphCardinality.ONE, srcRoleName);
				items.add(srcClassName+":"+srcRoleName, srcAssoc);
				tgtAssoc.with(srcAssoc);
			}
			model.with(tgtAssoc);
		}
		return model;
	}

}
