package de.uniks.networkparser.xml;

/*
NetworkParser
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
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleList;

public class PomFile implements SendableEntityCreatorTag, BaseItem{
	public static final String PROPERTY_MODELVERSION = "modelVersion?";
	public static final String PROPERTY_GROUPID = "groupId?";
	public static final String PROPERTY_ARTIFACTID ="artifactId?";
	public static final String PROPERTY_VERSION ="version?";
	public static final String PROPERTY_SCOPE ="scope?";
	public static final String PROPERTY_DEPENDENCIES ="dependencies";
	public static final String PROPERTY_DEPENDENCY ="dependency";

	private static final String TAG="project";
	private String modelVersion;
	private String groupId;
	private String artifactId;
	private String version;
	private String scope;
	private String tag=TAG;
	private SimpleList<PomFile> dependencies = new SimpleList<PomFile>();

	public PomFile withModelVersion(String value) {
		this.modelVersion = value;
		return this;
	}

	public String getModelVersion() {
		return modelVersion;
	}

	public PomFile withGroupId(String value) {
		this.groupId = value;
		return this;
	}

	public String getGroupId() {
		return groupId;
	}

	public PomFile withArtifactId(String value) {
		this.artifactId = value;
		return this;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public PomFile withVersion(String value) {
		this.version = value;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public PomFile withScope(String value) {
		this.scope = value;
		return this;
	}

	public String getScope() {
		return scope;
	}

	public PomFile withTag(String value) {
		this.tag = value;
		return this;
	}

	@Override
	public String getTag() {
		return tag;
	}

	public PomFile withDependency(PomFile value) {
		if(value == null) {
			return this;
		}
		value.withTag("dependency");
		this.dependencies.add( value );
		return this;
	}

	@Override
	public boolean add(Object... values) {
		if(values==null || values.length % 2 == 1) {
			return false;
		}
		for(int i=0;i<values.length;i+=2) {
			if(values[i] instanceof String) {
				setValue(this, (String)values[i], values[i+1], NEW);
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return toString(0, 0);
	}

	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
	}

	private void addChildren(StringBuilder sb, String spaces) {
		if(sb == null) {
			return;
		}
		for(String property : getProperties()) {
			Object value = getValue(this, property);
			if(value!=null){
				sb.append(spaces);
				sb.append("<" + property.substring(0, property.length() - 1)+">");
				sb.append(value);
				sb.append("</"+property.substring(0, property.length() - 1)+">");
			}
		}
	}

	public PomFile withArtifact(String groupId, String artifactId, String version) {
		withGroupId(groupId);
		withArtifactId(artifactId);
		withVersion(version);
		return this;
	}

	protected String toString(int indentFactor, int indent) {
		String spacesChild = "";
		String spaces = "";
		if (indentFactor > 0) {
			spacesChild = "\r\n" + EntityUtil.repeat(' ', indent+indentFactor);
		}
		spaces = EntityUtil.repeat(' ', indent);
		StringBuilder sb = new StringBuilder(spaces);
		if(tag==TAG) {
			sb.append("<"+tag+" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		} else {
			sb.append("<"+tag+">");
		}
		addChildren(sb, spacesChild);

		if(dependencies.size() >0) {
			sb.append(spacesChild+"<dependencies>");
			for(PomFile item : dependencies) {
				sb.append("\r\n" +item.toString(indentFactor, indent+indentFactor+indentFactor));
			}
			sb.append(spacesChild+"</dependencies>");
		}
		if (indentFactor > 0) {
			sb.append("\r\n");
		}
		sb.append(spaces+"</"+tag+">");

		return sb.toString();
	}

	public Object getValue(Object key) {
		return getValue(this, ""+key);
	}
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new PomFile();
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new PomFile();
	}

	@Override
	public String[] getProperties() {
		return new String[]{PROPERTY_MODELVERSION, PROPERTY_GROUPID, PROPERTY_ARTIFACTID, PROPERTY_VERSION, PROPERTY_SCOPE, PROPERTY_DEPENDENCIES};
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(PROPERTY_MODELVERSION.equals(attribute)){
			return ((PomFile)entity).getModelVersion();
		}
		if(PROPERTY_GROUPID.equals(attribute)){
			return ((PomFile)entity).getGroupId();
		}
		if(PROPERTY_ARTIFACTID.equals(attribute)){
			return ((PomFile)entity).getArtifactId();
		}
		if(PROPERTY_VERSION.equals(attribute)){
			return ((PomFile)entity).getVersion();
		}
		if(PROPERTY_SCOPE.equals(attribute)){
			return ((PomFile)entity).getScope();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(PROPERTY_MODELVERSION.equals(attribute)){
			((PomFile)entity).withModelVersion(""+value);
			return true;
		}
		if(PROPERTY_GROUPID.equals(attribute)){
			((PomFile)entity).withGroupId(""+value);
			return true;
		}
		if(PROPERTY_ARTIFACTID.equals(attribute)){
			((PomFile)entity).withArtifactId(""+value);
			return true;
		}
		if(PROPERTY_VERSION.equals(attribute)){
			((PomFile)entity).withVersion(""+value);
			return true;
		}
		if(PROPERTY_SCOPE.equals(attribute)){
			((PomFile)entity).withScope(""+value);
			return true;
		}
		return false;
	}

	@Override
	public String toString(Converter converter) {
		if(converter instanceof EntityStringConverter) {
			EntityStringConverter item = (EntityStringConverter)converter;
			return toString(item.getIndentFactor(), item.getIndent());
		}
		if(converter == null) {
			return null;
		}
		return converter.encode(this);
	}

	private Object getChild(XMLEntity xmlEntity, String value) {
		if(value == null || xmlEntity == null) {
			return null;
		}

		boolean isValue=false;
		String property;
		if(value.endsWith("?")) {
			property = value.substring(0, value.length() - 1);
			isValue=true;
		} else {
			property = value;
		}
		Entity child = xmlEntity.getElementBy(XMLEntity.PROPERTY_TAG, property);
		if(child != null) {
			if(isValue) {
				String newValue = ((XMLEntity) child).getValue();
				setValue(this, value, newValue, NEW);
				return newValue;
			}
			return child;
		}
		return null;
	}

	public PomFile withValue(String value) {
		XMLEntity xmlEntity = new XMLEntity().withValue(value);
		return withValue(xmlEntity);
	}
	public PomFile withValue(XMLEntity xmlEntity) {
		for(String property : getProperties()) {
			Object child = getChild(xmlEntity, property);
			if(PROPERTY_DEPENDENCIES.equals(property) && child != null) {
				// Parse Dependency
				XMLEntity children = (XMLEntity) child;
				for(int i=0;i<children.size();i++) {
					BaseItem dependency = children.getChild(i);
					PomFile pomDependency = new PomFile().withValue((XMLEntity)dependency);
					this.dependencies.add(pomDependency);
				}
			}
		}
		return this;
	}

	public int size() {
		return this.dependencies.size();
	}

	public SimpleList<PomFile> getDependencies() {
		return dependencies;
	}
}
