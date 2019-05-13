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

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.DataTypeSet;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

/**
 * @author Stefan The XSD Entity
 */

public class XSDEntity extends XMLEntity implements SendableEntityCreator {
	public static final String XSD_COMPLEX_TYPE = ":complexType";
	public static final String XSD_ELEMENT_TYPE = ":element";
	public static final String XSD_STRING_TYPE = ":string";
	public static final String XSD_SEQUENCE_TYPE = ":sequence";
	public static final String XSD_ATTRIBUTE_TYPE = ":attribute";
	public static final String XSD_UNBOUNDED = "unbounded";
	/** Constant of Choice. */
	public static final String PROPERTY_CHOICE = "choice";
	/** Constant of Sequence. */
	public static final String PROPERTY_SEQUENCE = "sequence";
	/** Constant of Attributes. */
	public static final String PROPERTY_ATTRIBUTE = "attribute";
	/** Constant of Minimum Elements. */
	public static final String PROPERTY_MINOCCURS = "minOccurs";
	/** Constant of Maximum Elements. */
	public static final String PROPERTY_MAXOCCURS = "maxOccurs";
	private static final String PROEPRTY_CHILDREN = "children";

	/** Elements of Choice. */
	private ArrayList<XSDEntity> choice;
	/** Elements of Sequence. */
	private ArrayList<XSDEntity> sequence;
	/** All Attributes. */
	private ArrayList<String> attribute;
	/** The Minimum of Elements. */
	private String minOccurs;
	/** The Maximum of Elements. */
	private String maxOccurs;

	/** @return The Choice of Elements. */
	public ArrayList<XSDEntity> getChoice() {
		return choice;
	}

	/**
	 * Set a ChoiceList
	 *
	 * @param value Elements of Choice.
	 */
	public void setChoice(ArrayList<XSDEntity> value) {
		this.choice = value;
	}

	/** @return The Sequence of Elements. */
	public ArrayList<XSDEntity> getSequence() {
		return sequence;
	}

	/**
	 * Set a Sequence Validator
	 *
	 * @param values Set the Sequence.
	 */
	public void setSequence(ArrayList<XSDEntity> values) {
		this.sequence = values;
	}

	/** @return All Attributes. */
	public ArrayList<String> getAttribute() {
		return attribute;
	}

	/**
	 * Set a List of Attributes
	 *
	 * @param values Set All Attributes.
	 */
	public void setAttribute(ArrayList<String> values) {
		this.attribute = values;
	}

	/** @return The Minimum of Elements. */
	public String getMinOccurs() {
		return minOccurs;
	}

	/**
	 * Set the Mimimum for XSD Entity
	 *
	 * @param value The Minimum of Elements.
	 * @return success for Set new Value
	 */
	public boolean setMinOccurs(String value) {
		if (value != this.minOccurs) {
			this.minOccurs = value;
			return true;
		}
		return false;
	}

	/** @return The Maximum of Elements. */
	public String getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * Set the Maximum of Occurs
	 *
	 * @param value the Maximum of Elements.
	 * @return success for Set new Value
	 */
	public boolean setMaxOccurs(String value) {
		if (value != maxOccurs) {
			this.maxOccurs = value;
			return true;
		}
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new XSDEntity();
	}

	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_CHOICE, PROPERTY_SEQUENCE, PROPERTY_ATTRIBUTE, PROPERTY_MINOCCURS,
				PROPERTY_MAXOCCURS, PROPERTY_TAG, PROPERTY_VALUE, PROEPRTY_CHILDREN };
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (entity == null || entity instanceof XSDEntity == false) {
			return false;
		}
		XSDEntity xsd = (XSDEntity) entity;
		if (PROPERTY_CHOICE.equalsIgnoreCase(attribute)) {
			return xsd.addToChoice((XSDEntity) value);
		}
		if (PROPERTY_SEQUENCE.equalsIgnoreCase(attribute)) {
			return xsd.addToSequence((XSDEntity) value);
		}
		if (PROPERTY_ATTRIBUTE.equalsIgnoreCase(attribute)) {
			return xsd.addToAdttribute((String) value);
		}
		if (PROPERTY_MINOCCURS.equalsIgnoreCase(attribute)) {
			return xsd.setMinOccurs((String) value);
		}
		if (PROPERTY_MAXOCCURS.equalsIgnoreCase(attribute)) {
			return xsd.setMaxOccurs((String) value);
		}
		if (PROPERTY_TAG.equalsIgnoreCase(attribute)) {
			return xsd.setTag((String) value);
		}
