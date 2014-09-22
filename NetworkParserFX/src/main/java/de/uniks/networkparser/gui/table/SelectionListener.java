package de.uniks.networkparser.gui.table;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class SelectionListener implements ListChangeListener<Integer>{
	private boolean isSelection=false;
	private ArrayList<TableViewFX> viewers = new ArrayList<TableViewFX>();

	public SelectionListener withTableViewer(TableViewFX viewer){
		this.viewers.add(viewer);
		return this;
	}
	
	@Override
    public void onChanged(Change<? extends Integer> change)
    {
		if(!isSelection){
			isSelection=true;
			selectItems(change.getList());
			
			isSelection = false;
		}
    }
	public SelectionListener selectItems(List<? extends Integer> items){
		
		for(TableViewFX viewer : viewers){
			setSelection(viewer, items);
		}
		return this;
	}
	
	public void setSelection(TableViewFX  viewer, List<? extends Integer> items){
		if(viewer!=null){
			ObservableList<Integer> selectedIndices = viewer.getSelectionModel().getSelectedIndices();
			for(Iterator<? extends Integer> iterator = items.iterator();iterator.hasNext();){
				Integer item=iterator.next();
				int index = (Integer) item;
				if(!selectedIndices.contains(index)){
					viewer.getSelectionModel().selectIndices(index);
				}
			}
			for(Iterator<? extends Integer> iterator = selectedIndices.iterator();iterator.hasNext();){
				int index=(int)iterator.next();
				if(!items.contains(index)){
					viewer.getSelectionModel().clearSelection(index);
				}
			}
		}
	}
}
