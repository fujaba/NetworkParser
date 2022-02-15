package de.uniks.networkparser.ext.petaf.filter;

import de.uniks.networkparser.SendableItem;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleList;

/**
 * Filter with Tree.
 *
 * @author Stefan Lindel
 */
public class TreeFilter extends SendableItem {
	
	/** The Constant PROPERTY_CHILDREN. */
	public static final String PROPERTY_CHILDREN = "children";
	
	/** The Constant PROPERTY_PARENT. */
	public static final String PROPERTY_PARENT = "parent";
	private SimpleList<TreeFilter> children;
	private TreeFilter parent;
	private Condition<?> filter;

	/**
	 * With children.
	 *
	 * @param children the children
	 * @return the tree filter
	 */
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

	/**
	 * Without children.
	 *
	 * @param children the children
	 * @return the tree filter
	 */
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

	/**
	 * With parent.
	 *
	 * @param parent the parent
	 * @return the tree filter
	 */
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

	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	public Condition<?> getFilter() {
		return filter;
	}

	/**
	 * With filter.
	 *
	 * @param filter the filter
	 * @return the tree filter
	 */
	public TreeFilter withFilter(Condition<?> filter) {
		this.filter = filter;
		return this;
	}
}
