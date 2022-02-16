package de.uniks.networkparser.graph;

import java.util.Collection;

import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.xml.HTMLEntity;

/**
 * The Class ObjectModel.
 *
 * @author Stefan
 */
public class ObjectModel extends GraphModel {
	
	/**
	 * Dump HTML.
	 *
	 * @param diagramName the diagram name
	 * @param write the write
	 * @return the HTML entity
	 */
	@Override
	public HTMLEntity dumpHTML(String diagramName, boolean... write) {
		if (diagramName == null || diagramName.length() < 1) {
			diagramName = this.getName();
		}
		if (diagramName == null) {
			diagramName = "Model";
		}
		if (diagramName.length() < 1) {
			return null;
		}
		HTMLEntity entity = super.dumpHTML(diagramName, write);

		if (diagramName.indexOf('/') < 0) {
			diagramName = "doc/" + diagramName;
		}

		diagramName = Story.addResource(entity, diagramName, false);

		if (write == null || write.length < 1 || !write[0]) {
			return entity;
		}
		String htmlText = entity.toString();
		if (FileBuffer.writeFile(diagramName, htmlText) >= 0) {
			return entity;
		}
		return null;
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new ObjectModel();
	}

	/**
	 * With.
	 *
	 * @param name the name
	 * @return the object model
	 */
	/* Override some Method because change ReturnValue */
	@Override
	public ObjectModel with(String name) {
		super.with(name);
		return this;
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	@Override
	public boolean add(Object... values) {
		if (values == null) {
			return true;
		}
		boolean add = true;
		for (Object item : values) {
			if (item instanceof Collection<?>) {
				Collection<?> items = (Collection<?>) item;
				for (Object i : items) {
					add = add(i);
				}
				continue;
			}
			if (item instanceof Match) {
				/* Change */
				Match match = (Match) item;
				GraphMember member = match.getMatch();
				ObjectInstance clazz = this.createClazz(member.getClazz().getName());
				ModifyEntry modifier = ModifyEntry.createModifier(member);
				GraphUtil.setChildren(clazz, modifier);

				Object newValue = match.getNewValue();
				if (newValue instanceof Attribute) {
					GraphUtil.setChildren(clazz, (GraphMember) newValue);
				} else if (newValue instanceof DataType) {
					if (member instanceof Attribute) {
						clazz.createAttribute(member.getName(), (DataType) newValue);
					}
				}
			}
			if (item instanceof Annotation) {
				super.withAnnotation((Annotation) item);
			} else if (item instanceof Clazz) {
				Clazz clazz = (Clazz) item;
				clazz.setClassModel(this);
			} else {
				add = false;
			}
		}
		return add;
	}

	/**
	 * Creates the clazz.
	 *
	 * @param name the name
	 * @return the object instance
	 */
	@Override
	public ObjectInstance createClazz(String name) {
		Clazz item = super.createClazz(name);
		if (item instanceof ObjectInstance) {
			return (ObjectInstance) item;
		}
		return null;
	}

	/**
	 * Creates the object.
	 *
	 * @param name the name
	 * @param type the type
	 * @return the object instance
	 */
	public ObjectInstance createObject(String name, String type) {
		ObjectInstance item = createClazz(type);
		if (item != null) {
			item.setId(name);
		}
		return item;
	}

	@Override
	protected ObjectInstance createInstance(String name) {
		return new ObjectInstance(name);

	}
}
