/*
   Copyright (c) 2013 zuendorf

   Permission is hereby granted, free of charge, to any person obtaining a copy of this software
   and associated documentation files (the "Software"), to deal in the Software without restriction,
   including without limitation the rights to use, copy, modify, merge, publish, distribute,
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:

   The above copyright notice and this permission notice shall be included in all copies or
   substantial portions of the Software.

   The Software shall be used for Good, not Evil.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.uniks.networkparser.test.model.ludo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashSet;

import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class Player {
	//==========================================================================
	public Object get(String attrName) {
		if (PROPERTY_COLOR.equalsIgnoreCase(attrName)) {
			return getColor();
		}
		if (PROPERTY_ENUMCOLOR.equalsIgnoreCase(attrName)) {
			return getEnumColor();
		}
		if (PROPERTY_NAME.equalsIgnoreCase(attrName)) {
			return getName();
		}
		if (PROPERTY_X.equalsIgnoreCase(attrName)) {
			return getX();
		}
		if (PROPERTY_Y.equalsIgnoreCase(attrName)) {
			return getY();
		}
		if (PROPERTY_GAME.equalsIgnoreCase(attrName)) {
			return getGame();
		}
		if (PROPERTY_NEXT.equalsIgnoreCase(attrName)) {
			return getNext();
		}
		if (PROPERTY_PREV.equalsIgnoreCase(attrName)) {
			return getPrev();
		}
		if (PROPERTY_DICE.equalsIgnoreCase(attrName)) {
			return getDice();
		}
		if (PROPERTY_START.equalsIgnoreCase(attrName)) {
			return getStart();
		}
		if (PROPERTY_BASE.equalsIgnoreCase(attrName)) {
			return getBase();
		}
		if (PROPERTY_LANDING.equalsIgnoreCase(attrName)) {
			return getLanding();
		}
		if (PROPERTY_PAWNS.equalsIgnoreCase(attrName)) {
			return getPawns();
		}
		return null;
	}

	//==========================================================================
	public boolean set(String attrName, Object value) {
		if (PROPERTY_COLOR.equalsIgnoreCase(attrName)) {
			setColor((String) value);
			return true;
		}
		if (PROPERTY_ENUMCOLOR.equalsIgnoreCase(attrName)) {
			setEnumColor((LudoColor) value);
			return true;
		}

		if (PROPERTY_NAME.equalsIgnoreCase(attrName)) {
			setName((String) value);
			return true;
		}

		if (PROPERTY_X.equalsIgnoreCase(attrName)) {
			setX(Integer.parseInt(value.toString()));
			return true;
		}
		if (PROPERTY_Y.equalsIgnoreCase(attrName)) {
			setY(Integer.parseInt(value.toString()));
			return true;
		}

		if (PROPERTY_GAME.equalsIgnoreCase(attrName)) {
			setGame((Ludo) value);
			return true;
		}

		if (PROPERTY_NEXT.equalsIgnoreCase(attrName)) {
			setNext((Player) value);
			return true;
		}

		if (PROPERTY_PREV.equalsIgnoreCase(attrName)) {
			setPrev((Player) value);
			return true;
		}

		if (PROPERTY_DICE.equalsIgnoreCase(attrName)) {
			setDice((Dice) value);
			return true;
		}

	  if (PROPERTY_START.equalsIgnoreCase(attrName))
	  {
		 setStart((Field) value);
		 return true;
	  }

	  if (PROPERTY_BASE.equalsIgnoreCase(attrName))
	  {
		 setBase((Field) value);
		 return true;
	  }

	  if (PROPERTY_LANDING.equalsIgnoreCase(attrName))
	  {
		 setLanding((Field) value);
		 return true;
	  }

	  if (PROPERTY_PAWNS.equalsIgnoreCase(attrName))
	  {
		 addToPawns((Pawn) value);
		 return true;
	  }

	  if ((PROPERTY_PAWNS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
	  {
		 removeFromPawns((Pawn) value);
		 return true;
	  }

	  return false;
	}

   //==========================================================================

   protected PropertyChangeSupport listeners = null;

   public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	   if(listeners != null) {
		   listeners.firePropertyChange(propertyName, oldValue, newValue);
	   }
   }

	public void addPropertyChangeListener(PropertyChangeListener listener) {
	   if(listeners == null) {
		   listeners = new PropertyChangeSupport(this);
	   }
	   listeners.addPropertyChangeListener(listener);
	}
	//==========================================================================
	public void removeYou() {
		setGame(null);
		setNext(null);
		setPrev(null);
		setDice(null);
		setStart(null);
		setBase(null);
		setLanding(null);
		removeAllFromPawns();
		firePropertyChange("REMOVE_YOU", this, null);
	}
	//==========================================================================
	public static final String PROPERTY_COLOR = "color";
	private String color;

	public String getColor() {
		return this.color;
	}

   public void setColor(String value) {
		if ( ! StrUtil.stringEquals(this.color, value)) {
			String oldValue = this.color;
			this.color = value;
			firePropertyChange(PROPERTY_COLOR, oldValue, value);
		}
	}

	public Player withColor(String value) {
		setColor(value);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(" ").append(this.getColor());
		result.append(" ").append(this.getName());
		result.append(" ").append(this.getX());
		result.append(" ").append(this.getY());
		return result.substring(1);
	}
	//==========================================================================
	public static final String PROPERTY_ENUMCOLOR = "enumColor";
	private LudoColor enumColor;

	public LudoColor getEnumColor() {
		return this.enumColor;
	}

	public void setEnumColor(LudoColor value) {
		if (this.enumColor != value) {
			LudoColor oldValue = this.enumColor;
			this.enumColor = value;
			firePropertyChange(PROPERTY_ENUMCOLOR, oldValue, value);
		}
	}

	public Player withEnumColor(LudoColor value) {
		setEnumColor(value);
		return this;
	}

	//==========================================================================
	public static final String PROPERTY_NAME = "name";
	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		if ( ! StrUtil.stringEquals(this.name, value)) {
			String oldValue = this.name;
			this.name = value;
			firePropertyChange(PROPERTY_NAME, oldValue, value);
		}
	}

	public Player withName(String value) {
		setName(value);
		return this;
	}

	//==========================================================================
	public static final String PROPERTY_X = "x";
	private int x;

	public int getX() {
		return this.x;
	}

	public void setX(int value) {
		if (this.x != value) {
			int oldValue = this.x;
			this.x = value;
			firePropertyChange(PROPERTY_X, oldValue, value);
		}
	}

	public Player withX(int value) {
		setX(value);
		return this;
	}

	//==========================================================================
	public static final String PROPERTY_Y = "y";
	private int y;

	public int getY() {
		return this.y;
	}

	public void setY(int value) {
		if (this.y != value) {
			int oldValue = this.y;
			this.y = value;
			firePropertyChange(PROPERTY_Y, oldValue, value);
		}
	}

	public Player withY(int value) {
		setY(value);
		return this;
	}

	/********************************************************************
	* <pre>
	*			  many					   one
	* Player ----------------------------------- Ludo
	*			  players				   game
	* </pre>
	*/
	public static final String PROPERTY_GAME = "game";
	private Ludo game = null;

	public Ludo getGame() {
		return this.game;
	}

	public boolean setGame(Ludo value) {
		boolean changed = false;
		if (this.game != value) {
			Ludo oldValue = this.game;
			if (this.game != null) {
				this.game = null;
				oldValue.withoutPlayers(this);
			}
			this.game = value;
			if (value != null) {
				value.withPlayers(this);
			}
			firePropertyChange(PROPERTY_GAME, oldValue, value);
			changed = true;
		}
		return changed;
	}

	public Player withGame(Ludo value) {
		setGame(value);
		return this;
	}

	public Ludo createGame() {
		Ludo value = new Ludo();
		withGame(value);
		return value;
	}

	/********************************************************************
	* <pre>
	*			  one					   one
	* Player ----------------------------------- Player
	*			  prev				   next
	* </pre>
	*/
	public static final String PROPERTY_NEXT = "next";
	private Player next = null;

	public Player getNext() {
		return this.next;
	}

	public boolean setNext(Player value) {
		boolean changed = false;
		if (this.next != value) {
			Player oldValue = this.next;
			if (this.next != null) {
				this.next = null;
				oldValue.setPrev(null);
			}
			this.next = value;
			if (value != null) {
				value.withPrev(this);
			}
			firePropertyChange(PROPERTY_NEXT, oldValue, value);
			changed = true;
		}
		return changed;
	}

	public Player withNext(Player value) {
		setNext(value);
		return this;
	}

	public Player createNext() {
		Player value = new Player();
		withNext(value);
		return value;
	}

	/********************************************************************
	* <pre>
	*			  one					   one
	* Player ----------------------------------- Player
	*			  next				   prev
	* </pre>
	*/
	public static final String PROPERTY_PREV = "prev";
	private Player prev = null;

	public Player getPrev() {
		return this.prev;
	}

	public boolean setPrev(Player value) {
		boolean changed = false;
		if (this.prev != value) {
			Player oldValue = this.prev;
			if (this.prev != null) {
				this.prev = null;
				oldValue.setNext(null);
			}
			this.prev = value;
			if (value != null) {
				value.withNext(this);
			}
			firePropertyChange(PROPERTY_PREV, oldValue, value);
			changed = true;
		}
		return changed;
	}

	public Player withPrev(Player value) {
		setPrev(value);
		return this;
	}

	public Player createPrev() {
		Player value = new Player();
		withPrev(value);
		return value;
	}

	/********************************************************************
	* <pre>
	*			  one					   one
	* Player ----------------------------------- Dice
	*			  player				   dice
	* </pre>
	*/
	public static final String PROPERTY_DICE = "dice";
	private Dice dice = null;

	public Dice getDice() {
		return this.dice;
	}

	public boolean setDice(Dice value) {
		boolean changed = false;
		if (this.dice != value) {
			Dice oldValue = this.dice;
			if (this.dice != null) {
				this.dice = null;
				oldValue.setPlayer(null);
			}
			this.dice = value;
			if (value != null) {
				value.withPlayer(this);
			}
			firePropertyChange(PROPERTY_DICE, oldValue, value);
			changed = true;
		}
		return changed;
	}

	public Player withDice(Dice value) {
		setDice(value);
		return this;
	}

	public Dice createDice() {
		Dice value = new Dice();
		withDice(value);
		return value;
	}

	/********************************************************************
	* <pre>
	*			  one					   one
	* Player ----------------------------------- Field
	*			  starter				   start
	* </pre>
	*/
	public static final String PROPERTY_START = "start";
	private Field start = null;

	public Field getStart() {
		return this.start;
	}

	public boolean setStart(Field value) {
		boolean changed = false;
		if (this.start != value) {
			Field oldValue = this.start;
			if (this.start != null) {
				this.start = null;
				oldValue.setStarter(null);
			}
			this.start = value;
			if (value != null) {
				value.setStarter(this);
			}
			firePropertyChange(PROPERTY_START, oldValue, value);
			changed = true;
		}
		return changed;
	}

	public Player withStart(Field value) {
		setStart(value);
		return this;
	}

	public Field createStart() {
		Field value = new Field();
		withStart(value);
		return value;
	}

	/********************************************************************
	* <pre>
	*			  one					   one
	* Player ----------------------------------- Field
	*			  baseowner				   base
	* </pre>
	*/
	public static final String PROPERTY_BASE = "base";
	private Field base = null;
	public Field getBase() {
		return this.base;
	}

	public boolean setBase(Field value) {
		boolean changed = false;
		if (this.base != value) {
			Field oldValue = this.base;
			if (this.base != null) {
				this.base = null;
				oldValue.setBaseowner(null);
			}
			this.base = value;
			if (value != null) {
				value.withBaseowner(this);
			}
			firePropertyChange(PROPERTY_BASE, oldValue, value);
			changed = true;
		}
		return changed;
	}

	public Player withBase(Field value) {
		setBase(value);
		return this;
	}

	public Field createBase() {
		Field value = new Field();
		withBase(value);
		return value;
	}

	/********************************************************************
	* <pre>
	*			  one					   one
	* Player ----------------------------------- Field
	*			  lander				   landing
	* </pre>
	*/
	public static final String PROPERTY_LANDING = "landing";
	private Field landing = null;
	public Field getLanding() {
		return this.landing;
	}

	public boolean setLanding(Field value) {
		boolean changed = false;
		if (this.landing != value) {
			Field oldValue = this.landing;
			if (this.landing != null) {
				this.landing = null;
				oldValue.setLander(null);
			}
			this.landing = value;
			if (value != null) {
				value.withLander(this);
			}
			firePropertyChange(PROPERTY_LANDING, oldValue, value);
			changed = true;
		}
		return changed;
	}

	public Player withLanding(Field value) {
		setLanding(value);
		return this;
	}

	public Field createLanding() {
		Field value = new Field();
		withLanding(value);
		return value;
	}

	/********************************************************************
	* <pre>
	*			  one					   many
	* Player ----------------------------------- Pawn
	*			  player				   pawns
	* </pre>
	*/

   public static final String PROPERTY_PAWNS = "pawns";

   private LinkedHashSet<Pawn> pawns = null;

   public LinkedHashSet<Pawn> getPawns()
   {
	  return this.pawns;
   }

   public boolean addToPawns(Pawn value)
   {
	  boolean changed = false;

	  if (value != null)
	  {
		 if (this.pawns == null)
		 {
			this.pawns = new LinkedHashSet<Pawn>();
		 }

		 changed = this.pawns.add (value);

		 if (changed)
		 {
			value.withPlayer(this);
			firePropertyChange(PROPERTY_PAWNS, null, value);
		 }
	  }

	  return changed;
   }

   public boolean removeFromPawns(Pawn value)
   {
	  boolean changed = false;

	  if ((this.pawns != null) && (value != null))
	  {
		 changed = this.pawns.remove (value);

		 if (changed)
		 {
			value.setPlayer(null);
			firePropertyChange(PROPERTY_PAWNS, value, null);
		 }
	  }

	  return changed;
   }

   public Player withPawns(Pawn value)
   {
	  addToPawns(value);
	  return this;
   }

   public Player withoutPawns(Pawn value)
   {
	  removeFromPawns(value);
	  return this;
   }

   public void removeAllFromPawns()
   {
	  LinkedHashSet<Pawn> tmpSet = new LinkedHashSet<Pawn>(this.getPawns());

	  for (Pawn value : tmpSet)
	  {
		 this.removeFromPawns(value);
	  }
   }

   public Pawn createPawns()
   {
	  Pawn value = new Pawn();
	  withPawns(value);
	  return value;
   }
}

