package de.uniks.networkparser.ext.javafx.component;

/*
NetworkParser
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

	public void setSelection(TableViewFX viewer, List<? extends Integer> items){
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
