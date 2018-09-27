package de.uniks.networkparser.simple.modelA.util;
import de.uniks.networkparser.simple.modelA.Room;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.simple.modelA.Person;

public class RoomSet extends SimpleSet<Room> {
	public static final RoomSet EMPTY_SET = new RoomSet().withFlag(RoomSet.READONLY);

	public Class<?> getTypClass() {
		return Room.class;
	}

	@Override
	public RoomSet getNewList(boolean keyValue) {
		return new RoomSet();
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
			for (Room obj : this) {
				result.add(obj.getName());
			}
		} else {
			for (Room obj : this) {
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
	public RoomSet filterName(String minValue, String maxValue) {
		RoomSet result = new RoomSet();
		for(Room obj : this) {
			if (minValue.compareTo(obj.getName()) <= 0 && maxValue.compareTo(obj.getName()) >= 0) {
				result.add(obj);
			}
		}
		return result;
	}

	public RoomSet withName(String value) {
		for (Room obj : this) {
			obj.setName(value);
		}
		return this;
	}
	public PersonSet getPersons(Person... filter) {
		PersonSet result = new PersonSet();
		if(listener != null) {
			result.withListener(listener);
			for(int i=0;i<size();i++) {
				listener.update(SimpleEvent.create(this, i, result, get(i), get(i).getPersons(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Room obj : this) {
				result.addAll(obj.getPersons());
			}
			return result;
		}
		for (Room obj : this) {
			PersonSet item = obj.getPersons();
			if(item != null) {
				for(int i=0;i<filter.length;i++) {
					if (item.contains(filter[i])) {
						result.add(filter[i]);
						break;
					}
				}
			}
		}
		return result;
	}


	public RoomSet withPersons(Person value) {
		for (Room obj : this) {
			obj.withPersons(value);
		}
		return this;
	}
	public RoomSet init() {
		return RoomSet.EMPTY_SET;
	}

}