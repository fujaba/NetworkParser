package de.uniks.networkparser.json;

import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.TextEntity;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.graph.util.AttributeSet;
import de.uniks.networkparser.graph.util.ClazzSet;
import de.uniks.networkparser.interfaces.BaseItem;

public class JDLTokener extends Tokener{

	@Override
	public BaseItem encode(Object entity, MapEntity map) {
		if(entity instanceof GraphList) {
			return encodeClassModel((GraphList)entity, map);
		}
		return null;
	}

	private BaseItem encodeClassModel(GraphList entity, MapEntity map) {
		TextEntity result = new TextEntity();
		ClazzSet clazzes = entity.getClazzes();
		for(Clazz item : clazzes) {
			result.add(this.encodeEntity(item));
		}
		AssociationSet associations = entity.getAssociations();
		for(Association assoc : associations) {
			result.add(this.encodeRelationship(assoc));
		}
		
		return result;
	}

	/*
 * 	entity <entity name> {
 *		  <field name> <type> [<validation>*]
 *		}
*/
	public TextEntity encodeEntity(Clazz item) {
		TextEntity result = new TextEntity();
		result.withTag("entity "+item.getName());
		AttributeSet attributes = item.getAttributes();
		TextEntity attributeList = new TextEntity();
		attributeList.withTag(JsonObject.START);
		for(Attribute attribute : attributes) {
			attributeList.add(encodeAttribute(attribute));
		}
		
		if(attributeList.size() > 0 ) {
			attributeList.withTag(JsonObject.START);
			attributeList.withTagEnd(JsonObject.END);
			result.add(attributeList);
		}
		return result;
	}
	
	//  <field name> <type> [<validation>*]
	public TextEntity encodeAttribute(Attribute item) {
		TextEntity result = new TextEntity();
		result.withTag(item.getName()+" "+item.getType(true));
		return result;
	}
	
	/*
	 * relationship (OneToMany | ManyToOne | OneToOne | ManyToMany) {
	 * <from entity>[{<relationship name>[(<display field>)]}] to <to entity>[{<relationship name>[(<display field>)]}]
	 *	  }
	*/
	public TextEntity encodeRelationship(Association assoc) {
		TextEntity relationship = new TextEntity();
		relationship.withTag("relationship "+getCardinality(assoc));
		TextEntity values = new TextEntity();
		values.withTag(JsonObject.START);
		values.add(encodeRelationshipClazz(assoc));
		values.add(" to ");
		values.add(encodeRelationshipClazz(assoc.getOther()));
		values.withTagEnd(JsonObject.END);
		relationship.add(values);
		
		return relationship;
	}
	
	public TextEntity encodeRelationshipClazz(Association assoc) {
		TextEntity result = new TextEntity();
		result.withTag(assoc.getClazz().getName());
		TextEntity values = new TextEntity();
		values.withTag("["+JsonObject.START);
		values.add(assoc.getName());
		values.withTagEnd(JsonObject.END+"]");
		result.add(values);
		return result;
	}
	
	private String getCardinality(Association assoc) {
		if(assoc.getCardinality()==Cardinality.ONE) {
			if(assoc.getOther().getCardinality()==Cardinality.ONE) {
				return "OneToOne";
			}
			return "OneToMany";
		}
		if(assoc.getOther().getCardinality()==Cardinality.ONE) {
			return "ManyToOne";
		}
		return "ManyToMany";
	}
}
