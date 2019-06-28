package org.sdmlib.test.examples.studyrightWithAssignments.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import org.sdmlib.test.examples.studyrightWithAssignments.model.TeachingAssistant;
import de.uniks.networkparser.list.SimpleSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Room;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.BooleanList;
import de.uniks.networkparser.SimpleEvent;

public class TeachingAssistantSet extends SimpleSet<TeachingAssistant> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		TeachingAssistant.PROPERTY_ROOM,
		TeachingAssistant.PROPERTY_CERTIFIED,
	};

	public static final TeachingAssistantSet EMPTY_SET = new TeachingAssistantSet().withFlag(TeachingAssistantSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TeachingAssistant();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof TeachingAssistant == false) {
			return null;
		}
		TeachingAssistant element = (TeachingAssistant)entity;
		if (TeachingAssistant.PROPERTY_ROOM.equalsIgnoreCase(attribute)) {
			return element.getRoom();
		}

		if (TeachingAssistant.PROPERTY_CERTIFIED.equalsIgnoreCase(attribute)) {
			return element.isCertified();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof TeachingAssistant == false) {
			return false;
		}
		TeachingAssistant element = (TeachingAssistant)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (TeachingAssistant.PROPERTY_ROOM.equalsIgnoreCase(attribute)) {
			element.setRoom((Room) value);
			return true;
		}

		if (TeachingAssistant.PROPERTY_CERTIFIED.equalsIgnoreCase(attribute)) {
			return element.setCertified((boolean) value);
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return TeachingAssistant.class;
	}

	@Override
	public TeachingAssistantSet getNewList(boolean keyValue) {
		return new TeachingAssistantSet();
	}


	public BooleanList isCertified(boolean... filter) {
		BooleanList result = new BooleanList();
		if(listener != null) {
			result.withListener(listener);
			TeachingAssistant[] children = this.toArray(new TeachingAssistant[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].isCertified(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (TeachingAssistant obj : this) {
				result.add(obj.isCertified());
			}
		} else {
			for (TeachingAssistant obj : this) {
				boolean item = obj.isCertified();
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
	public TeachingAssistantSet withCertified(boolean value) {
		for (TeachingAssistant obj : this) {
			obj.setCertified(value);
		}
		return this;
	}
	public RoomSet getRoom(Room... filter) {
		RoomSet result = new RoomSet();
		if(listener != null) {
			result.withListener(listener);
			TeachingAssistant[] children = this.toArray(new TeachingAssistant[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getRoom(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (TeachingAssistant obj : this) {
				result.add(obj.getRoom());
			}
			return result;
		}
		for (TeachingAssistant obj : this) {
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


	public TeachingAssistantSet withRoom(Room value) {
		for (TeachingAssistant obj : this) {
			obj.withRoom(value);
		}
		return this;
	}
}