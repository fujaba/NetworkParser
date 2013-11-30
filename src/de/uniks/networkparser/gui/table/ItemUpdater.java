package de.uniks.networkparser.gui.table;

import java.util.ArrayList;
import java.util.HashMap;

import org.sdmlib.serialization.gui.table.TableColumnInterface;

import de.uniks.networkparser.gui.ModelListenerProperty;

public class ItemUpdater {
	private HashMap<Object, ArrayList<ModelListenerProperty>> listener=new HashMap<Object, ArrayList<ModelListenerProperty>>(); 
	public void addItem(Object item, TableComponent tableComponent, ArrayList<TableColumnInterface> columns){
		for(TableColumnInterface column : columns){
			
		}
	}
}
