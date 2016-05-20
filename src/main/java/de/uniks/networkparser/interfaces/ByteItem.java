package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.converter.ByteConverter;

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

public interface ByteItem extends BaseItem {
	/**
	 * @param converter		ByteConverter for Format
	 * @param isDynamic		ByteStream for minimize output
	 * @return 				the ByteItem as String
	 */
	public String toString(ByteConverter converter, boolean isDynamic);

	/**
	 * @param isDynamic		ByteStream for minimize output
	 * @return ByteStream
	 */
	public ByteBuffer getBytes(boolean isDynamic);

	/**
	 * Write the Entity to the buffer
	 *
	 * @param buffer		for writing
	 * @param isDynamic		dynamic switsch
	 * @param lastEntity	is the entity is the last of a list
	 * @param isPrimitive	need the entity no datatyp
	 */
	public void writeBytes(ByteBuffer buffer, boolean isDynamic,
			boolean lastEntity, boolean isPrimitive);

	/**
	 * @param isDynamic	ByteStream for minimize output
	 * @param isLast	is the Element is the Last of Group
	 * @return the Size of Bytes
	 */
	public int calcLength(boolean isDynamic, boolean isLast);

	public byte getTyp();

	/** @return true if the ByteItem is Empty */
	public boolean isEmpty();

	/**
	 * Size of Item
	 * @return the Size of the Item
	 */
	public int size();
}
