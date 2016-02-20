package de.uniks.networkparser.xml;

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

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.xml.util.XMLEntityCreator;
/**
 * A Simple XMLIdMap for Decoding and Encoding XML Elements.
 *
 * @author Stefan Lindel
 */

public class XMLIdMap extends IdMap {
//	public final Filter SimpleFilter = new Filter().withIdFilter(BooleanCondition.value(false));
//	
//	public XMLEntity encode(Object entity, Filter filter) {
//		XMLEntity xmlEntity = new XMLEntity();
//		SendableEntityCreator createrProtoTyp = getCreatorClass(entity);
//		if (createrProtoTyp == null) {
//			return null;
//		}
//		if (createrProtoTyp instanceof SendableEntityCreatorTag) {
//			SendableEntityCreatorTag xmlCreater = (SendableEntityCreatorTag) createrProtoTyp;
//			if (xmlCreater.getTag() != null) {
//				xmlEntity.withTag(xmlCreater.getTag());
//			} else {
//				xmlEntity.withTag(entity.getClass().getName());
//			}
//		} else {
//			xmlEntity.withTag(entity.getClass().getName());
//		}
//		if (filter.isId(entity, entity.getClass().getName())) {
//			xmlEntity.put(ID, getId(entity));
//		}
//		with(filter, entity);
//		String[] properties = createrProtoTyp.getProperties();
//		if (properties != null) {
//			Object referenceObject = createrProtoTyp.getSendableInstance(true);
//			for (String property : properties) {
//				Object value = createrProtoTyp.getValue(entity, property);
//				if (value != null) {
//					Object refValue = createrProtoTyp.getValue(referenceObject,
//							property);
//					boolean encoding = !value.equals(refValue);
//					if (encoding) {
//						if (property.charAt(0)==XMLIdMap.ENTITYSPLITTER) {
//							parserChild(xmlEntity, property, value);
//						} else if (value instanceof Collection<?>) {
//							for (Object item : (Collection<?>) value) {
//								if(hasObjects(filter, item)) {
//									continue;
//								}
//								xmlEntity.with(encode(item, filter));
//							}
//						} else {
//							SendableEntityCreator valueCreater = getCreatorClass(value);
//							if (valueCreater != null) {
//								if(hasObjects(filter, value)) {
//									continue;
//								}
//								xmlEntity.with(encode(value));
//							} else {
//								xmlEntity.put(property, value);
//							}
//						}
//					}
//				}
//			}
//		}
//		return xmlEntity;
//	}
}
