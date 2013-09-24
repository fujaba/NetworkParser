package de.uniks.networkparser.yuml;

/*
 NetworkParser
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
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.BaseEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * The Class YUMLIdParser.
 */

public class YUMLIdMap extends IdMap {
	/** The Constant URL. */
	public static final String URL = "http://yuml.me/diagram/class/";

	/** The Constant for CLASS Diagramms. */
	public static final int CLASS = 1;

	/** The Constant for OBJECT Diagramms. */
	public static final int OBJECT = 2;

	private YUMLIdMapFilter filter = new YUMLIdMapFilter().withShowCardinality(
			true).withTyp(CLASS);

	/**
	 * Instantiates a new yUML id parser.
	 */
	public YUMLIdMap() {
		super();
	}

	/**
	 * Instantiates a new yUML id parser.
	 * 
	 * @param parent
	 *            the parent
	 */
	public YUMLIdMap(IdMap parent) {
		super(parent);
	}

	/**
	 * Parses the object.
	 * 
	 * @param object
	 *            the object
	 * @return the string
	 */
	public String parseObject(Object object) {
		return parse(object, filter.clone(new YUMLIdMapFilter())
				.withTyp(OBJECT));
	}

	/**
	 * Parses the class.
	 * 
	 * @param object
	 *            the object
	 * @param showCardinality
	 *            the show cardinality
	 * @return the string
	 */
	public String parseClass(Object object) {
		return parse(object, filter.clone(new YUMLIdMapFilter()).withTyp(CLASS));
	}

	public String parse(Object object, YUMLIdMapFilter filter) {
		YUMLList list = new YUMLList().withTyp(filter.getTyp());
		parse(object, filter, list, 0);
		return list.toString();
	}

	/**
	 * Parses the.
	 * 
	 * @param object
	 *            the object to Serialisation
	 * @param typ
	 *            Is it a OBJECT OR A CLASS diagram
	 * @param filter
	 *            Filter for Serialisation
	 * @param showCardinality
	 *            the show cardinality
	 * @return the Object as String
	 */
	private YUMLEntity parse(Object object, YUMLIdMapFilter filter,
			YUMLList list, int deep) {
		if (object == null) {
			return null;
		}

		String mainKey = getId(object);
		YUMLEntity element = list.getById(mainKey);
		if (element != null) {
			return element;
		}

		SendableEntityCreator prototyp = getCreatorClass(object);
		String className = object.getClass().getName();
		;
		className = className.substring(className.lastIndexOf('.') + 1);

		element = new YUMLEntity();
		element.withId(mainKey);
		element.withClassName(className);
		list.add(element);
		if (prototyp != null) {
			for (String property : prototyp.getProperties()) {
				Object value = prototyp.getValue(object, property);
				if (value == null) {
					continue;
				}
				if (value instanceof Collection<?>) {
					for (Object containee : ((Collection<?>) value)) {
						parsePropertyValue(object, filter, list, deep, element,
								property, containee, "0..n");
					}
				} else {
					parsePropertyValue(object, filter, list, deep, element,
							property, value, "0..1");
				}
			}
		}
		return element;
	}

	private void parsePropertyValue(Object entity, YUMLIdMapFilter filter,
			YUMLList list, int deep, YUMLEntity element, String property,
			Object item, String cardinality) {
		if (item == null) {
			return;
		}
		if (!filter.isPropertyRegard(this, entity, property, item, true,
				deep + 1)) {
			return;
		}
		if (!filter.isConvertable(this, entity, property, item, true, deep + 1)) {
			return;
		}
		SendableEntityCreator valueCreater = getCreatorClass(item);
		if (valueCreater != null) {
			YUMLEntity subId = parse(item, filter, list, deep + 1);
			list.addCardinality(new Cardinality().withSource(element)
					.withTarget(subId).withCardinality(cardinality));
		} else {
			element.addValue(property, item.getClass().getName(), "" + item);
		}
		return;
	}

	@Override
	public BaseEntity encode(Object value) {
		YUMLList list = new YUMLList();
		parse(value, this.filter.clone(new YUMLIdMapFilter()), list, 0);
		return list;
	}

	@Override
	public BaseEntity encode(Object value, Filter filter) {
		YUMLList list = new YUMLList();
		if (filter instanceof YUMLIdMapFilter) {
			YUMLIdMapFilter yumlFilter = (YUMLIdMapFilter) filter;
			list.withTyp(yumlFilter.getTyp());
			parse(value, yumlFilter, list, 0);
		}
		return list;
	}

	@Override
	public Object decode(BaseEntity value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the class name.
	 * 
	 * @param object
	 *            the object
	 * @return the class name
	 */
	public String getClassName(Object object) {
		if (object instanceof String) {
			object = getObject((String) object);
		}
		String className = object.getClass().getName();
		return className.substring(className.lastIndexOf('.') + 1);
	}
}
