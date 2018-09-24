package de.uniks.networkparser.ext;

import java.util.Collection;
import java.util.Set;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.graph.Pattern;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleIterator;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.ExcelCell;
import de.uniks.networkparser.parser.ExcelRow;
import de.uniks.networkparser.parser.ExcelSheet;

public class PatternCondition implements ObjectCondition{
	public static final String CREATE="create";
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
		if(values == null) {
			this.root = null;
			return this;
		}
		if(values.length ==1) {
			// May be a Set
			if(values[0] instanceof Set<?> && values[0] != null) {
				Set<Object> newSet = (Set<Object>) ReflectionLoader.newInstance(values[0].getClass());
				ReflectionLoader.call(newSet, "withListener", ObjectCondition.class, this);
				this.root = newSet;
				update(new SimpleEvent(root, CREATE, null, root));
				newSet.addAll((Collection<?>) values[0]);
			}else {
				SimpleSet<Object> newSet = getNewList();
				newSet.withListener(this);
				this.root = newSet;
				update(new SimpleEvent(root, CREATE, null, root));

				newSet.add(values[0]);
			}
		} else {
			SimpleSet<Object> newList = getNewList();
			newList.withListener(this);
			this.root = newList;
			update(new SimpleEvent(root, CREATE, null, root));

			for(Object item : values) {
				if(item != null) {
					newList.add(item);
				}
			}
		}
		if(root != null ) {
			if(root instanceof SimpleSet<?>) {
				((SimpleSet<?>)this.root).withAllowDuplicate(this.duplicate);
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
		if(value instanceof SimpleEvent) {
			SimpleEvent event = (SimpleEvent) value;
			if(CREATE.equals(event.getPropertyName())) {
				Object newValue = event.getNewValue();
				if(this.excelSheet != null) {
					this.excelSheet.createRow(newValue);
				}
				if(newValue instanceof SimpleSet<?>) {
					SimpleSet<?> set=(SimpleSet<?>) newValue;
					set.withAllowDuplicate(this.duplicate);
				}
				return true;
			}else if(SimpleSet.PROPERTY.equals(event.getPropertyName())) {
				// NOW ADD SOME ITEM
				Collection<Object> source = (Collection<Object>) event.getSource();
				ExcelRow row = this.excelSheet.getRow(source);
				if(row == null ) {
					System.out.println("ss");
				}
				this.excelSheet.toString();
				int index = this.excelSheet.getRowIndex(row);
				Object newValue = event.getNewValue();
				if(source.size() == 1) {
					// FIRST SIZE
					if(index>0) {
						row.setElementCount(this.excelSheet.get(index-1).size());
					}
					ExcelCell cell = new ExcelCell().withContent(newValue);
					row.add(cell);
//					// Duplicate Value
//					for(int i=0;i<row.getCount();i++) {
//						source.add(newValue);
//					}
				} else if(row.getCount() >0) {
					// FIRST ADD
					duplicateColumn(2, index);
					ExcelCell cell = new ExcelCell().withContent(newValue);
					row.add(cell);
					
					for(int r=1;r<index;r++) {
						ExcelRow masterRow = this.excelSheet.get(r);
						duplicateColumn(masterRow.getCount(), index);
					}
				}
				return true;
			}
		}
		if(value instanceof Pattern == false) {
			return false;
		}
		Pattern pattern = (Pattern) value;
		IdMap map = pattern.getMap();
		if(map == null) {
			return false;
		}
		if(pattern.getCandidates() == null && pattern.getParent()!= null) {
			Object match2 = pattern.getParent().getMatch();
			if(match2 != null) {
				SendableEntityCreator creator = map.getCreatorClass(match2);
				if(creator != null) {
					Object newValue = creator.getValue(match2, getLinkName());
					pattern.withCandidates(newValue);
				}
			}
		}

		SimpleIterator<Object> i = pattern.getIterator();
		if(i == null) {
			return false;
		}
		if(pattern.getMatch() != null && pattern.getMatch() != i.current() ) {
			if(this.value == null || this.value.equals(pattern.getMatch())) {
				return true;
			}
		}
		while(i.hasNext()) {
			Object candidate = i.next();
			if(candidate == null) {
				return false;
			}
			if(this.value == null || this.value.equals(candidate)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void duplicateColumn(int count, int max) {
		for(int i=1;i<count;i++) {
			for(int r=0;r<max;r++) {
				ExcelRow excelRow = this.excelSheet.get(r);
				ExcelCell firstCell = excelRow.get(0);
				ExcelCell cell = ExcelCell.create(firstCell.getContent());
				excelRow.add(cell);
			}
		}
		
		
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

	public static final PatternCondition createPatternPair(Object... root) {
		return new PatternCondition().withDuplicate(true).withRoot(root);
	}
}
