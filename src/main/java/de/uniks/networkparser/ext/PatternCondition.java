package de.uniks.networkparser.ext;

import java.util.Set;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.graph.Pattern;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleIterator;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.ExcelCell;
import de.uniks.networkparser.parser.ExcelRow;
import de.uniks.networkparser.parser.ExcelSheet;

public class PatternCondition implements ObjectCondition {
	public static final String CREATEPATTERN = "createpattern";
	private String link;
	private Object value;
	private boolean duplicate;
	private Set<?> root;
	private ExcelSheet excelSheet;

	public PatternCondition withLinkName(String value) {
		this.link = value;
		return this;
	}

	public PatternCondition withValue(Object value) {
		this.value = value;
		return this;
	}

	public PatternCondition withDuplicate(boolean value) {
		this.duplicate = value;
		this.excelSheet = new ExcelSheet();
		return this;
	}

	public boolean getDuplicate() {
		return duplicate;
	}

	public SimpleSet<Object> getNewList() {
		return new SimpleSet<Object>();
	}

	@SuppressWarnings("unchecked")
	public PatternCondition withRoot(Object... values) {
		if (values == null) {
			this.root = null;
			return this;
		}
		if (values.length == 1) {
			/* May be a Set */
			if (values[0] instanceof Set<?> && values[0] != null) {
				Set<Object> newSet = (Set<Object>) ReflectionLoader.newInstance(values[0].getClass());
				ReflectionLoader.call(newSet, "withListener", ObjectCondition.class, this);
				this.root = newSet;

				SimpleEvent evt = SimpleEvent.create(this, 0, newSet, newSet, values[0], null);
				update(evt);
			} else {
				SimpleSet<Object> newSet = getNewList();
				newSet.withListener(this);
				this.root = newSet;
				SimpleEvent evt = SimpleEvent.create(this, 0, newSet, newSet, values[0], null);
				update(evt);
			}
		} else {
			SimpleSet<Object> newSet = getNewList();
			newSet.withListener(this);
			this.root = newSet;
			update(new SimpleEvent(root, CREATEPATTERN, null, root));
			SimpleEvent evt = SimpleEvent.create(this, 0, newSet, newSet, values[0], null);
			update(evt);
		}
		if (root != null) {
			if (root instanceof SimpleSet<?>) {
				((SimpleSet<?>) this.root).withAllowDuplicate(this.duplicate);
			}
		}
		return this;
	}

	public Set<?> getRoot() {
		return root;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean update(Object value) {
		if (value instanceof SimpleEvent) {
			SimpleEvent event = (SimpleEvent) value;
			if (CREATEPATTERN.equals(event.getPropertyName())) {
				int index = event.getDepth();
				AbstractList<?> newCollection = (AbstractList<?>) event.getBeforeElement();
				Object child = event.getNewValue();
				Object[] filter = null;
				if (event.getModelValue() != null) {
					Class<?> filterClass = event.getModelValue().getClass();
					if (filterClass.equals(int[].class)) {
						int[] rowFilter = (int[]) event.getModelValue();
						filter = new Object[rowFilter.length];
						for (int i = 0; i < rowFilter.length; i++) {
							filter[i] = Integer.valueOf(rowFilter[i]);
						}
					} else if (filterClass.equals(double[].class)) {
						double[] rowFilter = (double[]) event.getModelValue();
						filter = new Object[rowFilter.length];
						for (int i = 0; i < rowFilter.length; i++) {
							filter[i] = Double.valueOf(rowFilter[i]);
						}
					} else {
						filter = (Object[]) event.getModelValue();
					}
				}
				/* For First Element set Dupplicate */
				if (index == 0) {
					newCollection.withAllowDuplicate(this.duplicate);
					this.excelSheet.createRow(newCollection);
				}
				Set<Object> childCollection;
				if (child instanceof Set<?>) {
					childCollection = (Set<Object>) child;
				} else {
					SimpleSet<Object> items = new SimpleSet<Object>();
					items.add(child);
					childCollection = items;
				}

				SimpleSet<String> filters = null;
				if (filter != null && filter.length > 0) {
					filters = new SimpleSet<String>();
					filters.with(filter);
				}

				if (childCollection.isEmpty()) {
					/* EMPTY VALUE CLEAR ITEMS FROM LIST Index is the Index of LastRow */
					for (int i = this.excelSheet.size() - 2; i >= 0; i--) {
						this.excelSheet.get(i).remove(i);
					}
				} else {
					ExcelRow last = this.excelSheet.getLast();
					/* SO SET or COPY COLUMN */
					boolean first = true;
					Object[] children = childCollection.toArray(new Object[childCollection.size()]);
					for (Object item : children) {
						if (filters == null || filters.contains(item)) {
							if (first) {
								/* ONLY ST AS NEW EXCELCELL */
								first = false;
							} else {
								for (int i = this.excelSheet.size() - 2; i >= 0; i--) {
									this.excelSheet.get(i).copy(index);
								}
							}
							ExcelCell cell = new ExcelCell().withContent(item);
							newCollection.add(item);
							last.add(cell);
						}
					}
				}
				return true;
			}
		}
		if (value instanceof Pattern == false) {
			return false;
		}
		Pattern pattern = (Pattern) value;
		IdMap map = pattern.getMap();
		if (map == null) {
			return false;
		}
		if (pattern.getCandidates() == null && pattern.getParent() != null) {
			Object match2 = pattern.getParent().getMatch();
			if (match2 != null) {
				SendableEntityCreator creator = map.getCreatorClass(match2);
				if (creator != null) {
					Object newValue = creator.getValue(match2, getLinkName());
					pattern.withCandidates(newValue);
				}
			}
		}

		SimpleIterator<Object> i = pattern.getIterator();
		if (i == null) {
			return false;
		}
		if (pattern.getMatch() != null && pattern.getMatch() != i.current()) {
			if (this.value == null || this.value.equals(pattern.getMatch())) {
				return true;
			}
		}
		while (i.hasNext()) {
			Object candidate = i.next();
			if (candidate == null) {
				return false;
			}
			if (this.value == null || this.value.equals(candidate)) {
				return true;
			}
		}

		return false;
	}

	public static final PatternCondition create(String linkName) {
		PatternCondition pattern = new PatternCondition();
		pattern.withLinkName(linkName);
		return pattern;
	}

	public String getLinkName() {
		return link;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		if (this.excelSheet != null) {
			return excelSheet.toString();
		}
		return super.toString();
	}

	public static final PatternCondition createPatternPair(Object... root) {
		return new PatternCondition().withDuplicate(true).withRoot(root);
	}

	public static boolean setValue(SimpleSet<?> patternObejct, String property, Object value, Object condition) {
		return false;
	}
}
