package de.uniks.ludo.model;
import de.uniks.ludo.model.LastField;
import de.uniks.ludo.model.Player;


public class Target extends Field {


	public static final String PROPERTY_LASTFIELD = "lastField";

	private LastField lastField = null;

	public LastField getLastField() {
		return this.lastField;
	}

	public boolean setLastField(LastField value) {
		if (this.lastField == value) {
			return false;
		}
		LastField oldValue = this.lastField;
		if (this.lastField != null) {
			this.lastField = null;
			oldValue.setTarget(null);
		}
		this.lastField = value;
		if (value != null) {
			value.withTarget(this);
		}
		firePropertyChange(PROPERTY_LASTFIELD, oldValue, value);
		return true;
	}

	public Target withLastField(LastField value) {
		this.setLastField(value);
		return this;
	}

	public LastField createLastField() {
		LastField value = new LastField();
		withLastField(value);
		return value;
	}

	public static final String PROPERTY_PLAYER = "player";

	private Player player = null;

	public Player getPlayer() {
		return this.player;
	}

	public boolean setPlayer(Player value) {
		if (this.player == value) {
			return false;
		}
		Player oldValue = this.player;
		if (this.player != null) {
			this.player = null;
			oldValue.withoutTarget(this);
		}
		this.player = value;
		if (value != null) {
			value.withTarget(this);
		}
		firePropertyChange(PROPERTY_PLAYER, oldValue, value);
		return true;
	}

	public Target withPlayer(Player value) {
		this.setPlayer(value);
		return this;
	}

	public Player createPlayer() {
		Player value = new Player();
		withPlayer(value);
		return value;
	}
}