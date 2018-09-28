package org.sdmlib.test.examples.studyrightWithAssignments.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import org.sdmlib.test.examples.studyrightWithAssignments.model.President;
import de.uniks.networkparser.list.SimpleSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.University;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;

public class PresidentSet extends SimpleSet<President> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		President.PROPERTY_UNIVERSITY,
	};

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new President();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof President == false) {
			return null;
		}
		President element = (President)entity;
		if (President.PROPERTY_UNIVERSITY.equalsIgnoreCase(attribute)) {
			return element.getUniversity();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof President == false) {
			return false;
		}
		President element = (President)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (President.PROPERTY_UNIVERSITY.equalsIgnoreCase(attribute)) {
			element.setUniversity((University) value);
			return true;
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}	public static final PresidentSet EMPTY_SET = new PresidentSet().withFlag(PresidentSet.READONLY);

	public Class<?> getTypClass() {
		return President.class;
	}

	@Override
	public PresidentSet getNewList(boolean keyValue) {
		return new PresidentSet();
	}


	public UniversitySet getUniversity(University... filter) {
		UniversitySet result = new UniversitySet();
		if(listener != null) {
			result.withListener(listener);
			for(int i=0;i<size();i++) {
				listener.update(SimpleEvent.create(this, i, result, get(i), get(i).getUniversity(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (President obj : this) {
				result.add(obj.getUniversity());
			}
			return result;
		}
		for (President obj : this) {
			University item = obj.getUniversity();
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


	public PresidentSet withUniversity(University value) {
		for (President obj : this) {
			obj.withUniversity(value);
		}
		return this;
	}
}