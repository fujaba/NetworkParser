package de.uniks.networkparser.ext.generic.ecore;

import java.util.List;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class EPackageFactory implements SendableEntityCreator {
	public Object value;
	
	public EPackageFactory(Object value) {
		this.value = value;
	}
	
	@Override
	public String[] getProperties() {
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return ReflectionLoader.newInstance(ReflectionLoader.EPACKAGE);
	}

	public List<EClassifierFactory> getEClassifiers() {
		SimpleList<EClassifierFactory> items = new SimpleList<EClassifierFactory>();
		List<Object> callList = ReflectionLoader.callList("getEClassifiers", this.value);
		for(Object item : callList) {
			if(item != null) {
				items.add(new EClassifierFactory(item));
			}
		}
		return items;
	}
	public List<EClassFactory> getEClasses() {
		SimpleList<EClassFactory> items = new SimpleList<EClassFactory>();
		List<Object> callList = ReflectionLoader.callList("getEClassifiers", this.value);
		for(Object item : callList) {
			if(item != null && ReflectionLoader.ECLASS.isAssignableFrom(item.getClass())) {
				items.add(new EClassFactory(item));
			}
		}
		return items;
	}

	public static final ClassModel getClassModelFromEPackage(Object epackage, String packageName, boolean withImpl) {
		// get class model from epackage
		ClassModel model = new ClassModel(packageName);
		if (epackage == null) {
			return model;
		}
		if (ReflectionLoader.EPACKAGE.isAssignableFrom(epackage.getClass()) == false) {
			return model;
		}
		EPackageFactory factory = new EPackageFactory(epackage);

		SimpleKeyValueList<EClassFactory, Clazz> classMap = new SimpleKeyValueList<EClassFactory, Clazz>();
		List<EClassFactory> eClasses = factory.getEClasses();
		for (EClassFactory eclass : eClasses) {
			// add an interface and a class to the SDMModel
			String fullClassName = eclass.getName();
			Clazz sdmClass = model.createClazz(fullClassName).enableInterface();

			if (withImpl) {
				sdmClass.enableInterface();

				String implClassName = GraphUtil.getPackage(fullClassName) + ".impl." + eclass.getName() + "Impl";
				model.createClazz(implClassName).withSuperClazz(sdmClass);
			}

			classMap.put(eclass, sdmClass);

			// add attributes
			for (EAttributesFactory eattr : eclass.getEAttributes()) {
				sdmClass.withAttribute(eattr.getName(),
						DataType.create(EntityUtil.shortClassName(eattr.getEType().getInstanceClassName())));
			}
		}

		SimpleSet<Object> doneERefs = new SimpleSet<Object>();
		for (EClassFactory eclass : eClasses) {
			if (!eclass.getESuperTypes().isEmpty()) {
				EClassFactory eSuperClass = eclass.getESuperTypes().get(0);
				Clazz sdmSuperClass = classMap.get(eSuperClass);
				Clazz sdmClass = classMap.get(eclass);
				sdmClass.withSuperClazz(sdmSuperClass);
			}

			SimpleList<EReferencesFactory> eReferences = eclass.getEReferences();
			for (EReferencesFactory eref : eReferences) {
				if (!doneERefs.contains(eref)) {
					EReferencesFactory  oppositeERef = eref.getEOpposite();
					if (oppositeERef.getValue() != null) {
						// create assoc
						EClassFactory srcEClass = oppositeERef.getEType();
						EClassFactory tgtEClass = eref.getEType();

						Clazz srcSDMClass = classMap.get(srcEClass);
						Clazz tgtSDMClass = classMap.get(tgtEClass);

						Cardinality srcCard = (oppositeERef.getUpperBound() == 1 ? Cardinality.ONE : Cardinality.MANY);
						Cardinality tgtCard = (eref.getUpperBound() == 1 ? Cardinality.ONE : Cardinality.MANY);

						srcSDMClass.withBidirectional(tgtSDMClass, eref.getName(), tgtCard, oppositeERef.getName(),
								srcCard);

						doneERefs.add(eref);
						doneERefs.add(oppositeERef);
					} else {
						// uni directional assoc
						EClassFactory srcEClass = eclass;
						EClassFactory tgtEClass = eref.getEType();

						Clazz srcSDMClass = classMap.get(srcEClass);
						Clazz tgtSDMClass = classMap.get(tgtEClass);

						Cardinality tgtCard = (eref.getUpperBound() == 1 ? Cardinality.ONE : Cardinality.MANY);

						srcSDMClass.withUniDirectional(tgtSDMClass, eref.getName(), tgtCard);

						doneERefs.add(eref);
					}
				}
			}
		}
		return model;
	}
}
