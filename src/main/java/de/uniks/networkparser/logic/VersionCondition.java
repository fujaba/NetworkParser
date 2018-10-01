package de.uniks.networkparser.logic;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class VersionCondition implements ObjectCondition, Comparable<VersionCondition> {
//	private String version;
	private int mayor;
	private int minor;
	private int revision;
	private ObjectCondition children;
	
	public VersionCondition withVersion(String version) {
		if(version != null) {
			String[] split = version.split("\\.");
			this.mayor = 0;
			this.minor = 0;
			this.revision = 0;
			if(split.length>2) {
				try {
					this.revision = Integer.valueOf(split[2]);
				}catch (Exception e) {
				}
			}
			if(split.length>1) {
				try {
					this.minor= Integer.valueOf(split[1]);
				}catch (Exception e) {
				}
			}
			if(split.length>0) {
				try {
					if(split[0].startsWith("^")) {
						// COMPAREGRATER
						this.children = new CompareTo().withCompare(CompareTo.GREATER);
						this.mayor= Integer.valueOf(split[0].substring(1));
					}else {
						this.mayor= Integer.valueOf(split[0]);
					}
				}catch (Exception e) {
				}
			}
		}
		return this;
	}
	
	@Override
	public String toString() {
		return this.mayor+"."+this.minor+"."+this.revision;
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		Object newValue = evt.getNewValue();
		if(newValue instanceof VersionCondition) {
			if(children != null) {
				if(children instanceof CompareTo) {
//					CompareTo.
				}
				return children.update(value);
			}
		}
		return true;
	}

	@Override
	public int compareTo(VersionCondition o) {
		if(this.mayor<o.mayor) {
			return -1;
		} else if(this.mayor>o.mayor) {
			return 1;
		}
		if(this.minor<o.minor) {
			return -1;
		} else if(this.minor>o.minor) {
			return 1;
		}
		if(this.revision<o.revision) {
			return -1;
		} else if(this.revision>o.revision) {
			return 1;
		}
		return 0;
	}

}
