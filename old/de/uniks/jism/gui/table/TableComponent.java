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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;

import com.sun.scenario.effect.impl.hw.RendererDelegate.Listener;

import de.uniks.jism.IdMap;
import de.uniks.jism.gui.GUIPosition;
import de.uniks.jism.gui.TableList;
import de.uniks.jism.gui.TableListCreator;
import de.uniks.jism.interfaces.SendableEntityCreator;
import de.uniks.jism.json.JsonIdMap;

public class TableComponent extends Control implements Listener, PropertyChangeListener {
	protected TableList list;
	protected TableView<Object> tableViewer;
	protected TableView<Object> fixedTableViewerLeft;
	protected SendableEntityCreator sourceCreator;
	protected Object source;
	protected IdMap map;
	private String property;
	protected UpdateSearchList updateItemListener;
	public static final String PROPERTY_ITEM = "item";
	//	private ArrayList<TableColumnView> columns = new ArrayList<TableColumnView>();
//	private Cursor defaultCursor = new Cursor(Display.getDefault(), SWT.CURSOR_ARROW);
//	private Cursor handCursor = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);
//	private TableItem activeItem;
//
//
//	private boolean isToolTip;
//	private Composite tableComposite;
//	private TableSyncronizer tableSyncronizer;
//	private Menu mnuColumns;
//
//	protected int additionKey;
//	protected TableFilterView tableFilterView;
//	private Menu headerMenu;
//	private SashForm sashForm;
//	public static final String PROPERTY_COLUMN = "column";
	
	public TableComponent(){
		super();
//			super(parent, style);
		if (map == null) {
			this.map = new JsonIdMap();
			this.map.withCreator(new TableListCreator());
		}
		createContent(this);
//		this.tableViewer.
	}

	public void createContent(TableComponent owner) {
//		tableComposite = new Composite(owner, SWT.NONE | SWT.FILL);
//		tableComposite.setLayoutData(GUIPosition.CENTER);
//		tableComposite.setLayout(new BorderLayout(0, 0));

		this.list = new TableList();
		this.list.addPropertyChangeListener(this);
		this.list.setIdMap(map);

//		tableFilterView = new TableFilterView(this, map);

//		headerMenu = new Menu(getShell(), SWT.POP_UP);
//		MenuItem columnsMenue = new MenuItem(headerMenu, SWT.CASCADE);
//		columnsMenue.setText(getText(DefaultTextItems.COLUMNS));
//		mnuColumns = new Menu(getShell(), SWT.DROP_DOWN);
//		columnsMenue.setMenu(mnuColumns);

		tableViewer = createBrowser(GUIPosition.CENTER);
//FIXME		super.getChildren().add(tableViewer);

//		setLayout(new BorderLayout(0, 0));
		
		this.updateItemListener = getUpdateListener();
	}
	
	public TableView<Object> getTableViewer(){
		return tableViewer;
	}
	
	protected UpdateSearchList getUpdateListener()
	{
		return new UpdateSearchList(this);
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public void markLost() {
	}

	protected TableView<Object> createBrowser(GUIPosition browserId) {
//		int flags = SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION;
//		if (!browserId.equals(GUIPosition.CENTER)) {
//			flags = flags | SWT.NO_SCROLL;
//		}
		
		TableView<Object> tableViewer = new TableView<Object>();
		tableViewer.setItems(list);
		
		
//		TableViewerComponent tableViewer = new TableViewerComponent(
//				tableComposite, this, flags, browserId);
//		tableViewer.setFilters(new ViewerFilter[] { tableFilterView });
//
//		Table table = tableViewer.getTable();
//		table.setLinesVisible(true);
//		table.setHeaderVisible(true);
//		table.setMenu(headerMenu);
//
//		table.addListener(SWT.KeyDown, this);
//		table.addListener(SWT.MouseMove, this);
//		table.addListener(SWT.MouseUp, this);
//		table.addListener(SWT.MouseExit, this);
//		table.addListener(SWT.SELECTED, this);
//		ScrollBar verticalScrollBar = table.getVerticalBar();
//		if (verticalScrollBar != null) {
//			verticalScrollBar.addSelectionListener(new SelectionListener() {
//				public void widgetDefaultSelected(SelectionEvent event) {
//				}
//
//				public void widgetSelected(SelectionEvent event) {
//					// listen for drag events in the scrollbar
//					// if the user scrolls away from the bottom, stop auto
//					// scrolling
//					// if the user scrolls back to the bottom, resume auto
//					// scrolling
//					if (event.detail != SWT.DRAG)
//						return;
//					refreshPosition();
//				}
//			});
//		}
//		table.setLayoutData(browserId);

		return tableViewer;
	}

	
	public boolean finishDataBinding(IdMap map, TableList item) {
		this.map = map;
		return finishDataBinding(item);
	}

	public boolean finishDataBinding(TableList item) {
		return finishDataBinding(item, TableList.PROPERTY_ITEMS);
	}

	public boolean finishDataBinding(Object item, String property) {
		if (map == null) {
			return true;
		}
		this.source = item;
		this.sourceCreator = map.getCreatorClass(source);
		this.property = property;
		if (sourceCreator == null) {
			return false;
		}

		// Copy all Elements to TableList
		Collection<?> collection = (Collection<?>) sourceCreator.getValue(item,
				property);
		if (collection != null) {
			Object[] items = collection.toArray(new Object[collection.size()]);
			for(int i=0;i<items.length;i++){
				addItem(items[i]);
			}
		}
		addUpdateListener(source);
		return true;
	}
	
	public ObservableList getColumns(){
		return tableViewer.getColumns();
	}
	
	public void addItem(Object item) {
		if (!list.contains(item)) {
//FIXME			if (tableFilterView.matchesSearchCriteria(item)) {
			list.add(item);
//				tableFilterView.refreshCounter();
				if (getParent() instanceof PropertyChangeListener) {
					((PropertyChangeListener) getParent())
							.propertyChange(new PropertyChangeEvent(this,
									PROPERTY_ITEM, null, item));
				}
//			}
			this.updateItemListener.addItem(item);
		}
	}
	public void addUpdateListener(Object list) {
		this.updateItemListener.addItem(list);
	}


}
