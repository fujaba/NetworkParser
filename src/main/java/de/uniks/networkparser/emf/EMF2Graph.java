package de.uniks.networkparser.emf;

import de.uniks.networkparser.graph.GraphAttribute;
import de.uniks.networkparser.graph.GraphCardinality;
import de.uniks.networkparser.graph.GraphClazz;
import de.uniks.networkparser.graph.GraphDataType;
import de.uniks.networkparser.graph.GraphEdge;
import de.uniks.networkparser.graph.GraphEnum;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphLiteral;
import de.uniks.networkparser.graph.GraphNode;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLEntity;

public class EMF2Graph {
	public static final String ECLASS = "ecore:EClass";
	public static final String ETYPE = "eType";
	public static final String EAttribute = "ecore:EAttribute";
	public static final String EReferences = "ecore:EReference";
	public static final String eSuperTypes = "eSuperTypes";
	public static final String EEnum = "ecore:EEnum";
	public static final String EOpposite = "eOpposite";
	public static final String UPPERBOUND = "upperBound";
	public static final String PARENT = "parent";

	public static GraphList decode(String content) {
		return decode(content, "");
	}

	public static GraphList decode(String content, String packageName) {
		GraphList model = new GraphList();
		model.withId(packageName);
		
		XMLEntity ecore = new XMLEntity().withValue(content);
		SimpleList<XMLEntity> refs = new SimpleList<XMLEntity>();
		SimpleList<XMLEntity> superClazzes = new SimpleList<XMLEntity>();

		// add classes
		for (XMLEntity eClassifier : ecore.getChildren()) {
			if (!eClassifier.containsKey(EMFIdMap.XSI_TYPE)) {
				continue;
			}
			if (eClassifier.getString(EMFIdMap.XSI_TYPE).equalsIgnoreCase(ECLASS)) {
				GraphClazz clazz = new GraphClazz().withId(eClassifier.getString(EMFIdMap.NAME)).withParent(model);
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
				GraphEnum graphEnum = new GraphEnum();
				graphEnum.withId(eClassifier.getString(EMFIdMap.NAME));
				for(XMLEntity child : eClassifier.getChildren()) {
					GraphLiteral literal = new GraphLiteral().withId(child.getString(EMFIdMap.NAME));
					for(String key : child.keySet()) {
						if(key.equals(EMFIdMap.NAME)) {
							continue;
						}
						literal.withKeyValue(key, child.get(key));
						graphEnum.add(literal);
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
			GraphEdge srcAssoc;
			if(tgtAssoc == null) {
				tgtAssoc = new GraphEdge(tgtClazz, GraphCardinality.ONE, tgtRoleName);
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
			if (!eref.containsKey(EOpposite)) {
//				srcRoleName = tgtRoleName+"_back";
			}else{
				srcRoleName = EMFUtil.getId(eref.getString(EOpposite));
			}
			if(srcRoleName != null && items.getValue(srcClassName+":"+srcRoleName) == null) {
				srcAssoc = new GraphEdge(srcClazz, GraphCardinality.ONE, srcRoleName);
				items.add(srcClassName+":"+srcRoleName, srcAssoc);
				tgtAssoc.with(srcAssoc);
			}
			model.with(tgtAssoc);
		}
		return model;
	}
}
