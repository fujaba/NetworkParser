package de.uniks.networkparser.sort;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
 */
import java.util.Comparator;
import de.uniks.networkparser.EntityValueFactory;
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.gui.table.TableList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * Compare Value for GUI.
 *
 * @author Stefan Lindel
 *
 * @param <V>
 *            Generic Parameter for all Types
 */
public class EntityComparator<V> implements Comparator<V> {
	/** Constant of IDMAP. */
	public static final String IDMAP = "%idmap%";
	/** Constant of HASHCODE. */
	public static final String HASHCODE = "%hashcode%";
	/** Constant of LIST. */
	public static final String LIST = "%list%";

	/** Variable of Direction. */
	private SortingDirection direction = SortingDirection.ASC;
	/** Variable of Column. */
	private String column = IDMAP;
	/** Variable of IdMap. */
	private IdMapEncoder map;
	/** Variable of Factory. */
	private EntityValueFactory cellCreator = new EntityValueFactory();
	/** Variable of TableList. */
	private TableList owner;
	/** Variable of creator. */
	private SendableEntityCreator creator;

	/**
	 * Set a GUI TableList.
	 *
	 * @param value
	 *            The new TbaleList
	 * @return EntityComparator Instance
	 */
	public EntityComparator<V> withTableList(TableList value) {
		this.owner = value;
		this.column = LIST;
		return this;
	}

	@Override
	public int compare(Object o1, Object o2) {
		return direction.getDirection() * compareValue(o2, o1);
	}

	/**
	 * @param o1
	 *            object for compare
	 * @param o2
	 *            object for compare
	 * @return compare result
	 */
	public int compareValue(Object o1, Object o2) {
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
	 * @param v1
	 *            value for compare
	 * @param v2
	 *            value for compare
	 * @return compare Result
	 */
	private int checkValues(Object v1, Object v2) {
		if (v1 instanceof String) {
			String valueA = (String) v1;
			if (v2 != null) {
				String valueB = (String) v2;
				int value = valueB.compareTo(valueA);
				if (value < 1) {
					return -1;
				}
			}
		} else if (v1 instanceof Integer) {
			Integer valueA = (Integer) v1;
			if (v2 != null) {
				Integer valueB = (Integer) v2;
				int value = valueB.compareTo(valueA);

				if (value < 1) {
					return -1;
				}
			}
			return 1;
		} else if (v1 instanceof Long) {
			Long valueA = (Long) v1;
			if (v2 != null) {
				Long valueB = (Long) v2;
				int value = valueB.compareTo(valueA);

				if (value < 1) {
					return -1;
				}
			}
		} else if (v1 instanceof Boolean) {
			Boolean valueA = (Boolean) v1;
			Boolean valueB = (Boolean) v2;
			if (valueB != null) {
				int value = valueB.compareTo(valueA);
				if (value < 1) {
					return -1;
				}
			}
		}
		return 1;
	}

	/**
	 * Compare o1 and o2.
	 *
	 * @param o1
	 *            object for compare
	 * @param o2
	 *            object for compare
	 * @return Int value < 0 o1 is smaller 0 o1 == o2 o1 is the same 1 o2 is
	 *         bigger
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
			String v1 = map.getId(o1);
			String v2 = map.getId(o2);
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
	 * @param value
	 *            Direction for set
	 * @return EntityComparator Instance
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
	 * @param value
	 *            The new Column for checking
	 * @return EntityComparator Instance
	 */
	public EntityComparator<V> withColumn(String value) {
		this.column = value;
		return this;
	}

	/**
	 * Set a new IdMap for comunicate between GUI and Model.
	 *
	 * @param value
	 *            The IdMap
	 * @return EntityComparator Instance
	 */
	public EntityComparator<V> withMap(IdMapEncoder value) {
		this.map = value;
		return this;
	}

	/**
	 * @return The Current IdMap.
	 */
	public IdMapEncoder getMap() {
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
	 * @param value
	 *            The cellCreator
	 * @return EntityComparator Instance
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
}
