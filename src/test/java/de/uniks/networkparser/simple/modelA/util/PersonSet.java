package de.uniks.networkparser.simple.modelA.util;
import de.uniks.networkparser.simple.modelA.Person;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.simple.modelA.Room;

public class PersonSet extends SimpleSet<Person> {
	public static final PersonSet EMPTY_SET = new PersonSet().withFlag(PersonSet.READONLY);

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