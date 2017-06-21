package de.uniks.networkparser.gui.controls;

public class AutoCompletionList {
//	private boolean caseSensitive=false;
//	private boolean sort=false;

	//PRIMITIVE WERTE
//	private ArrayList<String> list=new ArrayList<String>();

	// ANHAND EINER IDMAP
//	private IdMap map;
//	private String property;

	// COLLECTION mit PROPERTY
//	private Set<?> items;
//	private String property;

	//NUR JAVASCRIPT
//	private TreeSet<String> result;
//	private String oldSearch;
//
//	public Set<String> items(String text) {
//		if(oldSearch!=null && oldSearch.startsWith(text) && text.length()>0) {
//			oldSearch = text;
//			// Try to Filter
//			if(caseSensitive){
//				for(Iterator<String> i = result.iterator();i.hasNext();){
//					if(i.next().indexOf(text)<0) {
//						i.remove();
//					}
//				}
//			}else{
//				text = text.toLowerCase();
//				for(Iterator<String> i = result.iterator();i.hasNext();){
//					if(i.next().toLowerCase().indexOf(text)<0) {
//						i.remove();
//					}
//				}
//			}
//			return result;
//		}
//		result = new TreeSet<String>();
//		oldSearch = text;
//		if(caseSensitive){
//			for(String item : list) {
//				if(item.indexOf(text)>=0) {
//					result.add(item);
//				}
//			}
//			if(this.property != null && this.map != null) {
//				HashSet<String> items = this.getSearchList(this.items, this.property, new HashSet<String>());
//				for(Iterator<String> i = items.iterator();i.hasNext();){
//					String item = i.next();
//					if(item.indexOf(text)>=0) {
//						result.add(item);
//					}
//				}
//			}
//		}else{
//			text = text.toLowerCase();
//			for(String item : list) {
//				if(item.toLowerCase().indexOf(text)>=0) {
//					result.add(item);
//				}
//			}
//			if(this.property != null && this.map != null) {
//				HashSet<String> items = this.getSearchList(this.items, this.property, new HashSet<String>());
//				for(Iterator<String> i = items.iterator();i.hasNext();){
//					String item = i.next();
//					if(item.toLowerCase().indexOf(text)>=0) {
//						result.add(item);
//					}
//				}
//			}
//		}
//		return result;
//	}
//
//	private HashSet<String> getSearchList(Collection<?> items, String property, HashSet<String> result) {
//		for(Iterator<?> i = items.iterator();i.hasNext();){
//			Object item = i.next();
//			SendableEntityCreator creator = this.map.getCreatorClass(item);
//			int pos = property.indexOf(".");
//			if(pos<0) {
//				Object value = creator.getValue(item, property);
//				if(value instanceof String) {
//					result.add((String) value);
//				}
//			}else {
//				Object value = creator.getValue(item, property.substring(0, pos));
//				if( value instanceof Collection<?>){
//					getSearchList((Collection<?>)value, property.substring(pos + 1), result);
//				}
//			}
//		}
//		return result;
//	}
//
//	public AutoCompletionList with(String... values) {
//		if(values == null) {
//			return this;
//		}
//		for(String item : values) {
//			this.list.add(item);
//		}
//		return this;
//	}
//
//	public AutoCompletionList withList(Set<?> value, String property, IdMap map) {
//		this.items = value;
//		this.property = property;
//		this.map = map;
//		return this;
//	}
//
//	public AutoCompletionList withCaseSensitive(boolean value) {
//		this.caseSensitive = value;
//		return this;
//	}
//
}
