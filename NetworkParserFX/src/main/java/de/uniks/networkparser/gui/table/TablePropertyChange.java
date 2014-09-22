package de.uniks.networkparser.gui.table;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;

public class TablePropertyChange implements Runnable{
	private PropertyChangeEvent event;
	private TableComponent table;
	private Object source;
	private String property;
	private TableList sourceList;

	public TablePropertyChange(TableComponent table, PropertyChangeEvent event, Object source, String property, TableList sourceList){
		this.table = table;
		this.event = event;
		this.source = source;
		this.property = property;
		this.sourceList = sourceList;
	}
	
	@Override
	public void run() {
		boolean refreshColumn = false;
		if (this.source.equals(event.getSource())) {
			if (event.getOldValue() == null && event.getNewValue() != null && event.getPropertyName().equals(property)) {
				this.table.addItem(event.getNewValue());
			}else{
				refreshColumn=true;
			}
		}else if (this.sourceList.equals(event.getSource())) {
			if (event.getOldValue() == null && event.getNewValue() != null) {
				this.table.addItem(event.getNewValue());
			}else if (event.getPropertyName().equals(TableList.PROPERTY_ITEMS)) {
				if (event.getOldValue() != null && event.getNewValue() == null) {
					this.table.removeItem(event.getOldValue());
				}
			}
		}else{
			refreshColumn=true;
		}
		if(refreshColumn){
			for(Iterator<TableColumnFX> iterator = this.table.getColumnIterator();iterator.hasNext();){
				TableColumnFX column = iterator.next();
				if(column.getColumn().getAttrName().equals(event.getPropertyName())){
					column.setVisible(false);
					column.setVisible(true);
				}
			}
		}		
	}

}
