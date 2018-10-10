package org.sdmlib.simple.model.enums_d.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import org.sdmlib.simple.model.enums_d.TestEnum;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.SimpleEvent;

public class TestEnumSet extends SimpleSet<TestEnum> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		TestEnum.PROPERTY_VALUE,
	};

	public static final TestEnumSet EMPTY_SET = new TestEnumSet().withFlag(TestEnumSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof TestEnum == false) {
			return null;
		}
		TestEnum element = (TestEnum)entity;
		if (TestEnum.PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return element.getValue();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof TestEnum == false) {
			return false;
		}
		TestEnum element = (TestEnum)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (TestEnum.PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return element.setValue((Object) value);
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return TestEnum.class;
	}

	@Override
	public TestEnumSet getNewList(boolean keyValue) {
		return new TestEnumSet();
	}


	public SimpleList<Object> getValue(Object... filter) {
		SimpleList<Object> result = new SimpleList<Object>();
		if(listener != null) {
			result.withListener(listener);
			TestEnum[] children = this.toArray(new TestEnum[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getValue(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (TestEnum obj : this) {
				result.add(obj.getValue());
			}
		} else {
			for (TestEnum obj : this) {
				Object item = obj.getValue();
				for(int i=0;i<filter.length;i++) {
					if (filter[i].equals(item)) {
						result.add(item);
						break;
					}
				}
			}
		}
		return result;
	}
	public TestEnumSet filterValue(Object minValue, Object maxValue) {
		TestEnumSet result = new TestEnumSet();
		for(TestEnum obj : this) {
			if (	minValue.hashCode() <= obj.getValue().hashCode() && maxValue.hashCode() >= obj.getValue().hashCode()) {
				result.add(obj);
			}
		}
		return result;
	}

	public TestEnumSet withValue(Object value) {
		for (TestEnum obj : this) {
			obj.setValue(value);
		}
		return this;
	}
}