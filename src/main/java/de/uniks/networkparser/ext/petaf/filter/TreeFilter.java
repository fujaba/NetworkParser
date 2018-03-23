package de.uniks.networkparser.ext.petaf.filter;

/*
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
import de.uniks.networkparser.ext.petaf.SendableItem;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleList;

public class TreeFilter extends SendableItem {
	public static final String PROPERTY_CHILDREN = "children";
	public static final String PROPERTY_PARENT = "parent";
	private SimpleList<TreeFilter> children;
	private TreeFilter parent;
	private Condition<?> filter;

	public TreeFilter withChildren(TreeFilter... children) {
		if (children == null) {
			return this;
		}
		for (TreeFilter filter : children) {
			if (filter == null) {
				continue;
			}
			if (this.children == null) {
				this.children = new SimpleList<TreeFilter>();
			}
			if (this.children.add(filter)) {
				filter.withParent(this);
				firePropertyChange(PROPERTY_CHILDREN, null, filter);
			}
		}
		return this;
	}

	public TreeFilter withoutChildren(TreeFilter... children) {
		if (children == null || this.children == null) {
			return this;
		}
		for (TreeFilter filter : children) {
			if (this.children.remove(filter)) {
				filter.withParent(null);
				firePropertyChange(PROPERTY_CHILDREN, filter, null);
			}
		}
		return this;
	}

	public TreeFilter withParent(TreeFilter parent) {
		if (parent == this.parent) {
			return this;
		}
		TreeFilter oldValue = this.parent;
		if (oldValue != null) {
			this.parent = null;
			oldValue.withoutChildren(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.withChildren(this);
			firePropertyChange(PROPERTY_PARENT, oldValue, parent);
		}
		return this;
	}

	public Condition<?> getFilter() {
		return filter;
	}

	public TreeFilter withFilter(Condition<?> filter) {
		this.filter = filter;
		return this;
	}
}
