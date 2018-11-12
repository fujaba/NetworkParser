package de.uniks.ludo.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.ludo.model.LastField;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.ludo.model.Target;
import de.uniks.ludo.model.Field;
import de.uniks.ludo.model.Ludo;
import de.uniks.ludo.model.Meeple;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;

public class LastFieldSet extends SimpleSet<LastField> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		LastField.PROPERTY_TARGET,
		LastField.PROPERTY_PREV,
		LastField.PROPERTY_GAME,
		LastField.PROPERTY_MEEPLE,
	};

	public static final LastFieldSet EMPTY_SET = new LastFieldSet().withFlag(LastFieldSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new LastField();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof LastField == false) {
			return null;
		}
		LastField element = (LastField)entity;
		if (LastField.PROPERTY_TARGET.equalsIgnoreCase(attribute)) {
			return element.getTarget();
		}

		if (LastField.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			return element.getPrev();
		}

		if (LastField.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			return element.getGame();
		}

		if (LastField.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			return element.getMeeple();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof LastField == false) {
			return false;
		}
		LastField element = (LastField)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (LastField.PROPERTY_TARGET.equalsIgnoreCase(attribute)) {
			element.setTarget((Target) value);
			return true;
		}

		if (LastField.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			element.setPrev((Field) value);
			return true;
		}

		if (LastField.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			element.setGame((Ludo) value);
			return true;
		}

		if (LastField.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			element.setMeeple((Meeple) value);
			return true;
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return LastField.class;
	}

	@Override
	public LastFieldSet getNewList(boolean keyValue) {
		return new LastFieldSet();
	}


	public TargetSet getTarget(Target... filter) {
		TargetSet result = new TargetSet();
		if(listener != null) {
			result.withListener(listener);
			LastField[] children = this.toArray(new LastField[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getTarget(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (LastField obj : this) {
				result.add(obj.getTarget());
			}
			return result;
		}
		for (LastField obj : this) {
			Target item = obj.getTarget();
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


	public LastFieldSet withTarget(Target value) {
		for (LastField obj : this) {
			obj.withTarget(value);
		}
		return this;
	}
	public FieldSet getPrev(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			LastField[] children = this.toArray(new LastField[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPrev(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (LastField obj : this) {
				result.add(obj.getPrev());
			}
			return result;
		}
		for (LastField obj : this) {
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


	public LastFieldSet withPrev(Field value) {
		for (LastField obj : this) {
			obj.withPrev(value);
		}
		return this;
	}
	public FieldSet getNext(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			LastField[] children = this.toArray(new LastField[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getNext(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (LastField obj : this) {
				result.add(obj.getNext());
			}
			return result;
		}
		for (LastField obj : this) {
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


	public LastFieldSet withNext(Field value) {
		for (LastField obj : this) {
			obj.withNext(value);
		}
		return this;
	}
	public LudoSet getGame(Ludo... filter) {
		LudoSet result = new LudoSet();
		if(listener != null) {
			result.withListener(listener);
			LastField[] children = this.toArray(new LastField[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getGame(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (LastField obj : this) {
				result.add(obj.getGame());
			}
			return result;
		}
		for (LastField obj : this) {
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


	public LastFieldSet withGame(Ludo value) {
		for (LastField obj : this) {
			obj.withGame(value);
		}
		return this;
	}
	public MeepleSet getMeeple(Meeple... filter) {
		MeepleSet result = new MeepleSet();
		if(listener != null) {
			result.withListener(listener);
			LastField[] children = this.toArray(new LastField[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getMeeple(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (LastField obj : this) {
				result.add(obj.getMeeple());
			}
			return result;
		}
		for (LastField obj : this) {
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


	public LastFieldSet withMeeple(Meeple value) {
		for (LastField obj : this) {
			obj.withMeeple(value);
		}
		return this;
	}
}