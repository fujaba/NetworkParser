package de.uniks.networkparser.bytes;

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
import de.uniks.networkparser.Filter;

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
	public Filter clone() {
		return clone(new ByteFilter());
	}

	@Override
	protected Filter clone(Filter newInstance) {
		ByteFilter result = (ByteFilter) super.clone(newInstance);
		return result.withLenCheck(this.isLenCheck);
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
}
