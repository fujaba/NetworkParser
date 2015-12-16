package de.uniks.networkparser.event;

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
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.interfaces.StringItem;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLIdMap;

public class PomFile implements SendableEntityCreatorTag, StringItem, BaseItem{
	public static final String PROPERTY_MODELVERSION = "modelVersion?";
	public static final String PROPERTY_GROUPID = "groupId?";
	public static final String PROPERTY_ARTIFACTID ="artifactId?";
	public static final String PROPERTY_VERSION ="version?";
	public static final String PROPERTY_SCOPE ="scope?";
	public static final String PROPERTY_DEPENDENCY ="dependencies";
	private static final String TAG="project";
	private boolean visible;
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
		value.withTag("dependency");
		this.dependencies.add( value );
		return this;
	}

	@Override
	public PomFile withAll(Object... values) {
		if(values==null || values.length % 2 == 1) {
			return this;
		}
		for(int i=0;i<values.length;i+=2) {
			if(values[i] instanceof String) {
				setValue(this, (String)values[i], values[i+1], IdMap.NEW);	
			}
		}
		return this;
	}
	
	@Override
	public String toString() {
		return toString(0, 0);
	}

	@Override
	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
	}
	
	private void addChildren(StringBuilder sb, String spaces) {
		for(String property : getProperties()) {
			if(!property.endsWith(XMLIdMap.ATTRIBUTEVALUE)){
				continue;
			}
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
	
	public String toString(int indentFactor, int intent) {
		String spacesChild = "";
		String spaces = "";
		if (indentFactor > 0) {
			spacesChild = "\r\n" + EntityUtil.repeat(' ', intent+indentFactor);
		}
		spaces = EntityUtil.repeat(' ', intent);
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
				sb.append("\r\n" +item.toString(indentFactor, intent+indentFactor+indentFactor));
			}
			sb.append(spacesChild+"</dependencies>");
		}
		if (indentFactor > 0) {
			sb.append("\r\n");
		}
		sb.append(spaces+"</"+tag+">");

		return sb.toString();
	}

	@Override
	public Object getValueItem(Object key) {
		return getValue(this, ""+key);
	}
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new PomFile();
	}
	@Override
	public PomFile withVisible(boolean value) {
		this.visible = value;
		return this;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
	
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new PomFile();
	}

	@Override
	public String[] getProperties() {
		return new String[]{PROPERTY_MODELVERSION, PROPERTY_GROUPID, PROPERTY_ARTIFACTID, PROPERTY_VERSION, PROPERTY_SCOPE, PROPERTY_DEPENDENCY};
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
}
