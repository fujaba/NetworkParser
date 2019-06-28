package de.uniks.ludo.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.ludo.model.Target;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.ludo.model.LastField;
import de.uniks.ludo.model.Player;
import de.uniks.ludo.model.Field;
import de.uniks.ludo.model.Ludo;
import de.uniks.ludo.model.Meeple;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;

public class TargetSet extends SimpleSet<Target> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Target.PROPERTY_LASTFIELD,
		Target.PROPERTY_PLAYER,
		Target.PROPERTY_PREV,
		Target.PROPERTY_GAME,
		Target.PROPERTY_MEEPLE,
	};

	public static final TargetSet EMPTY_SET = new TargetSet().withFlag(TargetSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Target();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Target == false) {
			return null;
		}
		Target element = (Target)entity;
		if (Target.PROPERTY_LASTFIELD.equalsIgnoreCase(attribute)) {
			return element.getLastField();
		}

		if (Target.PROPERTY_PLAYER.equalsIgnoreCase(attribute)) {
			return element.getPlayer();
		}

		if (Target.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			return element.getPrev();
		}

		if (Target.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			return element.getGame();
		}

		if (Target.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			return element.getMeeple();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Target == false) {
			return false;
		}
		Target element = (Target)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Target.PROPERTY_LASTFIELD.equalsIgnoreCase(attribute)) {
			element.setLastField((LastField) value);
			return true;
		}

		if (Target.PROPERTY_PLAYER.equalsIgnoreCase(attribute)) {
			element.setPlayer((Player) value);
			return true;
		}

		if (Target.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			element.setPrev((Field) value);
			return true;
		}

		if (Target.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			element.setGame((Ludo) value);
			return true;
		}

		if (Target.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			element.setMeeple((Meeple) value);
			return true;
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return Target.class;
	}

	@Override
	public TargetSet getNewList(boolean keyValue) {
		return new TargetSet();
	}


	public LastFieldSet getLastField(LastField... filter) {
		LastFieldSet result = new LastFieldSet();
		if(listener != null) {
			result.withListener(listener);
			Target[] children = this.toArray(new Target[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getLastField(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Target obj : this) {
				result.add(obj.getLastField());
			}
			return result;
		}
		for (Target obj : this) {
			LastField item = obj.getLastField();
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


	public TargetSet withLastField(LastField value) {
		for (Target obj : this) {
			obj.withLastField(value);
		}
		return this;
	}
	public PlayerSet getPlayer(Player... filter) {
		PlayerSet result = new PlayerSet();
		if(listener != null) {
			result.withListener(listener);
			Target[] children = this.toArray(new Target[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPlayer(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Target obj : this) {
				result.add(obj.getPlayer());
			}
			return result;
		}
		for (Target obj : this) {
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


	public TargetSet withPlayer(Player value) {
		for (Target obj : this) {
			obj.withPlayer(value);
		}
		return this;
	}
	public FieldSet getPrev(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			Target[] children = this.toArray(new Target[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPrev(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Target obj : this) {
				result.add(obj.getPrev());
			}
			return result;
		}
		for (Target obj : this) {
			Field item = obj.getPrev();
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


	public TargetSet withPrev(Field value) {
		for (Target obj : this) {
			obj.withPrev(value);
		}
		return this;
	}
	public FieldSet getNext(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			Target[] children = this.toArray(new Target[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getNext(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Target obj : this) {
				result.add(obj.getNext());
			}
			return result;
		}
		for (Target obj : this) {
			Field item = obj.getNext();
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


	public TargetSet withNext(Field value) {
		for (Target obj : this) {
			obj.withNext(value);
		}
		return this;
	}
	public LudoSet getGame(Ludo... filter) {
		LudoSet result = new LudoSet();
		if(listener != null) {
			result.withListener(listener);
			Target[] children = this.toArray(new Target[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getGame(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Target obj : this) {
				result.add(obj.getGame());
			}
			return result;
		}
		for (Target obj : this) {
			Ludo item = obj.getGame();
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


	public TargetSet withGame(Ludo value) {
		for (Target obj : this) {
			obj.withGame(value);
		}
		return this;
	}
	public MeepleSet getMeeple(Meeple... filter) {
		MeepleSet result = new MeepleSet();
		if(listener != null) {
			result.withListener(listener);
			Target[] children = this.toArray(new Target[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getMeeple(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Target obj : this) {
				result.add(obj.getMeeple());
			}
			return result;
		}
		for (Target obj : this) {
			Meeple item = obj.getMeeple();
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


	public TargetSet withMeeple(Meeple value) {
		for (Target obj : this) {
			obj.withMeeple(value);
		}
		return this;
	}
}