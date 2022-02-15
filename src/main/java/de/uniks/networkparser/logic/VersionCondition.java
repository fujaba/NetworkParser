package de.uniks.networkparser.logic;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;

/**
 * The Class VersionCondition.
 *
 * @author Stefan
 */
public class VersionCondition implements ObjectCondition, Comparable<VersionCondition> {
	private int mayor;
	private int minor;
	private int revision;
	private ObjectCondition children;

	/**
	 * With version.
	 *
	 * @param version the version
	 * @return the version condition
	 */
	public VersionCondition withVersion(String version) {
		if (version != null) {
			String[] split = version.split("\\.");
			this.mayor = 0;
			this.minor = 0;
			this.revision = 0;
			if (split.length > 2) {
				try {
					this.revision = Integer.parseInt(split[2]);
				} catch (Exception e) {
				}
			}
			if (split.length > 1) {
				try {
					this.minor = Integer.parseInt(split[1]);
				} catch (Exception e) { //Empty
				}
			}
			if (split.length > 0) {
				try {
					if (split[0].startsWith("^")) {
						/* COMPAREGRATER */
						this.children = new CompareTo().withCompare(CompareTo.GREATER);
						this.mayor = Integer.parseInt(split[0].substring(1));
					} else {
						this.mayor = Integer.parseInt(split[0]);
					}
				} catch (Exception e) { //Empty
				}
			}
		}
		return this;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return this.mayor + "." + this.minor + "." + this.revision;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (!(value instanceof SimpleEvent)) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		Object newValue = evt.getNewValue();
		if (newValue instanceof VersionCondition && children != null) {
			return children.update(value);
		}
		return true;
	}

	/**
	 * Compare to.
	 *
	 * @param o the o
	 * @return the int
	 */
	@Override
	public int compareTo(VersionCondition o) {
		if (o == null) {
			return -1;
		}
		if (this.mayor < o.mayor) {
			return -1;
		} else if (this.mayor > o.mayor) {
			return 1;
		}
		if (this.minor < o.minor) {
			return -1;
		} else if (this.minor > o.minor) {
			return 1;
		}
		if (this.revision < o.revision) {
			return -1;
		} else if (this.revision > o.revision) {
			return 1;
		}
		return 0;
	}
}
