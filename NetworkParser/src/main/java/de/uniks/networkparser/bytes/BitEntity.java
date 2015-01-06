package de.uniks.networkparser.bytes;

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
import de.uniks.networkparser.AbstractEntityList;
import de.uniks.networkparser.AbstractList;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferedBytes;
import de.uniks.networkparser.interfaces.ByteConverter;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.FactoryEntity;

public class BitEntity extends AbstractEntityList<BitValue> implements
		ByteItem, FactoryEntity {
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
		this.items.add(new BitValue(start, len));
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
			this.typ = (byte) value;
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
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (PROPERTY_PROPERTY.equalsIgnoreCase(attribute)) {
			return this.property;
		} else if (PROPERTY_TYP.equalsIgnoreCase(attribute)) {
			return this.typ;
		} else if (PROPERTY_ORIENTATION.equalsIgnoreCase(attribute)) {
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
	public BitEntity with(Object... values) {
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
	public BaseItem getNewObject() {
		return new BitEntity();
	}

	@Override
	public BitEntity getNewArray() {
		return new BitEntity();
	}

	@Override
	public AbstractList<BitValue> getNewInstance() {
		return new BitEntity();
	}

	@Override
	public boolean remove(Object value) {
		return removeItemByObject((BitValue) value) >= 0;
	}
}
