package de.uniks.template;

import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.SortedSet;

public class TemplateResultFile extends SortedSet<TemplateResultFragment> implements SendableEntityCreator, LocalisationInterface {
	public static final String PROPERTY_PARENT="parent";
	public static final String PROPERTY_CHILD="child";
	
	public static final String PROPERTY_NAME="name";
	public static final String PROPERTY_HEADERS="headers";
	private String name;
	private String postfix;
	private String extension;
	private String path;
	private SendableEntityCreator parent;
	
	TemplateResultFile() {
		super(true);
	}

	public TemplateResultFile(Clazz clazz, boolean comparator) {
		super(comparator);
		this.withName(clazz);
	}

	public String getName() {
		return name;
	}
	
	public String getFileName() {
		CharacterBuffer buffer=new CharacterBuffer();
		if(path != null) {
			buffer.with(this.path);
			buffer.with('/');
		}
		buffer.with(this.name);
		buffer.with(this.postfix);
		buffer.with('.');
		buffer.with(this.extension);
		return buffer.toString();
	}

	public TemplateResultFile withName(String name) {
		this.name = name;
		return this;
	}
	
	public TemplateResultFile withName(Clazz clazz) {
		this.name = clazz.getName().replace(".", "/");
		return this;
	}

	public TemplateResultFile withPostfix(String value) {
		this.postfix = value;
		return this;
	}

	public TemplateResultFile withExtension(String value) {
		this.extension = value;
		return this;
	}
	public TemplateResultFile withPath(String value) {
		this.path = value;
		return this;
	}

	public boolean addChild(SendableEntityCreator child) {
		//FIXME FOR NON COMPARATOR
		if(isComparator() == false && child instanceof TemplateResultFragment) {
			TemplateResultFragment fragment = (TemplateResultFragment) child;
			if(fragment.getKey() == Template.TEMPLATE) {
				super.add(0, fragment);
				return true;
			}
		}
		if(super.add(child) == false) {
			return false;
		}
		child.setValue(child, PROPERTY_PARENT, this, SendableEntityCreator.NEW);
		return true;
	}

	@Override
	public String getText(CharSequence label, Object model, Object gui) {
		return null;
	}

	@Override
	public boolean putText(CharSequence label, CharSequence text) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean setParent(SendableEntityCreator value) {
		if(value != this.parent) {
			this.parent = value;
			return true;
		}
		return false;
	}

	public SendableEntityCreator getParent() {
		return this.parent;
	}
	
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TemplateResultFile();
	}

	@Override
	public String[] getProperties() {
		return new String[] {PROPERTY_NAME, PROPERTY_PARENT};
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity instanceof TemplateResultFile == false) {
			return null;
		}
		TemplateResultFile element = (TemplateResultFile) entity;
		int pos = attribute.indexOf('.');
		String attrName;
		if(pos>0) {
			attrName = attribute.substring(0, pos);
		}else {
			attrName = attribute;
		}
		if(PROPERTY_PARENT.equalsIgnoreCase(attrName)) {
			if(pos>0) {
				return element.getParent().getValue(element, attribute.substring(pos+1));
			}
			return element.getParent();
		}
		if(PROPERTY_HEADERS.equalsIgnoreCase(attrName)) {
			SimpleSet<String> headers=new SimpleSet<String>();
			for(TemplateResultFragment child : this) {
				headers.addAll(child.getHeaders());
			}
			return headers;
		}
		
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(PROPERTY_PARENT.equalsIgnoreCase(attribute)) {
			return this.setParent((SendableEntityCreator) value);
		}
		if(PROPERTY_CHILD.equalsIgnoreCase(attribute)) {
			return this.addChild((SendableEntityCreator) value);
		}
		return false;
	}
	
	@Override
	public String toString() {
		CharacterBuffer buffer= new CharacterBuffer();
		for(TemplateResultFragment fragment : this) {
			if(fragment.getKey() == Template.DECLARATION) {
				continue;
			}
			if(fragment.getKey() != Template.IMPORT) {
				// EVALUATION IMPORT
				TextItems 
//				fragment.getTemplate().update(TEMPLATERESULT);
				
			}
			buffer.with(fragment.getValue());
		}
		return buffer.toString();
	}
}
