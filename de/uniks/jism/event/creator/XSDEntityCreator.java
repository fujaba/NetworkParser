package de.uniks.jism.event.creator;

import java.util.ArrayList;

import de.uniks.jism.Tokener;
import de.uniks.jism.event.XSDEntity;
import de.uniks.jism.interfaces.XMLEntityCreator;
import de.uniks.jism.interfaces.XMLGrammar;
import de.uniks.jism.xml.XMLEntity;

public class XSDEntityCreator implements XMLEntityCreator, XMLGrammar{
	private String nameSpace;
	private ArrayList<String> privateStack;
	public static final String[] ignoreTags=new String[]{"annotation", "documentation", "complextype", "simpletype"};

	public XSDEntityCreator(String namespace){
		this.nameSpace = namespace;
	}
	@Override
	public String[] getProperties() {
		return new String[]{XSDEntity.PROPERTY_CHOICE, XSDEntity.PROPERTY_SEQUENCE, XSDEntity.PROPERTY_ATTRIBUTE, XSDEntity.PROPERTY_MINOCCURS, XSDEntity.PROPERTY_MAXOCCURS};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new XSDEntity();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((XSDEntity)entity).getValue(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		((XSDEntity)entity).put(attribute, value);
		return true;
	}

	@Override
	public String getTag() {
		return nameSpace+":element";
	}
	@Override
	public boolean initEntity(XMLEntity entity) {
		 privateStack = new ArrayList<String>();
		 return true;
	}

	@Override
	public boolean parseChild(XMLEntity entity, XMLEntity child, Tokener value) {
		String tag = child.getTag();
		for(String ignoreTag : ignoreTags){
			if(tag.equals(nameSpace+":"+ignoreTag)){
				return true;
			}
		}
		if(entity.getTag().equalsIgnoreCase(nameSpace+":"+XSDEntity.PROPERTY_SEQUENCE)){
			this.privateStack.add(XSDEntity.PROPERTY_SEQUENCE);
		}else if(entity.getTag().equalsIgnoreCase(nameSpace+":"+XSDEntity.PROPERTY_CHOICE)){
			this.privateStack.add(XSDEntity.PROPERTY_CHOICE);
		}
		return false;
	}
	

	@Override
	public void addChildren(XMLEntity parent, XMLEntity child) {
		if(this.privateStack.size()>0){
			String lastTag = this.privateStack.get(this.privateStack.size()-1);
			if(lastTag.equals(XSDEntity.PROPERTY_CHOICE)){
				((XSDEntity)parent).setValue(XSDEntity.PROPERTY_CHOICE, child);
			}else if(lastTag.equals(XSDEntity.PROPERTY_SEQUENCE)){
				((XSDEntity)parent).setValue(XSDEntity.PROPERTY_SEQUENCE, child);
			}

		}
		parent.addChild(child);		
	}
	@Override
	public void endChild(String tag) {
		this.privateStack.remove(this.privateStack.size()-1);
	}

}
