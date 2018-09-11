package de.uniks.networkparser.xml;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.TextEntity;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationSet;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.AttributeSet;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonObject;

public class JDLTokener extends Tokener{
	@Override
	public BaseItem encode(Object entity, MapEntity map) {
		if(entity instanceof GraphList) {
			return encodeClassModel((GraphList)entity, map);
		}
		return null;
	}

	private BaseItem encodeClassModel(GraphList entity, MapEntity map) {
		if(entity == null || map == null) {
			return null;
		}
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

	/**
	 * {@code
	 * 		entity <entity name> {
	 *		  	<field name> <type> [<validation>*]
	 *		}
	 * }
	 * @param item a Clazz for Transform
	 * @return a new TextEntity
	 */
	public TextEntity encodeEntity(Clazz item) {
		TextEntity result = new TextEntity();
		if(item == null) {
			return result;
		}
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

	/** Enocding Attribute
	 * {@code
	 * 		<field name> <type> [<validation>*]
	 * }
	 * @param item The Attribute to Convert
	 * @return a new TextEntity
	 */
	public TextEntity encodeAttribute(Attribute item) {
		TextEntity result = new TextEntity();
		if(item == null || item.getType() == null) {
			return result;
		}
		result.withTag(item.getName()+" "+item.getType().getName(true));
		return result;
	}

	/**
	 * Encoding RelationShip
	 * {@code
	 * 		relationship (OneToMany | ManyToOne | OneToOne | ManyToMany) {
	 *	 		<from entity>[{<relationship name>[(<display field>)]}] to <to entity>[{<relationship name>[(<display field>)]}]
	 *  	}
	 * }
	 * @param assoc a Association
	 * @return a new TextEntity
	 */
	public TextEntity encodeRelationship(Association assoc) {
		TextEntity relationship = new TextEntity();
		if(assoc == null) {
			return relationship;
		}
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
		if(assoc == null || assoc.getClazz() == null) {
			return result;
		}
		result.withTag(assoc.getClazz().getName());
		TextEntity values = new TextEntity();
		values.withTag("["+JsonObject.START);
		values.add(assoc.getName());
		values.withTagEnd(JsonObject.END+"]");
		result.add(values);
		return result;
	}

	private String getCardinality(Association assoc) {
		if(assoc == null || assoc.getOther() == null) {
			return null;
		}
		if(assoc.getCardinality()==Association.ONE) {
			if(assoc.getOther().getCardinality()==Association.ONE) {
				return "OneToOne";
			}
			return "OneToMany";
		}
		if(assoc.getOther().getCardinality()==Association.ONE) {
			return "ManyToOne";
		}
		return "ManyToMany";
	}
}