//				, PROPERTY_VALUE, PROEPRTY_CHILDREN
		return false;
	}
	
	@Override
	public Object put(String key, Object value) {
		//Override Standard for Custom Keys ;)
		if (PROPERTY_CHOICE.equalsIgnoreCase(key)) {
			addToChoice((XSDEntity) value);
//			return value;
		}
		if (PROPERTY_SEQUENCE.equalsIgnoreCase(key)) {
			addToSequence((XSDEntity) value);
//			return value;
		}
		if (PROPERTY_MINOCCURS.equalsIgnoreCase(key)) {
			setMinOccurs((String) value);
//			return value;
		}
		if (PROPERTY_MAXOCCURS.equalsIgnoreCase(key)) {
			setMaxOccurs((String) value);
//			return value;
		}
		return super.put(key, value);
	}

	public boolean setTag(String value) {
		if (value != this.tag) {
			this.tag = value;
			return true;
		}
		return false;
	}

	public boolean addToAdttribute(String value) {
		if (this.attribute == null) {
			this.attribute = new ArrayList<String>();
		}
		this.attribute.add(value);
		return true;
	}

	public boolean addToSequence(XSDEntity value) {
		if (this.sequence == null) {
			this.sequence = new ArrayList<XSDEntity>();
		}
		this.sequence.add(value);
		return true;
	}

	public boolean addToChoice(XSDEntity value) {
		if (this.choice == null) {
			this.choice = new ArrayList<XSDEntity>();
		}
		this.choice.add(value);
		return true;
	}

	public ClassModel createModel(String prefix) {
		ClassModel model = new ClassModel();
		if (prefix == null) {
			return model;
		}
		System.out.println(this.sizeChildren());
		this.cleanUp(prefix);
		System.out.println(this.sizeChildren());
		// Create Classes
		String elementType = prefix +XSDEntity.XSD_ELEMENT_TYPE;
		String complexType = prefix +XSDEntity.XSD_COMPLEX_TYPE;
		XMLEntity entity = this;
		SimpleKeyValueList<String, XMLEntity> typesValues = new SimpleKeyValueList<String, XMLEntity>();
		SimpleKeyValueList<Clazz, String> classTypes = new SimpleKeyValueList<Clazz, String>();
		
		String stringType = prefix + XSD_STRING_TYPE;
		Clazz rootElement = null;
		for(int i=0;i<entity.sizeChildren();i++) {
			XMLEntity child = (XMLEntity) entity.getChild(i);
			if(elementType.equalsIgnoreCase(child.getTag())) {
				if(stringType.equalsIgnoreCase(child.getString("type"))) {
					// Ignore
//					System.out.println("IGNORE:" +child.getTag());
				} else {
					// CHECK IF ONLY STRING
					Clazz childClass = new Clazz(child.getString("name"));
					// Add GraphSimpleSet without Comparator
					GraphUtil.setChildren(childClass, GraphSimpleSet.create(false));
//					Clazz childClass = model.createClazz(child.getString("name"));
					if(rootElement == null) {
						model.add(childClass);
						rootElement = childClass;
						this.callBack(childClass, true);
					}
					classTypes.put(childClass, child.getString("type"));
				}
			}else if(complexType.equalsIgnoreCase(child.getTag())) {
				typesValues.put(child.getString("name"), child);
			}
		}
		// Now Analyse Types
		model.with(prefix);
		parsingRootStructure(model, rootElement, classTypes, typesValues, null);
//		parsingFullStructure(prefix, model, classTypes, typesValues);
		return model;
	}
	
	private String changeName(String value) {
		if(value == null || value.length()<1) {
			return value;
		}
		int no = value.charAt(0);
		if(value.equals(value.toUpperCase())) {
			return value.toLowerCase();
		}
		if(no<'a' || no>'z') {
			return EntityUtil.downFirstChar(value);
		}
		return value;

	}
	protected String parsingRootStructure(ClassModel model, Clazz clazz, SimpleKeyValueList<Clazz, String> classTypes, SimpleKeyValueList<String, XMLEntity> typesValues, Clazz parent) {
		if(model == null || classTypes == null || typesValues == null) {
			return null;
		}
		String sequenzType = model.getName()+XSD_SEQUENCE_TYPE;
		String stringType = model.getName()+XSD_STRING_TYPE;
		String attributeType = model.getName()+XSD_ATTRIBUTE_TYPE;
		XMLEntity typeClassEntity = typesValues.get(classTypes.get(clazz));
		if(typeClassEntity == null) {
			return null;
		}
		//createContainerAssoc(delayname, assoc, childchildClass);
		for(int c=0;c<typeClassEntity.sizeChildren();c++) {
			XSDEntity child = (XSDEntity) typeClassEntity.getChild(c);
			if(sequenzType.equalsIgnoreCase(child.getTag())) {
				// Now Check for Container
				// typeClassEntity
				if(child.sizeChildren() == 1) {
					// It is a Containern Set
					XSDEntity first = (XSDEntity) child.getChild(0);

					String containerType = first.getString("type");
					if(XSD_UNBOUNDED.equalsIgnoreCase(first.getMaxOccurs()) == false) {
						System.out.println("IGNORE: "+first.getString("name") +" "+containerType);
					} else if(stringType.equalsIgnoreCase(containerType)  ) {
							String containerName = first.getString("name");
							Attribute containerAttribtute = parent.createAttribute(containerName, DataTypeSet.create(DataType.STRING));
							this.callBack(containerAttribtute, true, clazz.getName(), containerName);
							return containerName;
					}
				}
				SimpleList<String> orderKey=new SimpleList<String>();
				for(int s=0;s<child.sizeChildren();s++) {
					XMLEntity element = (XMLEntity) child.getChild(s);
					String name = element.getString("name");
					String type = element.getString("type");
					if(stringType.equalsIgnoreCase(type)) {
						orderKey.add(changeName(name));
//						orderKey.add(name);
						Attribute attr = clazz.createAttribute(name, DataType.STRING);
						callBack(attr, true);
					} else {
						// New Class Found
						Clazz subClazz = classTypes.getKey(type);
						if(subClazz != null) {
							// Now Analyse SubClass
							String result = this.parsingRootStructure(model, subClazz, classTypes, typesValues, clazz);
							if(result != null) {
								if(result.length()>0) {
									orderKey.add(result);
//									orderKey.add(result);
								}else {
									orderKey.add(changeName(name));
//									orderKey.add(name);
									model.add(subClazz);
									this.callBack(subClazz, false, name);
									Association assoc = clazz.createBidirectional(subClazz, name, Association.MANY, name+"Parent", Association.ONE);
									this.callBack(assoc, false);
								}
							}
						} else {
							System.err.println(type+" not parsing");
						}
					}
				}
				this.callBack(clazz, true, orderKey.toArray(new String[orderKey.size()]));
			}else if(attributeType.equalsIgnoreCase(child.getTag())) {
				if(stringType.equalsIgnoreCase(child.getString("type"))) {
					Attribute attr = clazz.createAttribute(child.getString("name"), DataType.STRING);
					this.callBack(attr, false);
				}else {
					System.err.println(child.getString("name")+" not parsing");
				}
			}
		}
		return "";
	}
