package de.uniks.networkparser.list;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.Comparator;

import de.uniks.networkparser.EntityValueFactory;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
/**
 * Compare Value for GUI.
 *
 * @author Stefan Lindel
 *
 * @param <V>
 *			Generic Parameter for all Types
 */
public class EntityComparator<V> implements Comparator<V> {
	/** Constant of IDMAP. */
	public static final String IDMAP = "%idmap%";
	/** Constant of HASHCODE. */
	public static final String HASHCODE = "%hashcode%";
	/** Constant of HASHCODE. */
	public static final String VALUES = "%values%";
	/** Constant of LIST. */
	public static final String LIST = "%list%";

	/** Variable of Direction. */
	private SortingDirection direction = SortingDirection.ASC;
	/** Variable of Column. */
	private String column = IDMAP;
	/** Variable of IdMap. */
	private IdMap map;
	/** Variable of Factory. */
	private EntityValueFactory cellCreator = new EntityValueFactory();
	/** Variable of TableList. */
	private SimpleList<Object> owner;
	/** Variable of creator. */
	protected SendableEntityCreator creator;

	/**
	 * Set a GUI TableList.
	 *
	 * @param value		The new TbaleList
	 * @return 			EntityComparator Instance
	 */
	public EntityComparator<V> withTableList(SimpleList<Object> value) {
		this.owner = value;
		this.column = LIST;
		return this;
	}

	@Override
	public int compare(Object o1, Object o2) {
		return direction.getDirection() * compareValue(o2, o1);
	}

	/**
	 * Compare Values
	 *
	 * @param o1	object for compare
	 * @param o2	object for compare
	 * @return 		compare result
	 */
	public int compareValue(Object o1, Object o2) {
		if(VALUES.equals(column)) {
			return checkValues(o1, o2);
		}
		if (map != null) {
			creator = map.getCreatorClass(o1);
			SendableEntityCreator c2 = map.getCreatorClass(o2);
			if (creator != c2) {
				creator = null;
			}
		}
		if (creator == null) {
			return checkIntern(o1, o2);
		}

		Object v1 = cellCreator.getCellValue(o1, creator, column);
		Object v2 = cellCreator.getCellValue(o2, creator, column);
		if (v1 == null) {
			if (v2 == null) {
				return checkIntern(o1, o2);
			}
			return checkValues(v1, v2);
		}
		return checkValues(v2, v1) * -1;
	}

	/**
	 * Compare values of v1 and v2.
	 *
	 * @param v1	value for compare
	 * @param v2	value for compare
	 * @return 		compare Result
	 */
	@SuppressWarnings("unchecked")
	private int checkValues(Object v1, Object v2) {
		if (v1 instanceof String) {
			String valueA = (String) v1;
			if (v2 != null) {
				String valueB = (String) v2;
				return valueB.compareTo(valueA);
			}
			return 1;
		}
		if (v1 instanceof Integer) {
			Integer valueA = (Integer) v1;
			if (v2 != null) {
				Integer valueB = (Integer) v2;
				int value = valueB.compareTo(valueA);
				if (value < 1) {
					return -1;
				}
			}
			return 1;
		}
		if (v1 instanceof Double) {
			Double valueA = (Double) v1;
			if (v2 != null) {
				Double valueB = (Double) v2;
				int value = valueB.compareTo(valueA);
				if (value < 1) {
					return -1;
				}
			}
			return 1;
		}
		if (v1 instanceof Long) {
			Long valueA = (Long) v1;
			if (v2 != null) {
				Long valueB = (Long) v2;
				int value = valueB.compareTo(valueA);

				if (value < 1) {
					return -1;
				}
			}
			return 1;
		}
		if (v1 instanceof Boolean) {
			Boolean valueA = (Boolean) v1;
			Boolean valueB = (Boolean) v2;
			if (valueB != null) {
				int value = valueB.compareTo(valueA);
				if (value < 1) {
					return -1;
				}
			}
			return 1;
		}
		if(v1 instanceof Comparable<?>) {
			if(v2 instanceof Comparable<?>) {
				return ((Comparable<Object>)v2).compareTo(v1);
			}
		}
		return 1;
	}

	/**
	 * Compare o1 and o2.
	 *
	 * @param o1	object for compare
	 * @param o2	object for compare
	 * @return 		Int value < 0 o1 is smaller 0 o1 == o2 o1 is the same 1 o2 is bigger
	 */
	private int checkIntern(Object o1, Object o2) {
		// SAME OBJECT MUST BE 0
		if (o2 == null) {
			if (o1 == null) {
				return 0;
			}
			return -1;
		} else if (o1 == null) {
			return -1;
		}

		if (o1.equals(o2)) {
			return 0;
		}

		if (LIST.equalsIgnoreCase(column) && owner != null) {
			return owner.indexOf(o1) - owner.indexOf(o2);
		}

		// KEY IN IDMAP
		if (IDMAP.equalsIgnoreCase(column) && map != null) {
			String v1 = map.getId(o1, false);
			String v2 = map.getId(o2, false);
			return v1.compareTo(v2);
		}
		// HASHCODE
		if (o1.hashCode() < o2.hashCode()) {
			return 1;
		}
		return -1;
	}

	/** @return The Sortdirection */
	public SortingDirection getDirection() {
		return direction;
	}

	/**
	 * Set a new Direction.
	 *
	 * @param value		Direction for set
	 * @return 			EntityComparator Instance
	 */
	public EntityComparator<V> withDirection(SortingDirection value) {
		this.direction = value;
		return this;
	}

	/** @return The Current Column */
	public String getColumn() {
		return column;
	}

	/**
	 * @param value		The new Column for checking
	 * @return 			EntityComparator Instance
	 */
	public EntityComparator<V> withColumn(String value) {
		this.column = value;
		return this;
	}

	/**
	 * Set a new IdMap for comunicate between GUI and Model.
	 *
	 * @param value		The IdMap
	 * @return 			EntityComparator Instance
	 */
	public EntityComparator<V> withMap(IdMap value) {
		this.map = value;
		return this;
	}

	/**
	 * @return The Current IdMap.
	 */
	public IdMap getMap() {
		return map;
	}

	/**
	 * @return Return the Current Cell Creator.
	 */
	public EntityValueFactory getCellCreator() {
		return cellCreator;
	}

	/**
	 * The new Creator for Cells.
	 *
	 * @param value		The cellCreator
	 * @return 			EntityComparator Instance
	 */
	public EntityComparator<V> withCellCreator(EntityValueFactory value) {
		this.cellCreator = value;
		return this;
	}

	/** @return Change SortDiraction */
	public SortingDirection changeDirection() {
		this.direction = direction.changeDirection();
		return direction;
	}

	public static EntityComparator<Object> createComparator() {
		EntityComparator<Object> cpr = new EntityComparator<Object>().withColumn(EntityComparator.VALUES).withDirection(SortingDirection.ASC);
		return cpr;
	}
}
