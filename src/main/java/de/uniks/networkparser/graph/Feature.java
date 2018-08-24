package de.uniks.networkparser.graph;

import java.util.LinkedHashSet;

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
import de.uniks.networkparser.list.SimpleSet;

public class Feature implements Comparable<Feature> {
	public static final Clazz ALL = new Clazz("*");
	public static final String NAME="name";
	public static final String CLASSVALUE="classValue";
	public static final String CLASSSTRING="classString";

	private SimpleList<Clazz> includeClazz = new SimpleList<Clazz>();
	private SimpleList<Clazz> excludeClazz = new SimpleList<Clazz>();
	private SimpleList<String> path = new SimpleList<String>();
	private Class<?> classValue;
	private String value;
	private String name;
	private boolean reference;

	// Constructor for Reference
	protected Feature(String name) {
	}

	// Real Constructor
	public Feature(Feature ref) {
		this.name = ref.getName();
		includeClazz.add(ALL);
	}

	public Feature create() {
		if(Feature.SETCLASS.equals(this)) {
			return newInstance(this).withClazzValue(SimpleSet.class);
		}
		if(Feature.CODESTYLE.equals(this)) {
			return newInstance(this).withStringValue(CODESTYLE_STANDARD);
		}
		return newInstance(this);
	}
	
	protected Feature newInstance(Feature ref) {
		return new Feature(ref);
	}

	
	/* For Generate PropertyChange */
	public static final Feature PROPERTYCHANGESUPPORT = new Feature("PROPERTYCHANGESUPPORT");
	/* For Generate Creator */
	public static final Feature SERIALIZATION = new Feature("SERIALIZATION");
	/* For Generate Set Class */
	public static final Feature SETCLASS = new Feature("SETCLASS");
	/* For Generate JUNIT */
	public static final Feature JUNIT = new Feature("JUNIT");

	/* For Generate dynamic Values */
	public static final Feature DYNAMICVALUES = new Feature("DYNAMICVALUES");

	/* For Generator CODESTYLE STANDARD OR DIVIDED*/
	public static final Feature CODESTYLE = new Feature("CODESTYLE");

	public static final Feature DIFFERENCE_BEHAVIOUR = new Feature("DIFFERENCE_BEHAVIOUR");
	
	public static final Feature METADATA = new Feature("METADATA");

	public static final Feature DOCUMENTATION = new Feature("DOCUMENTATION");
	
	public static final Feature SOURCECODE = new Feature("SOURCECODE");

	public static final Feature GENCODE = new Feature("GENCODE");
	//EMFSTYLE,	// For Generate EMF-Style 
	
	public static final FeatureSet allGeneateFlags = new FeatureSet()
			.with(PROPERTYCHANGESUPPORT, SERIALIZATION, SETCLASS, JUNIT,DYNAMICVALUES, CODESTYLE);

	public static Feature valueOf(String string) {
		for(Feature feature : allGeneateFlags) {
			if(feature.getName().equals(string)) {
				return feature.create();
			}
		}
		return null;
	}


	public static final String CODESTYLE_STANDARD = "standard";
	public static final String CODESTYLE_DIVIDED = "divided";

	public static final FeatureSet getNone() {
		return new FeatureSet();
	}

	public static FeatureSet getAll() {
		FeatureSet result = new FeatureSet().with(PROPERTYCHANGESUPPORT, SERIALIZATION, SETCLASS.create(), CODESTYLE.create());
		return result;
	}

	public static FeatureSet getStandAlone() {
		FeatureSet result = new FeatureSet().with(PROPERTYCHANGESUPPORT, CODESTYLE.create());
		result.add(SETCLASS.create().withClazzValue(LinkedHashSet.class));
		return result;
	}


	public Feature withIncludeClazz(String... value) {
		if(this.reference) {
			return null;
		}
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

	public Feature withExcludeClazz(String... value) {
		if(this.reference) {
			return null;
		}
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

	public Feature withExcludeClazz(Clazz... value) {
		if(this.reference) {
			return null;
		}
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

	public String getName() {
		return name;
	}

	public Feature withClazzValue(Class<?> value) {
		if(this.reference) {
			return null;
		}
		this.classValue = value;
		return this;
	}

	public Feature withPath(String... value) {
		if(this.reference) {
			return null;
		}
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
	public int compareTo(Feature o) {
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
		if (obj instanceof String) {
			return obj.equals(this.name);
		}
		if (obj instanceof Feature == false) {
			return false;
		}
		return compareTo((Feature) obj) == 0;
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

	public Feature withStringValue(String value) {
		if(this.reference) {
			return null;
		}
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
