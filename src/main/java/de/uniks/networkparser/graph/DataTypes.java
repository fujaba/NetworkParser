package de.uniks.networkparser.graph;

import java.util.Collection;

import de.uniks.networkparser.list.SimpleSet;

public class DataTypes extends DataType {
	private Object generics;

	DataTypes(String value) {
		super(value);
	}

	DataTypes(Clazz value) {
		super(value);
	}

	public static DataTypes create(Clazz typ) {
		return new DataTypes(typ);
	}

	public static DataTypes create(String typ) {
		return new DataTypes(typ);
	}

	public static DataTypes create(Class<?> typ) {
		return new DataTypes(new Clazz(typ));
	}

	protected DataTypes withGeneric(DataType... values) {
		if(values == null) {
			return this;
		}
		if(values.length == 1 && this.generics == null) {
			this.generics = values[0];
			return this;
		}
		SimpleSet<DataType> list = new SimpleSet<DataType>();
		if(this.generics instanceof SimpleSet<?>) {
			list.withList((Collection<?>) this.generics);
		}else {
			list.with(this.generics);
		}
		this.generics = list;
		for(DataType type : values) {
			list.add(type);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public SimpleSet<DataType> getGenerics() {
		if(generics instanceof SimpleSet<?>) {
			return (SimpleSet<DataType>)generics;
		}
		SimpleSet<DataType> list = new SimpleSet<DataType>();
		if(generics == null) {
			return list;
		}
		if(generics instanceof DataType) {
			list.add(generics);
		}
		return list;
	}
}
