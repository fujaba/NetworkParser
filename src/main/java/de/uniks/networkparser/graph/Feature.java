package de.uniks.networkparser.graph;

import java.util.LinkedHashSet;

/*
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
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

/**
 * Feature for Modell.
 *
 * @author Stefan Lindel
 */
public class Feature implements Comparable<Feature> {
	
	/** The Constant ALL. */
	public static final Clazz ALL = new Clazz("*");
	
	/** The Constant NAME. */
	public static final String NAME = "name";
	
	/** The Constant CLASSVALUE. */
	public static final String CLASSVALUE = "classValue";
	
	/** The Constant CLASSSTRING. */
	public static final String CLASSSTRING = "classString";

	private SimpleList<Clazz> includeClazz = new SimpleList<Clazz>();
	private SimpleList<Clazz> excludeClazz = new SimpleList<Clazz>();
	private SimpleList<String> path = new SimpleList<String>();
	private Class<?> classValue;
	private String value;
	private String name;
	private boolean reference;

	/* Constructor for Reference */
	protected Feature(String name) {
		this.name = name;
		this.reference = true;
	}

	/**
	 * Instantiates a new feature.
	 *
	 * @param ref the ref
	 */
	/* Real Constructor */
	public Feature(Feature ref) {
		if (ref != null) {
			this.name = ref.getName();
		}
		includeClazz.add(ALL);
	}

	/**
	 * Creates the.
	 *
	 * @return the feature
	 */
	public Feature create() {
		if (Feature.SETCLASS.equals(this)) {
			return newInstance(this).withClazzValue(SimpleSet.class);
		}
		if (Feature.CODESTYLE.equals(this)) {
			return newInstance(this).withStringValue(CODESTYLE_STANDARD);
		}
		return newInstance(this);
	}

	protected Feature newInstance(Feature ref) {
		return new Feature(ref);
	}

	/** The Constant PROPERTYCHANGESUPPORT. */
	/* For Generate PropertyChange */
	public static final Feature PROPERTYCHANGESUPPORT = new Feature("PROPERTYCHANGESUPPORT");
	
	/** The Constant SERIALIZATION. */
	/* For Generate Creator */
	public static final Feature SERIALIZATION = new Feature("SERIALIZATION");
	
	/** The Constant SETCLASS. */
	/* For Generate Set Class */
	public static final Feature SETCLASS = new Feature("SETCLASS");
	
	/** The Constant JUNIT. */
	/* For Generate JUNIT */
	public static final Feature JUNIT = new Feature("JUNIT");
	
	/** The Constant PATTERN. */
	/* For Generate PATTERN */
	public static final Feature PATTERN = new Feature("PATTERN");

	/** The Constant DYNAMICVALUES. */
	/* For Generate dynamic Values */
	public static final Feature DYNAMICVALUES = new Feature("DYNAMICVALUES");

	/** The Constant CODESTYLE. */
	/* For Generator CODESTYLE STANDARD OR DIVIDED */
	public static final Feature CODESTYLE = new Feature("CODESTYLE");

	/** The Constant DIFFERENCE_BEHAVIOUR. */
	public static final Feature DIFFERENCE_BEHAVIOUR = new Feature("DIFFERENCE_BEHAVIOUR");

	/** The Constant METADATA. */
	public static final Feature METADATA = new Feature("METADATA");

	/** The Constant DOCUMENTATION. */
	public static final Feature DOCUMENTATION = new Feature("DOCUMENTATION");

	/** The Constant SOURCECODE. */
	public static final Feature SOURCECODE = new Feature("SOURCECODE");

	/** The Constant GENCODE. */
	public static final Feature GENCODE = new Feature("GENCODE");

	/** The Constant allGenerateFlags. */
	public static final FeatureSet allGenerateFlags = new FeatureSet().with(PROPERTYCHANGESUPPORT, SERIALIZATION,
			SETCLASS, JUNIT, PATTERN, DYNAMICVALUES, CODESTYLE);

	/**
	 * Value of.
	 *
	 * @param string the string
	 * @return the feature
	 */
	public static Feature valueOf(String string) {
		for (Feature feature : allGenerateFlags) {
			if (feature.getName().equals(string)) {
				return feature.create();
			}
		}
		return null;
	}

	/** The Constant CODESTYLE_STANDARD. */
	public static final String CODESTYLE_STANDARD = "standard";
	
	/** The Constant CODESTYLE_DIVIDED. */
	public static final String CODESTYLE_DIVIDED = "divided";

	/**
	 * Creates the none.
	 *
	 * @return the feature set
	 */
	public static final FeatureSet createNone() {
		return new FeatureSet();
	}

