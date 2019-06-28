package de.uniks.ludo.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.ludo.model.Start;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.ludo.model.Player;
import de.uniks.ludo.model.Field;
import de.uniks.ludo.model.Ludo;
import de.uniks.ludo.model.Meeple;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;

public class StartSet extends SimpleSet<Start> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Start.PROPERTY_PLAYER,
		Start.PROPERTY_PREV,
		Start.PROPERTY_GAME,
		Start.PROPERTY_MEEPLE,
	};

	public static final StartSet EMPTY_SET = new StartSet().withFlag(StartSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Start();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Start == false) {
			return null;
		}
		Start element = (Start)entity;
		if (Start.PROPERTY_PLAYER.equalsIgnoreCase(attribute)) {
			return element.getPlayer();
		}

		if (Start.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			return element.getPrev();
		}

		if (Start.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			return element.getGame();
		}

		if (Start.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			return element.getMeeple();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Start == false) {
			return false;
		}
		Start element = (Start)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Start.PROPERTY_PLAYER.equalsIgnoreCase(attribute)) {
			element.setPlayer((Player) value);
			return true;
		}

		if (Start.PROPERTY_PREV.equalsIgnoreCase(attribute)) {
			element.setPrev((Field) value);
			return true;
		}

		if (Start.PROPERTY_GAME.equalsIgnoreCase(attribute)) {
			element.setGame((Ludo) value);
			return true;
		}

		if (Start.PROPERTY_MEEPLE.equalsIgnoreCase(attribute)) {
			element.setMeeple((Meeple) value);
			return true;
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return Start.class;
	}

	@Override
	public StartSet getNewList(boolean keyValue) {
		return new StartSet();
	}


	public PlayerSet getPlayer(Player... filter) {
		PlayerSet result = new PlayerSet();
		if(listener != null) {
			result.withListener(listener);
			Start[] children = this.toArray(new Start[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPlayer(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Start obj : this) {
				result.add(obj.getPlayer());
			}
			return result;
		}
		for (Start obj : this) {
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


	public StartSet withPlayer(Player value) {
		for (Start obj : this) {
			obj.withPlayer(value);
		}
		return this;
	}
	public FieldSet getPrev(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			Start[] children = this.toArray(new Start[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPrev(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Start obj : this) {
				result.add(obj.getPrev());
			}
			return result;
		}
		for (Start obj : this) {
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


	public StartSet withPrev(Field value) {
		for (Start obj : this) {
			obj.withPrev(value);
		}
		return this;
	}
	public FieldSet getNext(Field... filter) {
		FieldSet result = new FieldSet();
		if(listener != null) {
			result.withListener(listener);
			Start[] children = this.toArray(new Start[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getNext(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Start obj : this) {
				result.add(obj.getNext());
			}
			return result;
		}
		for (Start obj : this) {
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


	public StartSet withNext(Field value) {
		for (Start obj : this) {
			obj.withNext(value);
		}
		return this;
	}
	public LudoSet getGame(Ludo... filter) {
		LudoSet result = new LudoSet();
		if(listener != null) {
			result.withListener(listener);
			Start[] children = this.toArray(new Start[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getGame(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Start obj : this) {
				result.add(obj.getGame());
			}
			return result;
		}
		for (Start obj : this) {
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


	public StartSet withGame(Ludo value) {
		for (Start obj : this) {
			obj.withGame(value);
		}
		return this;
	}
	public MeepleSet getMeeple(Meeple... filter) {
		MeepleSet result = new MeepleSet();
		if(listener != null) {
			result.withListener(listener);
			Start[] children = this.toArray(new Start[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getMeeple(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Start obj : this) {
				result.add(obj.getMeeple());
			}
			return result;
		}
		for (Start obj : this) {
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


	public StartSet withMeeple(Meeple value) {
		for (Start obj : this) {
			obj.withMeeple(value);
		}
		return this;
	}
}