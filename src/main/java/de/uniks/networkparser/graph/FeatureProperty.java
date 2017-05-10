package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleList;

public class FeatureProperty implements Comparable<FeatureProperty> {
	public static final Clazz ALL = new Clazz("*");

	private SimpleList<Clazz> includeClazz = new SimpleList<Clazz>();
	private SimpleList<Clazz> excludeClazz = new SimpleList<Clazz>();
	private SimpleList<String> path = new SimpleList<String>();
	private Class<?> classValue;
	private String value;
	private Feature name;

	FeatureProperty(Feature name) {
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

}
