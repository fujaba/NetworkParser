package de.uniks.networkparser.logic;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
/**
 * Logicclass for simple Check of Value.
 *
 * @author Stefan Lindel
 */
public class ValuesSimple {
	/** The Variable of value. */
	private Object item;

	/**
	 * @param value
	 *            The new Value
	 * @return ValuesSimple Instance
	 */
	public static ValuesSimple with(Object value) {
		ValuesSimple simple = new ValuesSimple();
		simple.withValue(value);
		return simple;
	}

	/**
	 * @return THe Value of Check
	 */
	public Object getValue() {
		return item;
	}

	/**
	 * @param value Set the new Value.
	 */
	public void withValue(Object value) {
		this.item = value;
	}
}