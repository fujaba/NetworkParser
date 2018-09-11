package de.uniks.networkparser.ext.generic;

/*
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
import java.util.List;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class EMFParser {
	protected Object value;

	public EMFParser(Object value) {
		this.value = value;
	}

	public static final void addAttributes(EMFParser eclass, Clazz sdmClass) {
		List<Object> callList = getEAttributes(eclass);
		for(Object item : callList) {
			if (item != null) {
				String name = getName(item);
				EMFParser eClassifier = new EMFParser(ReflectionLoader.call(item, "getEType"));
				sdmClass.withAttribute(name, DataType.create(EntityUtil.shortClassName(getInstanceClassName(eClassifier))));
			}
		}
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

		SimpleKeyValueList<EMFParser, Clazz> classMap = new SimpleKeyValueList<EMFParser, Clazz>();
		List<EMFParser> eClasses = getEClasses(epackage);
		for (EMFParser eclass : eClasses) {
			// add an interface and a class to the SDMModel
			String fullClassName = getName(eclass);
			Clazz sdmClass = model.createClazz(fullClassName).enableInterface();

			if (withImpl) {
				sdmClass.enableInterface();

				String implClassName = GraphUtil.getPackage(fullClassName) + ".impl." + getName(eclass) + "Impl";
				model.createClazz(implClassName).withSuperClazz(sdmClass);
			}

			classMap.put(eclass, sdmClass);

			// add attributes
			addAttributes(eclass, sdmClass);
		}

		SimpleSet<Object> doneERefs = new SimpleSet<Object>();
		for (EMFParser eclass : eClasses) {
			if (getESuperTypes(eclass).isEmpty() == false) {
				EMFParser eSuperClass = getESuperTypes(eclass).get(0);
				Clazz sdmSuperClass = classMap.get(eSuperClass);
				Clazz sdmClass = classMap.get(eclass);
				sdmClass.withSuperClazz(sdmSuperClass);
			}

			List<Object> eReferences = getEReferences(eclass);
			for (Object eref : eReferences) {
				if (doneERefs.contains(eref) == false) {
					Object oppositeERef = getEOpposite(eref);
					if (oppositeERef != null) {
						// create assoc
						EMFParser srcEClass = getEType(oppositeERef);
						EMFParser tgtEClass = getEType(eref);

						Clazz srcSDMClass = classMap.get(srcEClass);
						Clazz tgtSDMClass = classMap.get(tgtEClass);

						int srcCard = (getUpperBound(oppositeERef) == 1 ? Association.ONE : Association.MANY);
						int tgtCard = (getUpperBound(eref) == 1 ? Association.ONE : Association.MANY);

						srcSDMClass.withBidirectional(tgtSDMClass, getName(eref), tgtCard, getName(oppositeERef), srcCard);

						doneERefs.add(eref);
						doneERefs.add(oppositeERef);
					} else {
						// uni directional assoc
						EMFParser srcEClass = eclass;
						EMFParser tgtEClass = getEType(eref);

						Clazz srcSDMClass = classMap.get(srcEClass);
						Clazz tgtSDMClass = classMap.get(tgtEClass);

						int tgtCard = (getUpperBound(eref) == 1 ? Association.ONE : Association.MANY);

						srcSDMClass.withUniDirectional(tgtSDMClass, getName(eref), tgtCard);

						doneERefs.add(eref);
					}
				}
			}
		}
		return model;
	}

	//REFACTORING
	public static final List<Object> getEAttributes(Object eref) {
		if(eref instanceof EMFParser) {
			return getEAttributes(((EMFParser)eref).getValue());
		}
		if(isEMF(eref) == false) {
			return null;
		}
		List<Object> callList = ReflectionLoader.callList(eref, "getEAttributes");
		return callList;
	}


	public static final List<Object> getEReferences(Object eref) {
		if(eref instanceof EMFParser) {
			return getEReferences(((EMFParser)eref).getValue());
		}
		if(isEMF(eref) == false) {
			return null;
		}
		List<Object> callList = ReflectionLoader.callList(eref, "getEReferences");
		return callList;
	}

	public static final SimpleList<EMFParser> getESuperTypes(Object eref) {
		if(eref instanceof EMFParser) {
			return getESuperTypes(((EMFParser)eref).getValue());
		}
		SimpleList<EMFParser> list=new SimpleList<EMFParser>();
		if(isEMF(eref) == false) {
			return list;
		}
		List<Object> callList = ReflectionLoader.callList(eref, "getESuperTypes");
		for(Object item : callList) {
			if(item != null) {
				list.add(new EMFParser(item));
			}
		}
		return list;
	}

	public static final List<EMFParser> getEClasses(Object eref) {
		if(eref instanceof EMFParser) {
			return getEClasses(((EMFParser)eref).getValue());
		}
		SimpleList<EMFParser> items = new SimpleList<EMFParser>();
		if(isEMF(eref) == false) {
			return items;
		}
		List<Object> callList = ReflectionLoader.callList(eref, "getEClassifiers");
		for(Object item : callList) {
			if(item != null && ReflectionLoader.ECLASS.isAssignableFrom(item.getClass())) {
				items.add(new EMFParser(item));
			}
		}
		return items;
	}

	public static final String getInstanceClassName(Object eref) {
		if(eref instanceof EMFParser) {
			return getInstanceClassName(((EMFParser)eref).getValue());
		}
		if(isEMF(eref) == false) {
			return "";
		}
		return ""+ReflectionLoader.call(eref, "getInstanceClassName");
	}

	public static final EMFParser getEType(Object eref) {
		if(eref instanceof EMFParser) {
			return getEType(((EMFParser)eref).getValue());
		}
		if(isEMF(eref) == false) {
			return null;
		}
		return new EMFParser(ReflectionLoader.call(eref, "getEType"));
	}

	public static final Integer getUpperBound(Object eref) {
		if(eref instanceof EMFParser) {
			return getUpperBound(((EMFParser)eref).getValue());
		}
		if(isEMF(eref) == false) {
			return -1;
		}
		Object call = ReflectionLoader.call(eref, "getUpperBound");
		if(call instanceof Integer) {
			return (Integer)call;
		}
		return -1;

	}

	public static final String getName(Object eref) {
		if(eref instanceof EMFParser) {
			return getName(((EMFParser)eref).getValue());
		}
		if(isEMF(eref) == false) {
			return "";
		}
		return "" + ReflectionLoader.call(eref, "getName");
	}
	
	public static final boolean isEMF(Object eref) {
		if(ReflectionLoader.EOBJECT == null || eref == null) {
			return false;
		}
		if(ReflectionLoader.EOBJECT.isAssignableFrom(eref.getClass()) == false) {
			return false;
		}
		return true;
	}
	public static final Object getEOpposite(Object eref) {
		if(eref instanceof EMFParser) {
			return getEOpposite(((EMFParser)eref).getValue());
		}
		if(isEMF(eref) == false) {
			return "";
		}
		return ReflectionLoader.call(eref, "getEOpposite");
	}

	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			return true;
		}
		if(obj instanceof EMFParser == false) {
			return false;
		}
		EMFParser other = (EMFParser) obj;
		return this.value.equals(other.getValue());
	}

	public Object getValue() {
		return this.value;
	}
}
