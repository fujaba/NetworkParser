package de.uniks.networkparser.ext.javafx.controls;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class AutoCompletionList {
	private ArrayList<String> list=new ArrayList<String>();
	private Set<?> items;
	private String property;
	private TreeSet<String> result;
	private String oldSearch;
	private IdMap map;
	private boolean caseSensitive=false;

	public Set<String> items(String text) {
		if(oldSearch!=null && oldSearch.startsWith(text) && text.length()>0) {
			oldSearch = text;
			// Try to Filter
			if(caseSensitive){
				for(Iterator<String> i = result.iterator();i.hasNext();){
					if(i.next().indexOf(text)<0) {
						i.remove();
					}
				}
			}else{
				text = text.toLowerCase();
				for(Iterator<String> i = result.iterator();i.hasNext();){
					if(i.next().toLowerCase().indexOf(text)<0) {
						i.remove();
					}
				}
			}
			return result;
		}
		result = new TreeSet<String>();
		oldSearch = text;
		if(caseSensitive){
			for(String item : list) {
				if(item.indexOf(text)>=0) {
					result.add(item);
				}
			}
			if(this.property != null && this.map != null) {
				HashSet<String> items = this.getSearchList(this.items, this.property, new HashSet<String>());
				for(Iterator<String> i = items.iterator();i.hasNext();){
					String item = i.next();
					if(item.indexOf(text)>=0) {
						result.add(item);
					}
				}
			}
		}else{
			text = text.toLowerCase();
			for(String item : list) {
				if(item.toLowerCase().indexOf(text)>=0) {
					result.add(item);
				}
			}
			if(this.property != null && this.map != null) {
				HashSet<String> items = this.getSearchList(this.items, this.property, new HashSet<String>());
				for(Iterator<String> i = items.iterator();i.hasNext();){
					String item = i.next();
					if(item.toLowerCase().indexOf(text)>=0) {
						result.add(item);
					}
				}
			}
		}
		return result;
	}

	private HashSet<String> getSearchList(Collection<?> items, String property, HashSet<String> result) {
		for(Iterator<?> i = items.iterator();i.hasNext();){
			Object item = i.next();
			SendableEntityCreator creator = this.map.getCreatorClass(item);
			int pos = property.indexOf(".");
			if(pos<0) {
				Object value = creator.getValue(item, property);
				if(value instanceof String) {
					result.add((String) value);
				}
			}else {
				Object value = creator.getValue(item, property.substring(0, pos));
				if( value instanceof Collection<?>){
					getSearchList((Collection<?>)value, property.substring(pos + 1), result);
				}
			}
		}
		return result;
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

	public AutoCompletionList withList(Set<?> value, String property, IdMap map) {
		this.items = value;
		this.property = property;
		this.map = map;
		return this;
	}

	public AutoCompletionList withCaseSensitive(boolean value) {
		this.caseSensitive = value;
		return this;
	}

}
