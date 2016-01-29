package de.uniks.networkparser.test.model;

public class Barbarian {
	public static final String PROPERTY_POSITION="position";
	public static final String PROPERTY_GAME="game";
	private int position;
	private Game game;

	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public Object get(String attribute) {
		if(PROPERTY_POSITION.equalsIgnoreCase(attribute)){
			return getPosition();
		} else if(PROPERTY_GAME.equalsIgnoreCase(attribute)){
			return getGame();
		}

		return null;
	}
	public boolean set(String attribute, Object value) {
		if(PROPERTY_POSITION.equalsIgnoreCase(attribute)){
			setPosition((Integer.valueOf("" +value)));
			return true;
		} else if(PROPERTY_GAME.equalsIgnoreCase(attribute)){
			this.setGame((Game) value);
			return true;
		}

		return false;
	}
	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
	}
}
