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
import de.uniks.networkparser.list.SimpleList;

public class FeatureProperty implements Comparable<FeatureProperty> {
	public static final Clazz ALL = new Clazz("*");
	public static final String NAME="name";
	public static final String CLASSVALUE="classValue";
	public static final String CLASSSTRING="classString";

	private SimpleList<Clazz> includeClazz = new SimpleList<Clazz>();
	private SimpleList<Clazz> excludeClazz = new SimpleList<Clazz>();
	private SimpleList<String> path = new SimpleList<String>();
	private Class<?> classValue;
	private String value;
	private Feature name;

	public FeatureProperty(Feature name) {
		this.name = name;
		includeClazz.add(ALL);
	}

	public FeatureProperty withIncludeClazz(String... value) {
		if (value == null) {
			return this;
		}
		for (String item : value) {
			if (item != null) {
				includeClazz.add(new Clazz(item));
			}
		}

		return this;
	}

	public FeatureProperty withExcludeClazz(String... value) {
		if (value == null) {
			return this;
		}
		if (value.length > 0) {
			// remove ALL
			includeClazz.remove(ALL);
		}
		for (String item : value) {
			if (item != null) {
				excludeClazz.add(new Clazz(item));
			}
		}
		return this;
	}

	public FeatureProperty withExcludeClazz(Clazz... value) {
		if (value == null) {
			return this;
		}
		if (value.length > 0) {
			// remove ALL
			includeClazz.remove(ALL);
		}
		for (Clazz item : value) {
			if (item != null) {
				excludeClazz.add(item);
			}
		}
		return this;
	}

	public boolean match(Clazz... clazzes) {
		if(clazzes == null) {
			return true;
		}
		for(Clazz clazz : clazzes) {
			if(clazz == null) {
				return true;
			}
			if(match(clazz.getName(false)) == false) {
				return false;
			}
		}
		return true;
	}

	public boolean match(String clazzName) {
		// if Clazz is positive
		boolean result = false;
		if(this.classValue != null) {
			return this.classValue.getName().equals(clazzName);
		}
		for (Clazz item : includeClazz) {
			if (item == null) {
				continue;
			}
			if (ALL.getName(false).equals(item.getName(false))) {
				result = true;
				break;
			} else if (item.getName(false).equals(clazzName)) {
				result = true;
				break;
			}
		}

		for (Clazz item : excludeClazz) {
			if (item == null) {
				continue;
			}
			if (ALL.getName(false).equals(item.getName(false))) {
				result = false;
				break;
			} else if (item.getName(false).equals(clazzName)) {
				result = false;
				break;
			}
		}
		return result;
	}

	public SimpleList<Clazz> getClazzes() {
		return includeClazz;
	}

	public Feature getName() {
		return name;
	}

	public FeatureProperty withClazzValue(Class<?> value) {
		this.classValue = value;
		return this;
	}

	public FeatureProperty withPath(String... value) {
		if (value == null) {
			return this;
		}
		for (String item : value) {
			if (item != null) {
				path.add(item);
			}
		}
		return this;
	}

	public SimpleList<String> getPath() {
		return path;
	}

	public Class<?> getClassValue() {
		return classValue;
	}

	@Override
	public int compareTo(FeatureProperty o) {
		if (this.name == null) {
			return 1;
		}
		return this.name.compareTo(o.getName());
	}

	@Override
	public int hashCode() {
		if (name != null) {
			return name.hashCode();
		}
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Feature) {
			return obj.equals(this.name);
		}
		if (obj instanceof FeatureProperty == false) {
			return false;
		}
		return compareTo((FeatureProperty) obj) == 0;
	}

	@Override
	public String toString() {
		if (name != null) {
			return name.toString();
		}
		return super.toString();
	}

	public String getStringValue() {
		return value;
	}

	public FeatureProperty withStringValue(String value) {
		this.value = value;
		return this;
	}

	public Object getValue(String value) {
		if(NAME.equalsIgnoreCase(value)) {
			return name;
		}
		if(CLASSVALUE.equalsIgnoreCase(value)) {
			return classValue;
		}
		if(CLASSSTRING.equalsIgnoreCase(value)) {
			if(classValue!= null) {
				return classValue.getName();
			}
			return "";
		}
		return null;
	}

}
