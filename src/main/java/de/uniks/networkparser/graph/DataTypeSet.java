package de.uniks.networkparser.graph;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class DataTypeSet.
 *
 * @author Stefan
 */
public class DataTypeSet extends DataType {
	private DataType generic;

	DataTypeSet() {
		super(SimpleSet.class.getName());
		this.value.withExternal(true);
	}

	private DataTypeSet withGeneric(DataType value) {
		this.generic = value;
		return this;
	}

	/**
	 * Gets the generic.
	 *
	 * @return the generic
	 */
	public DataType getGeneric() {
		return generic;
	}

	/**
	 * Gets the name.
	 *
	 * @param shortName the short name
	 * @return the name
	 */
	@Override
	public String getName(boolean shortName) {
		return getInternName(shortName, false);
	}

	@Override
	protected String getInternName(boolean shortName, boolean primitivAllow) {
		if (this.value == null || generic == null) {
			return null;
		}
		return this.value.getName(shortName) + "<" + generic.getInternName(shortName, primitivAllow) + ">";
	}

	/**
	 * Creates the.
	 *
	 * @param genericType the generic type
	 * @return the data type set
	 */
	public static DataTypeSet create(Object genericType) {
		return new DataTypeSet().withGeneric(DataType.create(genericType));
	}

	/**
	 * Creates the.
	 *
	 * @param container the container
	 * @param genericType the generic type
	 * @return the data type set
	 */
	public static DataTypeSet create(Clazz container, Object genericType) {
		DataTypeSet list = new DataTypeSet().withGeneric(DataType.create(genericType));
		if (container != null) {
			list.value = container;
		}
		return list;
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof DataTypeSet)) {
			return false;
		}
		if (obj.hashCode() == this.hashCode()) {
			return true;
		}
		if (this.generic == null) {
			return ((DataTypeSet) obj).getGeneric() == null;
		}
		return ((DataTypeSet) obj).getGeneric().equals(this.generic);
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * With external.
	 *
	 * @param external the external
	 * @return the data type set
	 */
	@Override
	public DataTypeSet withExternal(boolean external) {
		super.withExternal(external);
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @param value the value
	 * @return the value
	 */
	public Object getValue(String value) {
		if (PROPERTY_NAME.equals(value)) {
			return getGeneric().getName(true);
		}
		if (PROPERTY_CONTAINER.equals(value)) {
			return getClazz().getName(true);
		}
		if (PROPERTY_CATEGORIE.equals(value)) {
			return "SET";
		}
		return super.getValue(value);
	}
	
	/**
	 * With array.
	 *
	 * @param value the value
	 * @return the data type set
	 */
	@Override
	public DataTypeSet withArray(boolean value) {
		super.withArray(value);
		return this;
	}
}
