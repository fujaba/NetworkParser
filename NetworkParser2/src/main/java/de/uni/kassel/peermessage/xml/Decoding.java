package de.uni.kassel.peermessage.xml;

import java.util.ArrayList;

import de.uni.kassel.peermessage.interfaces.XMLEntityCreator;

public class Decoding {
	public static final char PREFIX = ':';
	private String buffer;
	private int len;
	private int pos;
	private ArrayList<XMLEntityCreator> stack = new ArrayList<XMLEntityCreator>();
	private ArrayList<Object> entities = new ArrayList<Object>();
	private XMLIdMap parent;

	public Decoding(XMLIdMap parent){
		this.parent=parent;
	}

	public Object decode(String value) {
		Object result = null;
		this.buffer = value;
		this.len = value.length();
		this.pos = 0;
		this.stack.clear();
		this.entities.clear();
		while (pos < len) {
			result = findTag("");
			if (result != null && !(result instanceof String)) {
				break;
			}
		}
		return result;
	}

	private boolean stepPos(char... character) {
		boolean exit = false;
		while (pos < len && !exit) {
			for (char zeichen : character) {
				if (buffer.charAt(pos) == zeichen) {
					exit = true;
					break;
				}
			}
			if (!exit) {
				pos++;
			}
		}
		return exit;
	}

	private Object findTag(String prefix) {
		if (stepPos('<')) {
			int start = ++pos;

			if (stepPos(' ', '>')) {
				String tag = getEntity(start);
				if (tag.length() > 0) {
					XMLEntityCreator entityCreater = parent.getCreatorDecodeClass(tag);
					Object entity = null;
					boolean plainvalue = false;
					String newPrefix = "";
					if (entityCreater == null) {
						// Not found child creater
						entityCreater = stack.get(stack.size() - 1);
						String[] properties = entityCreater.getProperties();
						prefix += tag;

						for (String prop : properties) {
							if (prop.equalsIgnoreCase(prefix)) {
								entity = entities.get(entities.size() - 1);
								plainvalue = true;
								break;
							} else if (prop.startsWith(prefix)) {
								entity = entities.get(entities.size() - 1);
							}
						}

						if (entity != null) {
							if (!plainvalue) {
								newPrefix = prefix + XMLIdMap.ENTITYSPLITTER;
								prefix += XMLIdMap.ATTRIBUTEVALUE;

							}
						}
					} else {
						entity = entityCreater.getSendableInstance(false);
						stack.add(entityCreater);
						entities.add(entity);
						newPrefix = XMLIdMap.ENTITYSPLITTER;
					}
					if (entity != null) {
						if (!plainvalue) {
							convertParams(entityCreater, entity, prefix);
						}
						if (buffer.charAt(pos) == '/') {
							// ENDTAG
							pos += 2;
						} else {
							pos++;
							if (plainvalue) {
								start = pos;
								String value;
								stepPos('<');
								value = buffer.substring(start, pos);
								stepPos('>');
								pos++;
								entityCreater.setValue(entity, prefix, value);
							} else {
								findTag(newPrefix);
							}
						}
						return entity;
					}
				}
			}
		}
		return null;
	}

	private String getEntity(int start) {
		String tag = buffer.substring(start, pos);
		if (tag.startsWith("?xml")) {
		} else if (tag.charAt(0) == '/') {
		} else {
			return tag;
		}
		return "";
	}

	private void convertParams(XMLEntityCreator entityCreater, Object entity,
			String prefix) {
		while (pos < len && buffer.charAt(pos) != '>') {
			if (buffer.charAt(pos) == '/') {
				break;
			}
			int start = ++pos;
			if (buffer.charAt(pos) != '/') {
				if (stepPos('=')) {
					String key = buffer.substring(start, pos);
					pos += 2;
					start = pos;
					if (stepPos('"')) {
						String value = buffer.substring(start, pos++);
						entityCreater.setValue(entity, prefix + key, value);
					}
				}
			}
		}
	}

}