	/**
	 * Creates the all.
	 *
	 * @return the feature set
	 */
	public static FeatureSet createAll() {
		FeatureSet result = new FeatureSet().with(PROPERTYCHANGESUPPORT, SERIALIZATION, PATTERN, SETCLASS.create(),
				CODESTYLE.create());
		return result;
	}

	/**
	 * Creates the stand alone.
	 *
	 * @return the feature set
	 */
	public static FeatureSet createStandAlone() {
		FeatureSet result = new FeatureSet().with(PROPERTYCHANGESUPPORT, CODESTYLE.create());
		result.add(SETCLASS.create().withClazzValue(LinkedHashSet.class));
		return result;
	}

	/**
	 * With include clazz.
	 *
	 * @param value the value
	 * @return the feature
	 */
	public Feature withIncludeClazz(String... value) {
		if (this.reference) {
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

	/**
	 * With exclude clazz.
	 *
	 * @param value the value
	 * @return the feature
	 */
	public Feature withExcludeClazz(String... value) {
		if (this.reference) {
			return null;
		}
		if (value == null) {
			return this;
		}
		if (value.length > 0) {
			/* remove ALL */
			includeClazz.remove(ALL);
		}
		for (String item : value) {
			if (item != null) {
				excludeClazz.add(new Clazz(item));
			}
		}
		return this;
	}

	/**
	 * With exclude clazz.
	 *
	 * @param value the value
	 * @return the feature
	 */
	public Feature withExcludeClazz(Clazz... value) {
		if (this.reference) {
			return null;
		}
		if (value == null) {
			return this;
		}
		if (value.length > 0) {
			/* remove ALL */
			includeClazz.remove(ALL);
		}
		for (Clazz item : value) {
			if (item != null) {
				excludeClazz.add(item);
			}
		}
		return this;
	}

	/**
	 * Match.
	 *
	 * @param clazzes the clazzes
	 * @return true, if successful
	 */
	public boolean match(Clazz... clazzes) {
		if (clazzes == null) {
			return true;
		}
		for (Clazz clazz : clazzes) {
			if (clazz == null) {
				return true;
			}
			if (!match(clazz.getName(false))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Match.
	 *
	 * @param clazzName the clazz name
	 * @return true, if successful
	 */
	public boolean match(String clazzName) {
		/* if Clazz is positive */
		boolean result = false;
		if (this.classValue != null) {
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

	/**
	 * Gets the clazzes.
	 *
	 * @return the clazzes
	 */
	public SimpleList<Clazz> getClazzes() {
		return includeClazz;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * With clazz value.
	 *
	 * @param value the value
	 * @return the feature
	 */
	public Feature withClazzValue(Class<?> value) {
		if (this.reference) {
			return null;
		}
		this.classValue = value;
		return this;
	}

	/**
	 * With path.
	 *
	 * @param value the value
	 * @return the feature
	 */
	public Feature withPath(String... value) {
		if (this.reference) {
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

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public SimpleList<String> getPath() {
		return path;
	}

	/**
	 * Gets the class value.
	 *
	 * @return the class value
	 */
	public Class<?> getClassValue() {
		return classValue;
	}

	/**
	 * Compare to.
	 *
	 * @param o the o
	 * @return the int
	 */
	@Override
	public int compareTo(Feature o) {
		if (this.name == null || o == null || o.getName() == null) {
			return 1;
		}
		return this.name.compareTo(o.getName());
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		if (name != null) {
			return name.hashCode();
		}
		return super.hashCode();
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return obj.equals(this.name);
		}
		if (!(obj instanceof Feature)) {
			return false;
		}
		return compareTo((Feature) obj) == 0;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		if (name != null) {
			return name.toString();
		}
		return "NONE:" + super.toString();
	}

	/**
	 * Gets the string value.
	 *
	 * @return the string value
	 */
	public String getStringValue() {
		return value;
	}

	/**
	 * With string value.
	 *
	 * @param value the value
	 * @return the feature
	 */
	public Feature withStringValue(String value) {
		if (this.reference) {
			return null;
		}
		this.value = value;
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @param value the value
	 * @return the value
	 */
	public Object getValue(String value) {
		if (NAME.equalsIgnoreCase(value)) {
			return name;
		}
		if (CLASSVALUE.equalsIgnoreCase(value)) {
			return classValue;
		}
		if (CLASSSTRING.equalsIgnoreCase(value)) {
			if (classValue != null) {
				return classValue.getName();
			}
			return "";
		}
		return null;
	}
}
