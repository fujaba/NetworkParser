package de.uniks.networkparser.test.model;

import java.util.ArrayList;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ListEntity implements SendableEntityCreator{
	public static final String OWNER="owner";
	public static final String CHILDREN="children";
	private ListEntity owner;

	private ArrayList<ListEntity> children = new ArrayList<ListEntity>();
	@Override
	public String[] getProperties() {
		return new String[]{OWNER, CHILDREN};
	}
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ListEntity();
	}
	@Override
	public Object getValue(Object entity, String attribute) {
		if(OWNER.equalsIgnoreCase(attribute)){
			return ((ListEntity)entity).getOwner();
		}
		if(CHILDREN.equalsIgnoreCase(attribute)){
			return  ((ListEntity)entity).getChildren();
		}
		return null;
	}
	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if(OWNER.equalsIgnoreCase(attribute)){
			((ListEntity)entity).withOwner((ListEntity) value);
			return  true;
		}
		if(CHILDREN.equalsIgnoreCase(attribute)){
			((ListEntity)entity).withChildren((ListEntity) value);
			return  true;
		}
		if((CHILDREN+IdMap.REMOVE).equalsIgnoreCase(attribute)){
			((ListEntity)entity).withoutChildren((ListEntity) value);
			return true;
		}
		return false;
	}
	public ListEntity getOwner() {
		return owner;
	}
	public ListEntity withOwner(ListEntity value) {
		if(value!=this.owner){
			this.owner = value;
			if(value!=null){
				value.withChildren(this);
			}
		}
		return this;
	}
	public ArrayList<ListEntity> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<ListEntity> children) {
		this.children = children;
	}
	public ListEntity withChildren(ListEntity... children) {
		if(children!=null){
			for (ListEntity item : children){
				if(this.children.add(item)){
					item.withOwner(this);
				}
			}
		}
		return this;
	}
	public ListEntity withoutChildren(ListEntity... children) {
		if(children!=null){
			for (ListEntity item : children){
				this.children.remove(item);
			}
		}
		return this;
	}
}
