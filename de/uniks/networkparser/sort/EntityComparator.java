package de.uniks.networkparser.sort;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
import java.util.Comparator;

import de.uniks.networkparser.EntityValueFactory;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.table.TableList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class EntityComparator implements Comparator<Object> {
	public static String IDMAP = "%idmap%";
	public static String HASHCODE = "%hashcode%";
	public static String LIST = "%list%";

	private SortingDirection direction = SortingDirection.ASC;
	private String column = IDMAP;
	private IdMap map;
	private EntityValueFactory cellCreator = new EntityValueFactory();
	private TableList owner;
	protected SendableEntityCreator creator;
	
	public EntityComparator withTableList(TableList owner){
		this.owner = owner;
		this.column = LIST;
		return this;
	}

	@Override
	public int compare(Object o1, Object o2) {
		return direction.getDirection() * compareValue(o2, o1);
	}

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
		if (v1 == null && v2 == null) {
			return checkIntern(o1, o2);
		}

		if (v1 instanceof String) {
			String valueA = (String) v1;
			String valueB = (String) v2;
			if (valueA != null) {
				if (valueB != null) {
					int value = valueB.compareTo(valueA);

					if (value < 1) {
						return -1;
					}
				}
				return 1;
			}
		} else if (v1 instanceof Integer) {
			Integer valueA = (Integer) v1;
			Integer valueB = (Integer) v2;
			if (valueA != null) {
				if (valueB != null) {
					int value = valueB.compareTo(valueA);

					if (value < 1) {
						return -1;
					}
				}
				return 1;
			}
		} else if (v1 instanceof Long) {
			Long valueA = (Long) v1;
			Long valueB = (Long) v2;
			if (valueA != null) {
				if (valueB != null) {
					int value = valueB.compareTo(valueA);

					if (value < 1) {
						return -1;
					}
				}
				return 1;
			}
		} else if (v1 instanceof Boolean) {
			Boolean valueA = (Boolean) v1;
			Boolean valueB = (Boolean) cellCreator.getCellValue(o2, creator, column);
			if (valueA != null) {
				if (valueB != null) {
					int value = valueB.compareTo(valueA);

					if (value < 1) {
						return -1;
					}
				}
				return 1;
			}
		}
		return -1;
	}

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
		
		if(LIST.equalsIgnoreCase(column) && owner != null) {
			return owner.indexOf(o1)-owner.indexOf(o2);
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

	public SortingDirection getDirection() {
		return direction;
	}

	public EntityComparator withDirection(SortingDirection direction) {
		this.direction = direction;
		return this;
	}

	public String getColumn() {
		return column;
	}

	public EntityComparator withColumn(String column) {
		this.column = column;
		return this;
	}

	public EntityComparator withMap(IdMap value) {
		this.map = value;
		return this;
	}

	public IdMap getMap() {
		return map;
	}

	public EntityValueFactory getCellCreator() {
		return cellCreator;
	}

	public EntityComparator withCellCreator(EntityValueFactory cellCreator) {
		this.cellCreator = cellCreator;
		return this;
	}

	public SortingDirection changeDirection() {
		this.direction = direction.changeDirection();
		return direction;
	}
}
