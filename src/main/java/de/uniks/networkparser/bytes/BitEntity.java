package de.uniks.networkparser.bytes;

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
import de.uniks.networkparser.interfaces.BufferedBytes;
import de.uniks.networkparser.interfaces.ByteConverter;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.list.SimpleList;
public class BitEntity extends SimpleList<BitValue> implements

		ByteItem {
	public static final byte BIT_STRING = 0x53; // S = String;
	public static final byte BIT_NUMBER = 0x4E; // N = Number
	public static final byte BIT_BYTE = 0x42; // B = Byte
	public static final byte BIT_REFERENCE = 0x52; // R = Reference

	// Can be a Typ
	protected String property;
	protected byte typ = BIT_BYTE;
	protected int orientation = 1;
	public static final String PROPERTY_PROPERTY = "property";
	public static final String PROPERTY_TYP = "typ";
	public static final String PROPERTY_ORIENTATION = "orientation";

	public BitEntity with(String property, byte typ) {
		this.property = property;
		this.typ = typ;
		return this;
	}

	public BitEntity withStartLen(int start, int len) {
		this.add(new BitValue(start, len));
		return this;
	}

	public BitEntity withOrientation(int orientation) {
		this.orientation = orientation;
		return this;
	}

	public String getPropertyName() {
		return property;
	}

	@Override
	public byte getTyp() {
		return typ;
	}

	public boolean isTyp(byte... referenceTyp) {
		if (referenceTyp == null) {
			return false;
		}
		for (byte typ : referenceTyp) {
			if (this.typ == typ) {
				return true;
			}
		}
		return false;
	}

	public boolean set(String attribute, Object value) {
		if (PROPERTY_PROPERTY.equalsIgnoreCase(attribute)) {
			this.property = "" + value;
			return true;
		} else if (PROPERTY_TYP.equalsIgnoreCase(attribute)) {
			this.typ = (Byte) value;
			return true;
		} else if (PROPERTY_ORIENTATION.equalsIgnoreCase(attribute)) {
			this.orientation = Integer.parseInt("" + value);
			return true;
		}
		return false;
	}

	/*
	 * Generic Getter for Attributes
	 */
	public Object get(String attrName) {
		if(attrName==null) {
			return null;
		}
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attrName = attrName.substring(0, pos);
		}
		if (PROPERTY_PROPERTY.equalsIgnoreCase(attrName)) {
			return this.property;
		} else if (PROPERTY_TYP.equalsIgnoreCase(attrName)) {
			return this.typ;
		} else if (PROPERTY_ORIENTATION.equalsIgnoreCase(attrName)) {
			return this.orientation;
		}
		return null;
	}

	@Override
	public BufferedBytes getBytes(boolean isDynamic) {
		return null;
	}

	@Override
	public void writeBytes(BufferedBytes buffer, boolean isDynamic,
			boolean last, boolean isPrimitive) {
	}

	@Override
	public int calcLength(boolean isDynamic, boolean isLast) {
		return 0;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public String toString(ByteConverter converter) {
		return toString();
	}

	@Override
	public String toString(ByteConverter converter, boolean isDynamic) {
		return toString();
	}

	@Override
	public BitEntity withAll(Object... values) {
		if(values==null){
			return this;
		}
		for (Object value : values) {
			if (value instanceof Byte) {
				this.typ = BIT_BYTE;
				this.property = "" + value;
			} else if (value instanceof Integer) {
				this.typ = BIT_NUMBER;
				this.property = "" + value;
			} else if (value instanceof BitEntity) {
				this.add((BitValue) value);
			} else {
				this.typ = BIT_STRING;
				this.property = "" + value;
			}
		}
		return this;
	}

	public int getOrientation() {
		return orientation;
	}

	@Override
	public BitEntity getNewList(boolean keyValue) {
		return new BitEntity();
	}

	@Override
	public boolean remove(Object value) {
		return removeByObject(value) >= 0;
	}
}
