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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.uniks.jism.DefaultTextItems;
import de.uniks.jism.IdMap;
import de.uniks.jism.gui.GUIPosition;

public class SearchTableComponent extends TableComponent {
	private Text searchText;
	private Composite northComponents;
	private Composite firstNorth;
   
	public SearchTableComponent(Composite parent, int style, IdMap map) {
		super(parent, style, map);
	}
	
	public SearchTableComponent(Composite parent, int style) {
		super(parent, style);
	}

	public void createContent(Composite owner){
		boolean isInit=northComponents!=null;
		this.setLayout(new RowLayout(1));
		if(!isInit){
			northComponents = new Composite(this, SWT.FILL);
			northComponents.setLayoutData(GUIPosition.NORTH);
			northComponents.setLayout(new GridLayout(3,false));
		}
		
		super.createContent(this);
		if(!isInit){
			firstNorth=new Composite(northComponents, SWT.NONE);
			firstNorth.setLayout(new RowLayout(SWT.HORIZONTAL));
			
			Label lblSearch = new Label(northComponents, SWT.NONE);
			
			lblSearch.setText(getText(DefaultTextItems.SEARCH)+":");
			searchText = new Text(northComponents, SWT.BORDER | SWT.ICON_SEARCH | SWT.SEARCH);
			
			searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			
			tableFilterView.setSearchText(searchText);
			searchText.addModifyListener(tableFilterView);
		}
	}

	public void refreshNorthLayout(){
		if(firstNorth!=null){
			if(firstNorth.getChildren().length<1){
				firstNorth.setVisible(false);
				firstNorth.dispose();
				firstNorth=null;
			}
			northComponents.setLayout(new GridLayout(northComponents.getChildren().length, false));
			this.layout(true);
		}
	}
	
	public boolean finishDataBinding(Object item, String property) {
		boolean result=super.finishDataBinding(item, property);
		refreshNorthLayout();
		
		return result;
	}
	
	public Composite getNorth() {
		return northComponents;
	}

	public Composite getFirstNorth() {
		return firstNorth;
	}

	@Override
	public void executeEvent(PropertyChangeEvent event) {
		super.executeEvent(event);
		
		if (event != null && source.equals(event.getSource()) ){
			if(getProperty()!=null && getProperty().equals(event.getPropertyName())) {
				if(event.getNewValue()==null){
					if(event.getOldValue()!=null){
						//REMOVE ENTRY
						removeItem(event.getOldValue());

					}
				}else if(event.getOldValue()==null){
					// add Item
					addItem(event.getNewValue());
				}
			}
		}
	}

	public void setKeyListener(KeyListener listener) {
		searchText.addKeyListener(listener);
	}
	
	public Text getSearchField() {
		return searchText;
	}
	@Override
	public void addKeyListener(KeyListener listener) {
		super.addKeyListener(listener);
		getSearchField().addKeyListener(listener);
	}
}
