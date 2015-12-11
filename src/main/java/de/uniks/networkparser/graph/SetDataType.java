package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

public class SetDataType extends DataType{
	private DataType generic;
	
	SetDataType() {
		super(SimpleSet.class.getName());
		this.value.withExternal(true);
	}
	
	public static SetDataType ref(Clazz value) {
		SetDataType result = new SetDataType().withGeneric(DataType.ref(value));
		return result;
	}
	public static SetDataType ref(String value) {
		SetDataType result = new SetDataType().withGeneric(DataType.ref(value));
		return result;
	}
	
	public static SetDataType ref(DataType value) {
		SetDataType result = new SetDataType().withGeneric(value);
		return result;
	}


	private SetDataType withGeneric(DataType value) {
		this.generic = value;
		return this;
	}

	public DataType getGeneric() {
		return generic;
	}
	
	@Override
	public String getName(boolean shortName) {
		if (this.value == null) {
			return null;
		}
		return this.value.getName(shortName) + "<" + generic.getName(shortName) + ">";
	}
}
