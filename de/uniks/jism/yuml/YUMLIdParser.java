package de.uniks.jism.yuml;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.util.Collection;

import de.uniks.jism.Filter;
import de.uniks.jism.IdMap;
import de.uniks.jism.interfaces.JISMEntity;
import de.uniks.jism.interfaces.SendableEntityCreator;

/**
 * The Class YUMLIdParser.
 */
public class YUMLIdParser extends IdMap {
	/** The Constant URL. */
	public static final String URL = "http://yuml.me/diagram/class/";

	/** The Constant for CLASS Diagramms. */
	public static final int CLASS = 1;

	/** The Constant for OBJECT Diagramms. */
	public static final int OBJECT = 2;
	
	private YUmlIdMapFilter filter = new YUmlIdMapFilter().withShowCardinality(true).withTyp(CLASS);

	/**
	 * Instantiates a new yUML id parser.
	 */
	public YUMLIdParser() {
		super();
	}

	/**
	 * Instantiates a new yUML id parser.
	 *
	 * @param parent
	 *			the parent
	 */
	public YUMLIdParser(IdMap parent) {
		super(parent);
	}
	
	/**
	 * Parses the object.
	 *
	 * @param object
	 *			the object
	 * @return the string
	 */
	public String parseObject(Object object) {
		return parse(object, filter.clone(new YUmlIdMapFilter()).withTyp(OBJECT));
	}

	/**
	 * Parses the class.
	 *
	 * @param object
	 *			the object
	 * @param showCardinality
	 *			the show cardinality
	 * @return the string
	 */
	public String parseClass(Object object) {
		return parse(object, filter.clone(new YUmlIdMapFilter()).withTyp(CLASS));
	}

	public String parse(Object object, YUmlIdMapFilter filter) {
		YUMLList list = new YUMLList().withTyp(filter.getTyp());
		parse(object, filter, list, 0);
		return list.toString();
	}

	/**
	 * Parses the.
	 *
	 * @param object  the object to Serialisation
	 * @param typ     Is it a OBJECT OR A CLASS diagram
	 * @param filter  Filter for Serialisation
	 * @param showCardinality  the show cardinality
	 * @return the Object as String
	 */
	private YUMLEntity parse(Object object, YUmlIdMapFilter filter, YUMLList list, int deep) {
		if (object == null) {
			return null;
		}
		
		String mainKey = getId(object);
		YUMLEntity element = list.getById(mainKey);
		if(element!=null){
			return element;
		}
		
		SendableEntityCreator prototyp = getCreatorClass(object);
		String className = object.getClass().getName();;
		className = className.substring(className.lastIndexOf('.') + 1);
		
		element=new YUMLEntity();
		element.withId(mainKey);
		element.withClassName(className);
		
		if (prototyp != null ) {
			for (String property : prototyp.getProperties()) {
				Object value = prototyp.getValue(object, property);
				if (value == null) {
					continue;
				}
				if (value instanceof Collection<?>) {
					for (Object containee : ((Collection<?>) value)) {
						if(containee == null){
							continue;
						}
						if (!filter.isRegard(this, object, property,
								containee, true, deep+1)) {
							continue;
						}
						if (!filter.isConvertable(this, object, property,
								containee, true, deep+1)) {
							continue;
						}

						YUMLEntity subId = parse(containee, filter, list, deep+1);
						element.addValue(property, containee.getClass().getName(), subId.getId(), "0..n");
					}
				} else {
					if (!filter.isRegard(this, object, property, value,
							false, deep+1)) {
						continue;
					}
					if (!filter.isConvertable(this, object, property,
							value, false, deep+1)) {
						continue;
					}
					SendableEntityCreator valueCreater = getCreatorClass(value);
					if (valueCreater != null) {
						YUMLEntity subId = parse(value, filter, list, deep+1);
						element.addValue(property, value.getClass().getName(), subId.getId(), "0..1");
					} else {
						element.addValue(property, value.getClass().getName(), ""+value, "");
					}
				}
			}
		}
		return element;
	}
	
	
//	/**
//	 * @param key of the Object
//	 * @param typ Is it a OBJECT OR A CLASS diagram
//	 * @param showCardinality  the show cardinality
//	 * @return Object as String
//	 */
//	private String getUMLText(String key, YUmlIdMapFilter filter) {
//		String[] itemsId = key.split("-");
//
//		String first = getYUMLString(itemsId[0], filter.getTyp(), filter);
//		String second = getYUMLString(itemsId[1], filter.getTyp(), filter);
//		String result;
//		if (typ == OBJECT) {
//			result = first + "-" + second;
//		} else {
//			String firstCardNo = filter.getLinkCardinality(key);
//			String secondCardNo = filter.getLinkCardinality(itemsId[1] + "-" + itemsId[0]);
//			result = first;
//			if ( filter.isShowCardinality() ) {
//				String firstCardName = filter.getLinkProperty(key);
//				String secondCardName = filter.getLinkProperty(itemsId[1] + "-" + itemsId[0]);
//				result += firstCardName + ": " + firstCardNo + "-";
//				if (secondCardName != null) {
//					result += secondCardName + ": " + secondCardNo;
//				}
//			} else {
//				result += firstCardNo + "-";
//				if (secondCardNo != null) {
//					result += secondCardNo;
//				}
//			}
//			result += second;
//		}
//		return result;
//	}

//	/**
//	 * Gets the cardinality.
//	 *
//	 * @param cardinaltity
//	 *			the cardinaltity
//	 * @param typ
//	 *			Is it a OBJECT OR A CLASS diagram
//	 * @return the cardinality
//	 */
//	private String getCardinality(String cardinaltity, int typ) {
//		if (typ == OBJECT) {
//			return "";
//		}
//		return cardinaltity;
//	}

	/**
	 * Gets the class name.
	 *
	 * @param object
	 *			the object
	 * @return the class name
	 */
	public String getClassName(Object object) {
		if (object instanceof String) {
			object = getObject((String) object);
		}
		String className = object.getClass().getName();
		return className.substring(className.lastIndexOf('.') + 1);
	}

	@Override
	public JISMEntity encode(Object value) {
//		value.
//		return parseClass(value);
		return new YUMLEntity();
	}

	@Override
	public Object decode(JISMEntity value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JISMEntity encode(Object value, Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}
}
