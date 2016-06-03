package de.uniks.networkparser.interfaces;

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
/**
 * The Class BaseEntity.
 * @author Stefan Lindel
 */
public interface BaseItem {
	public static final String CRLF = "\r\n";

	/**
	 * Convert Element to String
	 * @return the Item as String
	 */
	@Override
	public String toString();

	/**
	 * Convert Element to String
	 * @param converter	Converter for Format
	 * @return the Item as String with converter
	 */
	public String toString(Converter converter);

	/** Add Elements to List or KeyValue
	 * if param Modulo 2 the Params can be Key,Value
	 * or add all Values  to List
	 * @param values Items to Add to List
	 * @return this Component
	 */
	BaseItem with(Object... values);

	public BaseItem getNewList(boolean keyValue);
}
