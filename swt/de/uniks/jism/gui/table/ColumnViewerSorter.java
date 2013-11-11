package de.uniks.jism.gui.table;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;

import de.uniks.jism.gui.TableList;
import de.uniks.jism.sort.SortingDirection;

public class ColumnViewerSorter extends ViewerComparator implements Sorter {
	protected TableColumnView view;
	protected TableComponent tableComponent;
	protected Column columnConfig;
	protected Sorter comparator;
	private SortingDirection direction;

	public ColumnViewerSorter(TableColumnView tableView, TableComponent tableComponent, Column columnConfig){
		super();
		this.tableComponent = tableComponent;
		this.view = tableView;
		this.columnConfig = columnConfig;
		TableViewerColumn tableViewerColumn = tableView.getTableViewerColumn();
		if(tableViewerColumn!=null){
			tableViewerColumn.getColumn().addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					refreshDirection();
				}
			});
		}
	}

	public void refreshDirection(){
		TableViewerColumn tableViewerColumn = view.getTableViewerColumn();
		if (tableViewerColumn.getViewer().getComparator() != null) {
			if (tableViewerColumn.getViewer().getComparator() == ColumnViewerSorter.this) {
				TableList list = tableComponent.getList();
				SortingDirection newDirection=list.changeDirection();
				setSorter(newDirection);
			} else {
				setSorter(SortingDirection.ASC);
			}
		} else {
			setSorter(SortingDirection.DESC);
		}
	}

	public void setSorter(SortingDirection direction) {
		try{
			TableViewerColumn tableViewerColumn = view.getTableViewerColumn();
			TableColumn column = tableViewerColumn.getColumn();
			TableList list=tableComponent.getList();
			list.setSort(columnConfig.getAttrName(), direction, columnConfig.getCellValueCreator());
			
			if(this.comparator==null){
				if(columnConfig instanceof ColumnNotification){
					this.comparator = ((ColumnNotification) columnConfig).getComparator();
				}
			}
			
			
			if(this.comparator==null){
				comparator=this;
			}
			comparator.setDirection(direction);
			this.tableComponent.setSorting(column, direction, this);
		}catch(Exception e){
			
		}
	}
	
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		try{
			return comparator.compareTo(view, columnConfig, e1, e2);
		}catch(Exception e){
			
		}
		return 0;
	}

	@Override
	public int compareTo(TableColumnView viewer, Column column, Object o1, Object o2) {
		TableList value=tableComponent.getList();
		return value.compare(o1, o2);
	}

	@Override
	public void setDirection(SortingDirection value) {
		this.direction = value;
	}
}
