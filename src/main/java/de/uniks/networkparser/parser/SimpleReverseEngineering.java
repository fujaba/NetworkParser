package de.uniks.networkparser.parser;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Literal;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class SimpleReverseEngineering implements ObjectCondition {
    private NetworkParserLog logger;
    
    public SimpleReverseEngineering withLogger(NetworkParserLog logger) {
        this.logger = logger;
        return this;
    }
    
    private void info(String method, String msg) {
        if(logger != null) {
            logger.info(this, method, msg);
        }
    }
    
	public boolean parsing(GraphModel model, SimpleSet<?> lists) {
		if (lists == null) {
			return true;
		}
		/* Simple Reverse Engineering */
		int i;
		SimpleList<ParserEntity> entities = new SimpleList<ParserEntity>();
		for (i = 0; i < lists.size(); i++) {
			Object child = lists.get(i);
			if (child != null && child instanceof ParserEntity) {
				entities.add(child);
				ParserEntity entity = (ParserEntity) child;
				Clazz clazz = entity.getClazz();
				clazz.setClassModel(model);
			}
		}

		/* Merge Assoc */
		SimpleList<Association> assocList = new SimpleList<Association>();
		SimpleKeyValueList<Clazz, Clazz> generations = new SimpleKeyValueList<Clazz, Clazz>();
		for (i = 0; i < entities.size(); i++) {
			ParserEntity entity = entities.get(i);
			Clazz clazz = entity.getClazz();
			
			SimpleList<SymTabEntry> symbolEntries;

			symbolEntries = entity.getSymbolEntries(SymTabEntry.TYPE_EXTENDS);
			if (symbolEntries != null && symbolEntries.size() == 1) {
				/* Java Extends */
				String name = symbolEntries.get(0).getName();
				Clazz otherClazz = (Clazz) model.getChildByName(name, Clazz.class);
				if (otherClazz != null) {
					generations.put(clazz, otherClazz);
				}
			}
			if (Clazz.TYPE_ENUMERATION.equalsIgnoreCase(clazz.getType())) {
				SimpleList<SymTabEntry> types = entity.getSymbolEntries(SymTabEntry.TYPE_ENUMVALUE);
				for (SymTabEntry enumValue : types) {
					String name = enumValue.getName();
					if (name != null && name.length() > 0) {
						clazz.enableEnumeration(new Literal(name));
					}
				}
				continue;
			}
			symbolEntries = entity.getSymbolEntries(SymTabEntry.TYPE_ATTRIBUTE);
			if (symbolEntries == null) {
				continue;
			}
			for (SymTabEntry symbolEntry : symbolEntries) {
				if (symbolEntry.getDataType() == null || symbolEntry.getName() == null) {
					continue;
				}
				String dataType = symbolEntry.getDataType();
				String name = symbolEntry.getName();
				Clazz otherClazz = (Clazz) model.getChildByName(dataType, Clazz.class);
				if (otherClazz != null) {
					/* Association */
					Association assoc = new Association(otherClazz).with(Association.ONE).with(name);
					assoc.with(AssociationTypes.UNDIRECTIONAL);
					Association otherAssoc = new Association(clazz).with(AssociationTypes.EDGE);
					assoc.with(otherAssoc);
					assocList.add(assoc);
					continue;
				}
				String simpleType = dataType.toLowerCase();
				if (simpleType.startsWith("set<")) {
					/* MANY ASSOCATION OR ATTRIBUTE */
					String clazzName = dataType.substring(4, dataType.length() - 1);
					otherClazz = (Clazz) model.getChildByName(clazzName, Clazz.class);

					/* Its is a Assoc */
					if (otherClazz != null) {
						Association assoc = new Association(otherClazz).with(Association.MANY).with(name);
						assoc.with(AssociationTypes.UNDIRECTIONAL);

						Association otherAssoc = new Association(clazz).with(AssociationTypes.EDGE);
						assoc.with(otherAssoc);
						assocList.add(assoc);
					} else {
					    info("parsing", symbolEntry.getDataType() + ":" + symbolEntry.getName());
					}
					continue;
				}
				if (simpleType.startsWith("list<")) {
					/* MANY ASSOCATION OR ATTRIBUTE */
					String clazzName = dataType.substring(5, dataType.length() - 1);
					otherClazz = (Clazz) model.getChildByName(clazzName, Clazz.class);

					/* Its is a Assoc */
					if (otherClazz != null) {
						Association assoc = new Association(otherClazz).with(Association.MANY).with(name);
						assoc.with(AssociationTypes.UNDIRECTIONAL);

						Association otherAssoc = new Association(clazz).with(AssociationTypes.EDGE);
						assoc.with(otherAssoc);
						assocList.add(assoc);
					} else {
					    info("parsing", symbolEntry.getDataType() + ":" + symbolEntry.getName());
					}
					continue;
				}
				boolean isArray =simpleType.endsWith("[]"); 
				if(isArray) {
					simpleType = simpleType.substring(0, simpleType.length() -2);
				}
				
				/* Parsing simple Attributes */
				if (DataType.STRING.equals(simpleType)) {
					/* Its a String */
					clazz.createAttribute(name, DataType.STRING.withArray(isArray));
				} else if (DataType.BYTE.equals(simpleType)) {
					/* Its a Byte */
					clazz.createAttribute(name, DataType.BYTE.withArray(isArray));
				} else if (DataType.INT.equals(simpleType)) {
					/* Its a INT */
					clazz.createAttribute(name, DataType.INT.withArray(isArray));
				} else if (DataType.FLOAT.equals(simpleType)) {
					/* Its a Float */
					clazz.createAttribute(name, DataType.FLOAT.withArray(isArray));
				} else if (DataType.DOUBLE.equals(simpleType)) {
					/* Its a Double */
					clazz.createAttribute(name, DataType.DOUBLE.withArray(isArray));
				} else if (DataType.LONG.equals(simpleType)) {
					/* Its a Long */
					clazz.createAttribute(name, DataType.LONG.withArray(isArray));
				} else if (DataType.BOOLEAN.equals(simpleType)) {
					/* Its a Boolean */
					clazz.createAttribute(name, DataType.BOOLEAN.withArray(isArray));
				} else if (DataType.DATE.equals(simpleType)
						|| DataType.DATE.getName(true).equalsIgnoreCase(simpleType)) {
					/* Its a Date */
					clazz.createAttribute(name, DataType.DATE.withArray(isArray));
				} else {
				    info("parsing", symbolEntry.getDataType() + ":" + symbolEntry.getName());
				}
			}
		}

		/* Last Step is to find Bidirectional Associaton */
		for (i = assocList.size() - 1; i >= 0; i--) {
			/* Try to make bidrection Assoc and */
			Association valueAssoc = assocList.get(i);
			if (valueAssoc == null) {
				continue;
			}
			Clazz searchClazz = valueAssoc.getOtherClazz();
			for (int j = i - 1; j >= 0; j--) {
				Association nextAssoc = assocList.get(j);
				if (nextAssoc.getClazz() == searchClazz && nextAssoc.getOtherClazz() == valueAssoc.getClazz()) {
					valueAssoc.with(nextAssoc);
					assocList.remove(j);
					break;
				}
			}
		}

		/* Add all Assoc to Clazzes */
		for (i = assocList.size() - 1; i >= 0; i--) {
			Association valueAssoc = assocList.get(i);
			if (valueAssoc == null) {
				continue;
			}
			GraphUtil.setChildren(valueAssoc.getClazz(), valueAssoc);
			Association otherAssoc = valueAssoc.getOther();
			if (otherAssoc.getName() == null) {
			    info("parsing", "UNDIRECTIONAL");
			}
			GraphUtil.setChildren(otherAssoc.getClazz(), otherAssoc);
		}

		/* Add Generation */
		for (i = 0; i < generations.size(); i++) {
			Clazz clazz = generations.getKeyByIndex(i);
			Clazz otherClazz = generations.getValueByIndex(i);
			clazz.withSuperClazz(otherClazz);
		}
		return true;
	}

	@Override
	public boolean update(Object value) {
		if (value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		if (evt.getSource() instanceof GraphModel == false || evt.getNewValue() instanceof SimpleSet<?> == false) {
			return false;
		}
		SimpleSet<?> lists = (SimpleSet<?>) evt.getNewValue();
		GraphModel source = (GraphModel) evt.getSource();
		return parsing(source, lists);

	}
}
