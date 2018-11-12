package de.uniks.ludo.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.ludo.model.Dice;
import de.uniks.ludo.model.util.FieldSet;
import java.util.Collection;
import de.uniks.ludo.model.Player;
import de.uniks.ludo.model.util.PlayerSet;


public class Ludo {
	protected PropertyChangeSupport listeners = null;

	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (listeners != null) {
			listeners.firePropertyChange(propertyName, oldValue, newValue);
			return true;
		}
		return false;
	}

	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(listener);
		return true;
	}

	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(propertyName, listener);
		return true;
	}

	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(listener);
		}
		listeners.removePropertyChangeListener(listener);
		return true;
	}

	public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(propertyName, listener);
		}
		return true;
	}

	public static final String PROPERTY_DICE = "dice";

	private Dice dice = null;

	public Dice getDice() {
		return this.dice;
	}

	public boolean setDice(Dice value) {
		if (this.dice == value) {
			return false;
		}
		Dice oldValue = this.dice;
		if (this.dice != null) {
			this.dice = null;
			oldValue.setGame(null);
		}
		this.dice = value;
		if (value != null) {
			value.withGame(this);
		}
		firePropertyChange(PROPERTY_DICE, oldValue, value);
		return true;
	}

	public Ludo withDice(Dice value) {
		this.setDice(value);
		return this;
	}

	public Dice createDice() {
		Dice value = new Dice();
		withDice(value);
		return value;
	}

	public static final String PROPERTY_FIELD = "field";

	private FieldSet field = null;

	public FieldSet getField() {
		if(this.field == null) {
			return FieldSet.EMPTY_SET;
		}
		return this.field;
	}

	public boolean setField(Field... values) {
		if (values == null) {
			return true;
		}
		boolean result=true;
		if (this.field == null) {
			this.field = new FieldSet();
		}
		for (Field item : values) {
			if (item == null) {
				continue;
			}
			this.field.withVisible(true);
			boolean changed = this.field.add(item);
			this.field.withVisible(false);
			result = result & changed;
			if (changed) {
				item.setGame(this);
				firePropertyChange(PROPERTY_FIELD, null, item);
			}
		}
		return result;
	}

	public Ludo withField(Object... values) {
		if (values == null) {
			return this;
		}
		for (Object item : values) {
			if (item == null) {
				continue;
			}
			if (item instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) item;
				setField(collection.toArray(new Field[collection.size()]));
			} else {
				setField((Field) item);
			}
		}
		return this;
	}

	public Ludo withoutField(Field... value) {
		if(this.field == null) {
			return this;
		}
		for (Field item : value) {
			if (item != null) {
				if (this.field.remove(item)) {
					item.withGame(null);
				}
			}
		}
		return this;
	}

	public Field createField() {
		Field value = new Field();
		withField(value);
		return value;
	}

	public static final String PROPERTY_CURRENTPLAYER = "currentPlayer";

	private Player currentPlayer = null;

	public Player getCurrentPlayer() {
		return this.currentPlayer;
	}

	public boolean setCurrentPlayer(Player value) {
		if (this.currentPlayer == value) {
			return false;
		}
		Player oldValue = this.currentPlayer;
		if (this.currentPlayer != null) {
			this.currentPlayer = null;
			oldValue.setCurrentGame(null);
		}
		this.currentPlayer = value;
		if (value != null) {
			value.withCurrentGame(this);
		}
		firePropertyChange(PROPERTY_CURRENTPLAYER, oldValue, value);
		return true;
	}

	public Ludo withCurrentPlayer(Player value) {
		this.setCurrentPlayer(value);
		return this;
	}

	public Player createCurrentPlayer() {
		Player value = new Player();
		withCurrentPlayer(value);
		return value;
	}

	public static final String PROPERTY_PLAYERS = "players";

	private PlayerSet players = null;

	public PlayerSet getPlayers() {
		if(this.players == null) {
			return PlayerSet.EMPTY_SET;
		}
		return this.players;
	}

	public boolean setPlayers(Player... values) {
		if (values == null) {
			return true;
		}
		boolean result=true;
		if (this.players == null) {
			this.players = new PlayerSet();
		}
		for (Player item : values) {
			if (item == null) {
				continue;
			}
			this.players.withVisible(true);
			boolean changed = this.players.add(item);
			this.players.withVisible(false);
			result = result & changed;
			if (changed) {
				item.setGame(this);
				firePropertyChange(PROPERTY_PLAYERS, null, item);
			}
		}
		return result;
	}

	public Ludo withPlayers(Object... values) {
		if (values == null) {
			return this;
		}
		for (Object item : values) {
			if (item == null) {
				continue;
			}
			if (item instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) item;
				setPlayers(collection.toArray(new Player[collection.size()]));
			} else {
				setPlayers((Player) item);
			}
		}
		return this;
	}

	public Ludo withoutPlayers(Player... value) {
		if(this.players == null) {
			return this;
		}
		for (Player item : value) {
			if (item != null) {
				if (this.players.remove(item)) {
					item.withGame(null);
				}
			}
		}
		return this;
	}

	public Player createPlayers() {
		Player value = new Player();
		withPlayers(value);
		return value;
	}

	public static final String PROPERTY_WINNER = "winner";

	private Player winner = null;

	public Player getWinner() {
		return this.winner;
	}

	public boolean setWinner(Player value) {
		if (this.winner == value) {
			return false;
		}
		Player oldValue = this.winner;
		if (this.winner != null) {
			this.winner = null;
			oldValue.setWonGame(null);
		}
		this.winner = value;
		if (value != null) {
			value.withWonGame(this);
		}
		firePropertyChange(PROPERTY_WINNER, oldValue, value);
		return true;
	}

	public Ludo withWinner(Player value) {
		this.setWinner(value);
		return this;
	}

	public Player createWinner() {
		Player value = new Player();
		withWinner(value);
		return value;
	}
   public void init(Player... p1)    {
      
    }


}