package de.uniks.template;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedList;

public class TemplateResultFile extends SortedList<TemplateResultFragment> implements TemplateInterface, LocalisationInterface {
	public static final String PROPERTY_NAME="name";
	public static final String PROPERTY_PARENT="parent";
	public static final String PROPERTY_HEADERS="headers";
	private String name;
	private String postfix;
	private String extension;
	private String path;
	private TemplateInterface parent;
	
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

	@Override
	public boolean add(TemplateInterface child) {
		if(super.add(child) == false) {
			return false;
		}
		child.setParent(this);
		return true;
	}

	@Override
	public String getText(CharSequence label, Object model, Object gui) {
		return null;
	}

	@Override
	public String get(CharSequence label) {
		return null;
	}

	@Override
	public boolean setParent(TemplateInterface value) {
		if(value != this.parent) {
			this.parent = value;
			return true;
		}
		return false;
	}

	@Override
	public TemplateInterface getParent() {
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
			SimpleList<String> headers=new SimpleList<String>();
			for(TemplateResultFragment child : this) {
				headers.addAll(child.getHeaders());
			}
			return headers;
		}
		
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
