package de.uniks.networkparser.parser;

import java.util.Iterator;

import de.uniks.networkparser.list.AbstractList;
/*
NetworkParser
Copyright (c) 2011 - 2016, Stefan Lindel
All rights reserved.

Licensed under the EUPL, Version 1.1 or (as soon they
will be approved by the European Commission) subsequent
versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl5

Unless required by applicable law or agreed to in writing, software distributed under the Licence is
distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the Licence for the specific language governing permissions and limitations under the Licence.
*/
import de.uniks.networkparser.list.SimpleList;

public class ExcelRow implements Iterable<ExcelCell> {
	private SimpleList<ExcelCell> children;
	private Object parent;
	private String name;
	private int len;

	public ExcelRow withName(String name) {
		this.name = name;
		return this;
	}

	public String getName() {
		return name;
	}

	public ExcelRow withParent(Object parent) {
		this.parent = parent;
		return this;
	}

	public Object getParent() {
		return parent;
	}

	public int getRowPos() {
		if (this.size() > 0) {
			return first().getReferenz().y;
		}
		return -1;
	}

	private ExcelCell first() {
		if (children == null) {
			return null;
		}
		return children.first();
	}

	public ExcelCell getItem(int index) {
		for (int i = 0; i < this.size(); i++) {
			ExcelCell cell = this.get(i);
			if (cell != null && cell.getReferenz().x == index) {
				return cell;
			}
		}
		return new ExcelCell();
	}

	public ExcelCell get(int index) {
		return this.children.get(index);
	}

	public SimpleList<ExcelCell> getChildren() {
		return children;
	}

	public int size() {
		if (children == null) {
			return 0;
		}
		return children.size();
	}

	public boolean add(ExcelCell... values) {
		if (values == null) {
			return false;
		}
		if (children == null) {
			children = new SimpleList<ExcelCell>();
		}
		boolean result = true;
		for (ExcelCell item : values) {
			if (item == null) {
				continue;
			}
			Object content = item.getContent();
			if (content != null) {
				int temp = content.toString().length();
				if (temp > this.len) {
					this.len = temp;
				}
			}
			result = children.add(item) && result;
		}
		return true;
	}

	@Override
	public Iterator<ExcelCell> iterator() {
		if (children == null) {
			children = new SimpleList<ExcelCell>();
		}
		return children.iterator();
	}

	public ExcelRow with(ExcelCell... values) {
		add(values);
		return this;
	}

	public ExcelCell remove(int i) {
		ExcelCell result = this.children.remove(i);
		if (this.parent != null) {
			if (parent instanceof AbstractList<?>) {
				AbstractList<?> collection = (AbstractList<?>) parent;
				collection.remove(i);
			}
		}
		return result;
	}

	public ExcelCell copy(int pos) {
		Object content = this.get(pos).getContent();
		ExcelCell cell = new ExcelCell().withContent(content);
		this.children.add(pos + 1, cell);
		if (this.parent != null) {
			if (parent instanceof AbstractList<?>) {
				AbstractList<?> collection = (AbstractList<?>) parent;
				collection.add(pos, content);
			}
		}
		return cell;
	}

	public int getContentLength() {
		return len;
	}

}
