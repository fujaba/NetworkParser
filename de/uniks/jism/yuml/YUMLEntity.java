package de.uniks.jism.yuml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.uniks.jism.interfaces.BaseEntityList;
import de.uniks.jism.interfaces.JSIMEntity;

public class YUMLEntity implements JSIMEntity, BaseEntityList{
	private int typ;
	private String className;
	private String id;
	private boolean showLine;
	private boolean isVisible=true;
	private ArrayList<YUMLEntity> children=new ArrayList<YUMLEntity>();
	
	
	@Override
	public BaseEntityList getNewArray() {
		return new YUMLEntity();
	}

	@Override
	public JSIMEntity getNewObject() {
		return new YUMLEntity();
	}

	@Override
	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
	}

	@Override
	public String toString(int indentFactor, int intent) {
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
			return "[" + id + " : " + className + "]";
		}
		return "[" + id + "]";
	}

	@Override
	public void setVisible(boolean value) {
		this.isVisible = value;
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public BaseEntityList initWithMap(Collection<?> value) {
		for(Iterator<?> i = value.iterator();i.hasNext();){
			Object item = i.next();
			if(item instanceof YUMLEntity){
				children.add((YUMLEntity)item);
			}
		}
		return this;
	}

	@Override
	public BaseEntityList put(Object value) {
		if(value instanceof YUMLEntity){
			children.add((YUMLEntity)value);
		}
		return this;
	}

	@Override
	public int size() {
		return children.size();
	}

	@Override
	public boolean add(Object value) {
		if(value instanceof YUMLEntity){
			children.add((YUMLEntity)value);
			return true;
		}
		return false;
	}

	@Override
	public Object get(int z) {
		return children.get(z);
	}

	
	// GETTER AND SETTER
	public int getTyp() {
		return typ;
	}

	public YUMLEntity withTyp(int typ) {
		this.typ = typ;
		return this;
	}
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

}
