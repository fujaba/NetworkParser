package de.uni.kassel.peermessage.xml;

import java.util.Collection;

import de.uni.kassel.peermessage.EntityUtil;
import de.uni.kassel.peermessage.interfaces.SendableEntityCreator;
import de.uni.kassel.peermessage.interfaces.XMLEntityCreator;

public class Encoding {
	public static final String ID="id";
	private XMLIdMap parent;

	public Encoding(XMLIdMap parent){
		this.parent=parent;
	}

	public XMLEntity encode(Object entity) {
		XMLEntityCreator createrProtoTyp = parent.getCreatorClass(entity);
		if (createrProtoTyp == null) {
			return null;
		} 
		XMLEntity xmlEntity=new XMLEntity();
		if(createrProtoTyp.getTag()!=null){
			xmlEntity.setTag(createrProtoTyp.getTag());
		}else{
			xmlEntity.setTag(entity.getClass().getName());
		}
		String[] properties = createrProtoTyp.getProperties();
		Object referenceObject = createrProtoTyp.getSendableInstance(true);
		
		if(parent.isId()){
			xmlEntity.put(ID, parent.getId(entity));
		}
		
		if (properties != null) {
			for (String property : properties) {
				Object value = createrProtoTyp.getValue(entity, property);
				if (value != null) {
					Object referenceTyp = createrProtoTyp.getValue(referenceObject,
							property);
					if(!value.equals(referenceTyp)){
						if (property.startsWith(XMLIdMap.ENTITYSPLITTER)) {
							parserChild(xmlEntity, property, value);
						}else{
							if (value instanceof Collection<?>) {
								for(Object item : (Collection<?>)value){
									xmlEntity.addChild(encode(item)); 
								}
								
							}else{
								SendableEntityCreator valueCreater = parent.getCreatorClass(value);
								if (valueCreater != null) {
									xmlEntity.addChild(encode(value));
								} else {
									xmlEntity.put(property, value);
								}
							}
						}
					}
				}
			}
		}
		return xmlEntity;
	}
	
	private XMLEntity parserChild(XMLEntity parent,
			String property, Object value) {
		
		if(property.startsWith(XMLIdMap.ENTITYSPLITTER)){
			int pos = property.indexOf(XMLIdMap.ENTITYSPLITTER, 1);
			if(pos<0){
				pos = property.indexOf(XMLIdMap.ATTRIBUTEVALUE, 1);
			}
			String label;
			String newProp="";
			if(pos>0){
				label=property.substring(1,pos);
				newProp=property.substring(pos+1);
			}else{
				label=property.substring(1);
			}
			if(label.length()>0){
				XMLEntity child=new XMLEntity(label);
				parserChild(child, newProp, value);
				parent.addChild(child);
				return child;
			}
		}else if("".equals(property)){
			parent.setValue(EntityUtil.valueToString(value, true, parent));
		}
		return null;
	}
}
