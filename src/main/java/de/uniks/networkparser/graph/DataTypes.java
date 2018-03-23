package de.uniks.networkparser.graph;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
