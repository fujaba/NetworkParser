package de.uniks.networkparser.simple.modelA.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.simple.modelA.Person;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.simple.modelA.Room;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.list.StringList;

public class PersonSet extends SimpleSet<Person> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Person.PROPERTY_ROOM,
		Person.PROPERTY_AGE,
		Person.PROPERTY_NAME,
	SendableEntityCreator.DYNAMIC
	};

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Person();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Person == false) {
			return null;
		}
		Person element = (Person)entity;
		if (Person.PROPERTY_ROOM.equalsIgnoreCase(attribute)) {
			return element.getRoom();
		}

		if (Person.PROPERTY_AGE.equalsIgnoreCase(attribute)) {
		return element.getAge();
		}

		if (Person.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
		return element.getName();
		}

		if(SendableEntityCreator.DYNAMIC.equalsIgnoreCase(attribute)) {
			return element.getDynamicValues();
		}
		return element.getDynamicValue(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Person == false) {
			return false;
		}
		Person element = (Person)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Person.PROPERTY_ROOM.equalsIgnoreCase(attribute)) {
			element.setRoom((Room) value);
			return true;
		}

		if (Person.PROPERTY_AGE.equalsIgnoreCase(attribute)) {
			element.setAge((int) value);
			return true;
		}

		if (Person.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			element.setName((String) value);
			return true;
		}

		element.withDynamicValue(attribute, value);
		return true;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}	public static final PersonSet EMPTY_SET = new PersonSet().withFlag(PersonSet.READONLY);

	public Class<?> getTypClass() {
		return Person.class;
	}

	@Override
	public PersonSet getNewList(boolean keyValue) {
		return new PersonSet();
	}


	public NumberList getAge(int... filter) {
		NumberList result = new NumberList();
		if(listener != null) {
			result.withListener(listener);
			for(int i=0;i<size();i++) {
				listener.update(SimpleEvent.create(this, i, result, get(i), get(i).getAge(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Person obj : this) {
				result.add(obj.getAge());
			}
		} else {
			for (Person obj : this) {
				int item = obj.getAge();
				for(int i=0;i<filter.length;i++) {
					if (filter[i] == item) {
						result.add(item);
						break;
					}
				}
			}
		}
		return result;
	}
	public PersonSet filterAge(int minValue, int maxValue) {
		PersonSet result = new PersonSet();
		for(Person obj : this) {
			if (minValue <= obj.getAge() && maxValue >= obj.getAge()) {
				result.add(obj);
			}
		}
		return result;
	}

	public PersonSet withAge(int value) {
		for (Person obj : this) {
			obj.setAge(value);
		}
		return this;
	}
	public StringList getName(String... filter) {
		StringList result = new StringList();
		if(listener != null) {
			result.withListener(listener);
			for(int i=0;i<size();i++) {
				listener.update(SimpleEvent.create(this, i, result, get(i), get(i).getName(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Person obj : this) {
				result.add(obj.getName());
			}
		} else {
			for (Person obj : this) {
				String item = obj.getName();
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
	public PersonSet filterName(String minValue, String maxValue) {
		PersonSet result = new PersonSet();
		for(Person obj : this) {
			if (minValue.compareTo(obj.getName()) <= 0 && maxValue.compareTo(obj.getName()) >= 0) {
				result.add(obj);
			}
		}
		return result;
	}

	public PersonSet withName(String value) {
		for (Person obj : this) {
			obj.setName(value);
		}
		return this;
	}
	public RoomSet getRoom(Room... filter) {
		RoomSet result = new RoomSet();
		if(listener != null) {
			result.withListener(listener);
			for(int i=0;i<size();i++) {
				listener.update(SimpleEvent.create(this, i, result, get(i), get(i).getRoom(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Person obj : this) {
				result.add(obj.getRoom());
			}
			return result;
		}
		for (Person obj : this) {
			Room item = obj.getRoom();
			if(item != null) {
				for(int i=0;i<filter.length;i++) {
					if (item.equals(filter[i])) {
						result.add(item);
						break;
					}
				}
			}
		}
		return result;
	}


	public PersonSet withRoom(Room value) {
		for (Person obj : this) {
			obj.withRoom(value);
		}
		return this;
	}
}