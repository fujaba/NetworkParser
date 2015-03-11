package de.uniks.networkparser.gui.table;

/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
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
import java.beans.PropertyChangeEvent;
import java.util.Iterator;

import de.uniks.networkparser.gui.TableList;

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
		System.out.println(event);
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
