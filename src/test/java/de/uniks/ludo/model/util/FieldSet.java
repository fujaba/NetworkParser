package de.uniks.ludo.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.ludo.model.Field;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.ludo.model.Ludo;
import de.uniks.ludo.model.Meeple;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;

public class FieldSet extends SimpleSet<Field> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Field.PROPERTY_PREV,
		Field.PROPERTY_GAME,
		Field.PROPERTY_MEEPLE,
	};

	public static final FieldSet EMPTY_SET = new FieldSet().withFlag(FieldSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Field();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Field == false) {
			return null;
		}
		Field element = (Field)entity;
		if (Field.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			return element.getPrev();
		}

		if (Field.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			return element.getGame();
		}

		if (Field.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			return element.getMeeple();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Field == false) {
			return false;
		}
		Field element = (Field)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Field.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			element.setPrev((Field) value);
			return true;
		}

		if (Field.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			element.setGame((Ludo) value);
			return true;
		}

		if (Field.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			element.setMeeple((Meeple) value);
			return true;
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return Field.class;
	}

	@Override
	public FieldSet getNewList(boolean keyValue) {
		return new FieldSet();
	}


	public FieldSet getPrev(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			Field[] children = this.toArray(new Field[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPrev(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Field obj : this) {
				result.add(obj.getPrev());
			}
			return result;
		}
		for (Field obj : this) {
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


	public FieldSet withPrev(Field value) {
		for (Field obj : this) {
			obj.withPrev(value);
		}
		return this;
	}
	public FieldSet getNext(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			Field[] children = this.toArray(new Field[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getNext(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Field obj : this) {
				result.add(obj.getNext());
			}
			return result;
		}
		for (Field obj : this) {
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


	public FieldSet withNext(Field value) {
		for (Field obj : this) {
			obj.withNext(value);
		}
		return this;
	}
	public LudoSet getGame(Ludo... filter) {
		LudoSet result = new LudoSet();
		if(listener != null) {
			result.withListener(listener);
			Field[] children = this.toArray(new Field[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getGame(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Field obj : this) {
				result.add(obj.getGame());
			}
			return result;
		}
		for (Field obj : this) {
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


	public FieldSet withGame(Ludo value) {
		for (Field obj : this) {
			obj.withGame(value);
		}
		return this;
	}
	public MeepleSet getMeeple(Meeple... filter) {
		MeepleSet result = new MeepleSet();
		if(listener != null) {
			result.withListener(listener);
			Field[] children = this.toArray(new Field[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getMeeple(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Field obj : this) {
				result.add(obj.getMeeple());
			}
			return result;
		}
		for (Field obj : this) {
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


	public FieldSet withMeeple(Meeple value) {
		for (Field obj : this) {
			obj.withMeeple(value);
		}
		return this;
	}
}