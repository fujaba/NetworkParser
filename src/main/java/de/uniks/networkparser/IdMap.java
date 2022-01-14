package de.uniks.networkparser;

import java.util.Collection;
import java.util.List;

import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.bytes.ByteEntity;
import de.uniks.networkparser.bytes.ByteTokener;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphPatternMatch;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.xml.EMFTokener;

/**
 * The Class IdMap.
 *
 * @author Stefan Lindel
 */
public class IdMap extends SimpleMap {
	protected ByteTokener byteTokener = new ByteTokener().withMap(this);

	public IdMap() {
		super();
		this.add(new TextItems());
		this.add(new DateTimeEntity());
	}

	@Override
	protected Object decodingByte(Filter filter, byte firstChar, Buffer buffer) {
		MapEntity map = new MapEntity(filter, flag, this, byteTokener);
		/* MUST BE BYTE */
		return byteTokener.decodeValue(firstChar, buffer, map);
	}

	@Override
	protected Object decodingEMF(Tokener tokener, Filter filter, byte flag, Buffer element, Object root) {
		EMFTokener emfTokener = null;
		if (tokener instanceof EMFTokener) {
			emfTokener = (EMFTokener) tokener;
		} else if (tokener == null) {
			emfTokener = new EMFTokener();

		}
		if (emfTokener != null) {
			MapEntity map = new MapEntity(filter, flag, this, emfTokener);
			emfTokener.withMap(this);
			return emfTokener.decode(map, element, root);
		}
		return null;
	}

	@Override
	protected Object decoding(BaseItem value, MapEntity map) {
		if (value instanceof ByteEntity) {
			ByteEntity entity = (ByteEntity) value;
			return byteTokener.decodeValue(entity, map);
		}
		return super.decoding(value, map);
	}

	public ByteItem toByteItem(Object object) {
		MapEntity map = new MapEntity(filter, flag, this, byteTokener);
		return byteTokener.encode(object, map);
	}

	public ByteItem toByteItem(Object object, Filter filter) {
		MapEntity map = new MapEntity(filter, flag, this, byteTokener);
		return byteTokener.encode(object, map);
	}

	public GraphList toObjectDiagram(Object object) {
		GraphTokener tokener = new GraphTokener();
		MapEntity map = new MapEntity(filter, flag, this, tokener);
		tokener.withMap(this);
		return tokener.encode(object, map);
	}

	//
	public GraphList toClassDiagram(Object object) {
		GraphTokener tokener = new GraphTokener();
		MapEntity map = new MapEntity(filter, flag, this, tokener);
		map.withTokenerFlag(GraphTokener.FLAG_CLASS);
		tokener.withMap(this);
		return tokener.encode(object, map);
	}

	public GraphPatternMatch getDiff(Object source, Object target, boolean ordered) {
		byte value = GraphTokener.FLAG_UNORDERD;
		if (ordered) {
			value = GraphTokener.FLAG_ORDERD;
		}
		GraphTokener tokener = new GraphTokener();
		MapEntity map = new MapEntity(filter, value, this, tokener);
		map.withFlag(value);
		tokener.withMap(this);
		return tokener.diffModel(source, target, map);
	}

	public boolean addI18N(Object root, TextItems i18n) {
		return addI18N(root, i18n, new SimpleSet<>(), null, null);
	}

	private boolean addI18N(Object root, TextItems i18n, SimpleSet<Object> items, String key, List<?> subElements) {
		if (items == null || i18n == null || !items.add(root)) {
			return false;
		}
		SendableEntityCreator creator = this.getCreatorClass(root);
		if (creator == null) {
			return false;
		}
		String[] properties = creator.getProperties();

		for (String property : properties) {
			String fullKey;
			if (key == null) {
				fullKey = property.toLowerCase();
			} else {
				fullKey = key + ":" + property.toLowerCase();
			}

			Object value = creator.getValue(root, property);
			Object element;
			if (i18n.isAutoCreate()
					&& (value == null || (value instanceof Collection<?> && ((Collection<?>) value).isEmpty()))) {
				/* Check for Creating */
				if (value instanceof SendableEntityCreator) {
					/* SIMPLE CASE */
					element = i18n.getLabelValue(fullKey + ":autocreate");
					boolean creating;
					if (element instanceof Boolean) {
						creating = (Boolean) element;
					} else {
						element = i18n.getLabelValue(fullKey);
						if (element instanceof List<?>) {
							subElements = (List<?>) element;
						}
						creating = checkforCreating(element);
					}
					if (creating) {
						Object newValue = ((SendableEntityCreator) value).getSendableInstance(false);
						creator.setValue(root, property, newValue, SendableEntityCreator.NEW);
						value = newValue;
					}
				}
			}

			if (value == null || (i18n.isReplaceEmptyString() && ("" + value).equals(""))) {
				/* Check if Is Text */
				Object text = i18n.getLabelValue(fullKey);
				if (text != null) {
					creator.setValue(root, property, text, NEW);
					continue;
				}
				/* IF SubElements set May be in Collection */
				if (subElements != null) {
					for (Object item : subElements) {
						if (item instanceof Entity) {
							Entity subElement = (Entity) item;
							for (int i = 0; i < subElement.size(); i++) {
								String keyByIndex = subElement.getKeyByIndex(i);
								if (property.equalsIgnoreCase(keyByIndex)) {
									Object newText = subElement.getValueByIndex(i);
									if (newText != null) {
										/* Replace Template if Exist */
										if (i18n.isTemplateReplace() && newText instanceof String) {
											/* Replace TextTemplate */
										}
										creator.setValue(root, property, newText, NEW);
									}
									break;
								}
							}
						}
					}
				}
			}
			if (value instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) value;
				for (Object item : collection) {
					addI18N(item, i18n, items, fullKey, subElements);
				}
			} else {
				addI18N(value, i18n, items, fullKey, subElements);
			}
		}
		return true;
	}

	/**
	 * Check for Creating Rekursiv
	 *
	 * @param element the new Element
	 * @return success for autocreate
	 */
	private boolean checkforCreating(Object element) {
		if (element == null) {
			return false;
		}
		if (element instanceof Entity) {
			Entity entity = (Entity) element;
			Object entitryValue = entity.getValue("autocreate");
			if (entitryValue instanceof Boolean) {
				return (Boolean) entitryValue;
			}
		}
		if (element instanceof List<?>) {
			List<?> collection = (List<?>) element;
			for (Object item : collection) {
				if (checkforCreating(item)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public IdMap withSession(String value) {
		super.withSession(value);
		return this;
	}
	
	@Override
	public IdMap with(Object... values) {
		super.with(values);
		return this;
	}
	
	@Override
	public IdMap withCreator(SendableEntityCreator... createrClass) {
		super.withCreator(createrClass);
		return this;
	}
	
	@Override
	public IdMap withTimeStamp(long newValue) {
		super.withTimeStamp(newValue);
		return this;
	}
}
