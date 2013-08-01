package de.uniks.jism.yuml;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import de.uniks.jism.interfaces.BaseEntityList;
import de.uniks.jism.interfaces.JISMEntity;

public class YUMLEntity implements JISMEntity{
	private String className;
	private String id;
	private boolean showLine;
	private boolean isVisible=true;
	private LinkedHashMap<String, String> objValues=new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> clazzValues=new LinkedHashMap<String, String>();
	
	@Override
	public BaseEntityList getNewArray() {
		return new YUMLList();
	}

	@Override
	public JISMEntity getNewObject() {
		return new YUMLEntity();
	}

	@Override
	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
	}

	@Override
	public String toString(int indentFactor, int intent) {
		if(id==null){
			return toString(YUMLIdParser.CLASS, false);
		}
		return toString(YUMLIdParser.OBJECT, false);
	}

	public String toString( int typ, boolean shortString) {
		if(!isVisible){
			return "";
		}
		if (typ == YUMLIdParser.OBJECT) {
			if (showLine) {
				String text = id + " : " + className;
				return "["
						+ text
						+ "\\n"
						+ new String(new char[text.length()]).replace("\0", "&oline;") + "]";
			}
			return "[" + id + " : " + className + parseValues(typ, shortString) + "]";
		}
		return "[" + className + parseValues(typ, shortString) + "]";
	}
	public String parseValues(int typ, boolean shortString){
		if(shortString){
			return "";
		}
		StringBuilder sb=new StringBuilder();
		Iterator<Entry<String, String>> i=null;
		String splitter="";
		if(objValues.size()>0){
			if (typ == YUMLIdParser.OBJECT) {
				i = objValues.entrySet().iterator();
				splitter="=";
			}else if (typ == YUMLIdParser.CLASS) {
				i = clazzValues.entrySet().iterator();
				splitter=":";
				
			}
		}
		if(i!=null){
				sb.append("|");
				Entry<String, String> item = i.next();
				sb.append(item.getKey() + splitter + item.getValue());
				
				while(i.hasNext()){
					item = i.next();
					sb.append(";");
					sb.append(item.getKey() + splitter + item.getValue());
				}
		}
		return sb.toString();
	}

	@Override
	public YUMLEntity withVisible(boolean value) {
		this.isVisible = value;
		return this;
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	// GETTER AND SETTER
	public String getClassName() {
		return className;
	}

	public YUMLEntity withClassName(String className) {
		this.className = className;
		return this;
	}

	public String getId() {
		return id;
	}

	public YUMLEntity withId(String id) {
		this.id = id;
		return this;
	}

	public boolean isShowLine() {
		return showLine;
	}

	public YUMLEntity withShowLine(boolean showLine) {
		this.showLine = showLine;
		return this;
	}

	public void addValue(String property, String clazz, String value){
		this.objValues.put(property, value);
		this.clazzValues.put(property, clazz);
	}
}
