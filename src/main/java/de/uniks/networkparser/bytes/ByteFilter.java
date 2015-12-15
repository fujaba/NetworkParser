package de.uniks.networkparser.bytes;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;

public class ByteFilter extends Filter {
	private boolean isLenCheck;

	public boolean isLenCheck() {
		return isLenCheck;
	}

	public ByteFilter withLenCheck(boolean value) {
		this.isLenCheck = value;
		return this;
	}

	@Override
	public ByteFilter newInstance(Filter referenceFilter) {
		if(referenceFilter == null) {
			referenceFilter = new ByteFilter();
		}
		ByteFilter filter = (ByteFilter) super.newInstance(referenceFilter);
		filter.withLenCheck(this.isLenCheck);
		return filter;
	}

	public String getCharset() {
		return "UTF-8";
	}

	public int getIndexOfClazz(String clazzName) {
		if(visitedObjects == null) {
			return -1;
		}
		int pos = 0;
		for (Object item : visitedObjects) {
			if (clazzName.equalsIgnoreCase(item.getClass().getName())) {
				return pos;
			}
			pos++;
		}
		return -1;
	}

	public String getClazz(int pos) {
		if(visitedObjects == null) {
			return null;
		}
		Object item = visitedObjects.get(pos);
		if (item instanceof String) {
			return "" + item;
		}
		return null;
	}

	public String getLastClazz() {
		if(visitedObjects == null) {
			return null;
		}
		if (visitedObjects.size() > 0) {
			return visitedObjects.get(visitedObjects.size() - 1).getClass()
					.getName();
		}
		return null;
	}
	
	public ByteFilter withMap(IdMap map) {
		super.withMap(map);
		return this;
	}
}