//	protected ClassModel parsingFullStructure(String prefix, ClassModel model, SimpleKeyValueList<Clazz, String> classTypes, SimpleKeyValueList<String, XMLEntity> typesValues ) {
//		String sequenzType = prefix+XSD_SEQUENCE_TYPE;
//		String stringType = prefix+XSD_STRING_TYPE;
//		for(int i=0;i<classTypes.size();i++) {
//			Clazz clazz = classTypes.get(i);
//			String type = classTypes.getValueByIndex(i);
//			XMLEntity typeClassEntity = typesValues.get(type);
//			if(typeClassEntity == null) {
//				continue;
//			}
//			for(int c=0;c<typeClassEntity.sizeChildren();c++) {
//				XMLEntity child = (XMLEntity) typeClassEntity.getChild(c);
//				if(sequenzType.equalsIgnoreCase(child.getTag())) {
//					SimpleList<String> orderKey=new SimpleList<String>();
//					for(int s=0;s<child.sizeChildren();s++) {
//						XMLEntity element = (XMLEntity) child.getChild(s);
//						orderKey.add(element.getString("name"));
//						if(stringType.equalsIgnoreCase(element.getString("type"))) {
//							Attribute attr = clazz.createAttribute(element.getString("name"), DataType.STRING);
//							callBack(attr, true);
//						} else {
//							// New Class Found
//							Clazz key = classTypes.getKey(element.getString("type"));
//							if(key != null) {
//								String name = element.getString("name");
//								model.add(key);
//								this.callBack(key, false);
//								Association assoc = clazz.createBidirectional(key, name, Association.MANY, name+"Parent", Association.ONE);
//								this.callBack(assoc, false);
//							}else {
//								System.err.println(child.getString("type")+" not parsing");
//							}
//							
//						}
//					}
////					this.createClazzOrder(clazz, orderKey);
//				}else if((prefix+":attribute").equalsIgnoreCase(child.getTag())) {
//					if(stringType.equalsIgnoreCase(child.getString("type"))) {
//						Attribute attr = clazz.createAttribute(child.getString("name"), DataType.STRING);
//						this.callBack(attr, false);
//					}else {
//						System.err.println(child.getString("name")+" not parsing");
//					}
//				}
//			}
//		}
//		// Combinate Classes
//		if(classTypes.size()>0) {
//			Clazz clazz = classTypes.get(0);
//			this.mergeContainer(model, clazz, new SimpleList<Clazz>());
//		}
//		return model;
//	}
//
//	private boolean mergeContainer(ClassModel model, Clazz clazz, SimpleList<Clazz> mergingClazzes) {
//		if(clazz == null || mergingClazzes == null) {
//			return false;
//		}
//		mergingClazzes.add(clazz);
//		AssociationSet associations = clazz.getAssociations();
//		for(Association assoc : associations) {
//			Clazz childClass = assoc.getOtherClazz();
//			if(mergingClazzes.contains(childClass) == false) {
//				mergeContainer(model, childClass, mergingClazzes);
//			}
//			
//			Clazz childchildClass = null; 
//			AssociationSet childAssocs = childClass.getAssociations();
//			String delayname="";
//			if(childClass.getAttributes().size() == 0 && childAssocs.size() == 2) {
//				if(childAssocs.size() == 1) {
//					System.err.println("ERROR: "+childClass.getName()+"->"+clazz.getName());
//				}
//				delayname = "."+childClass.getName();
//				if( childAssocs.get(0).getOtherClazz() == clazz) {
//					childchildClass = childAssocs.get(1).getOtherClazz();
//				}else if(childAssocs.get(1).getOtherClazz() == clazz){
//					childchildClass = childAssocs.get(0).getOtherClazz();
//				}
//			}
//			if(childchildClass != null) {
//				assoc.with(childchildClass);
//				model.remove(childClass);
//				createContainerAssoc(delayname, assoc, childchildClass);
////				GraphUtil.withChildren(assoc, new ConstName().with(delayname+childchildClass.getName()));
//			}
//		}
//		return true;
//	}
//	
	public boolean cleanUp(String prefix) {
		if (prefix == null) {
			return false;
		}
		
		SimpleKeyValueList<String, String> simpleReplaceType = new SimpleKeyValueList<String, String>();
		String complex = prefix + XSD_COMPLEX_TYPE;
		String stringType = prefix + XSD_STRING_TYPE;
		for (int i = 0; i < this.sizeChildren(); i++) {
			XMLEntity child = (XMLEntity) this.getChild(i);
			if (complex.equalsIgnoreCase(child.getTag()) && child.sizeChildren() == 1) {
				String name = child.getString("name");
				XMLEntity content = (XMLEntity) child.getChild(0);
				if (content.sizeChildren() == 1) {
					XMLEntity value = (XMLEntity) content.getChild(0);
					if (stringType.equals(value.getString("base"))) {
						int pos = simpleReplaceType.indexOfValue(name);
						if(pos >=0) {
							simpleReplaceType.put(name, stringType);
							simpleReplaceType.setValue(pos, stringType);
						} else {
							simpleReplaceType.put(name, stringType);
						}
						this.withoutChild(child);
						i--;
					}
				}
			}
		}
		cleanUpTypes(prefix, simpleReplaceType, this);
		return true;
	}

	private void cleanUpTypes(String prefix, SimpleKeyValueList<String, String> simpleReplaceType, XMLEntity parent) {
		String elementType = prefix + XSD_ELEMENT_TYPE;
		for (int i = 0; i < parent.sizeChildren(); i++) {
			XMLEntity child = (XMLEntity) parent.getChild(i);
			if (elementType.equalsIgnoreCase(child.getTag())) {
				String childType = child.getString("type", "");
				int pos = simpleReplaceType.indexOf(childType);
				if (pos >= 0) {
					child.setValueItem("type", simpleReplaceType.getValueByIndex(pos));
				}
			}
			// Rekursive
			for (int c = 0; c < child.sizeChildren(); c++) {
				cleanUpTypes(prefix, simpleReplaceType, (XMLEntity) child.getChild(c));
			}
		}
	}
	
//CallBack
	protected boolean callBack(GraphMember member, boolean value, String... params) {
		return true;
	}
	protected void createContainerAssoc(String delayname, Association assoc, Clazz childchildClass) {
	}
}
