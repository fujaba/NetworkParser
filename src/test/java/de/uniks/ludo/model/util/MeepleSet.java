package de.uniks.ludo.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.ludo.model.Meeple;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.ludo.model.Field;
import de.uniks.ludo.model.Player;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;

public class MeepleSet extends SimpleSet<Meeple> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Meeple.PROPERTY_FIELD,
		Meeple.PROPERTY_PLAYER,
	};

	public static final MeepleSet EMPTY_SET = new MeepleSet().withFlag(MeepleSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Meeple();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Meeple == false) {
			return null;
		}
		Meeple element = (Meeple)entity;
		if (Meeple.PROPERTY_FIELD.equalsIgnoreCase(attribute)) {
			return element.getField();
		}

		if (Meeple.PROPERTY_PLAYER.equalsIgnoreCase(attribute)) {
			return element.getPlayer();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Meeple == false) {
			return false;
		}
		Meeple element = (Meeple)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Meeple.PROPERTY_FIELD.equalsIgnoreCase(attribute)) {
			element.setField((Field) value);
			return true;
		}

		if (Meeple.PROPERTY_PLAYER.equalsIgnoreCase(attribute)) {
			element.setPlayer((Player) value);
			return true;
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return Meeple.class;
	}

	@Override
	public MeepleSet getNewList(boolean keyValue) {
		return new MeepleSet();
	}


	public FieldSet getField(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			Meeple[] children = this.toArray(new Meeple[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getField(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Meeple obj : this) {
				result.add(obj.getField());
			}
			return result;
		}
		for (Meeple obj : this) {
			Field item = obj.getField();
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


	public MeepleSet withField(Field value) {
		for (Meeple obj : this) {
			obj.withField(value);
		}
		return this;
	}
	public PlayerSet getPlayer(Player... filter) {
		PlayerSet result = new PlayerSet();
		if(listener != null) {
			result.withListener(listener);
			Meeple[] children = this.toArray(new Meeple[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPlayer(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Meeple obj : this) {
				result.add(obj.getPlayer());
			}
			return result;
		}
		for (Meeple obj : this) {
			Player item = obj.getPlayer();
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


	public MeepleSet withPlayer(Player value) {
		for (Meeple obj : this) {
			obj.withPlayer(value);
		}
		return this;
	}
}