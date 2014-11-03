package de.uniks.networkparser.gui.controls;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.uniks.networkparser.gui.table.TableList;

public class AutoCompletionList implements AutoCompletion<String>{
	private ArrayList<String> list=new ArrayList<String>();
	private TableList tableList=new TableList();
	
	@Override
	public Set<String> items(String text, boolean caseSensitive) {
		HashSet<String> values = new HashSet<String>();
		if(text==null){
			return values;
		}
		if(caseSensitive){
			for(String item : list) {
				if(text.equals(item)) {
					values.add(item);
				}
//				text.
//				if()
			}
		}
		return values;
	}
	
	public AutoCompletionList with(String... values) {
		if(values == null) {
			return this;
		}
		for(String item : values) {
			this.list.add(item);
		}
		return this;
	}
	
	public AutoCompletionList withList(TableList value) {
		this.tableList = value;
		return this;
	}
}
