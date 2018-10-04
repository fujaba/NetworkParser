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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleList;

public class ArtifactFile implements SendableEntityCreatorTag, BaseItem, Comparable<ArtifactFile> {
	public static final String PROPERTY_MODELVERSION = "modelVersion?";
	public static final String PROPERTY_GROUPID = "groupId?";
	public static final String PROPERTY_ARTIFACTID = "artifactId?";
	public static final String PROPERTY_VERSION = "version?";
	public static final String PROPERTY_SCOPE = "scope?";
	public static final String PROPERTY_DEPENDENCIES = "dependencies";
	public static final String PROPERTY_DEPENDENCY = "dependency";
	public static final String PROPERTY_CLASSIFIER = "classifier";

	private static final String TAG = "project";
	private String modelVersion;
	private String groupId;
	private String artifactId;
	private String version;
	private String scope;
	private String tag = TAG;
	private String time;
	private SimpleList<ArtifactFile> dependencies = new SimpleList<ArtifactFile>();

	public static final String SNAPSHOT = "SNAPSHOT";
	private boolean isSnapshot;
	private SimpleList<String> classifier = new SimpleList<String>();
	private String index;
	private String fileName;
	private int pomNumber[] = new int[] { 1, 1, 1, 1, 1, 1, 0 };// First 3 Number are Max next 3 Number are Current //
																// Number of Six is Index

	public ArtifactFile withModelVersion(String value) {
		this.modelVersion = value;
		return this;
	}

	public String getModelVersion() {
		return modelVersion;
	}

	public ArtifactFile withGroupId(String value) {
		this.groupId = value;
		return this;
	}

	public String getGroupId() {
		return groupId;
	}

	public ArtifactFile withArtifactId(String value) {
		this.artifactId = value;
		return this;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public ArtifactFile withVersion(String value) {
		this.version = value;
		if (value != null) {
			int start = 0;
			int pos = 0;
			for (int i = 0; i < value.length(); i++) {
				if (value.charAt(i) == '.') {
					if (i - start > this.pomNumber[pos]) {
						this.pomNumber[pos] = i - start;
						this.pomNumber[pos + 3] = this.pomNumber[pos];
					}
					start = i + 1;
					pos++;
				}
			}
			if (value.length() - start > this.pomNumber[pos]) {
				this.pomNumber[pos] = value.length() - start;
				this.pomNumber[pos + 3] = this.pomNumber[pos];
			}
			calculatePomNumber();
		}
		return this;
	}

	public boolean calculatePomNumber(int... newSize) {
		int i;
		boolean change = false;
		if (newSize != null) {
			for (i = 0; i < newSize.length; i++) {
				if (i == 3) {
					break;
				}
				if (this.pomNumber[i] < newSize[i]) {
					this.pomNumber[i] = newSize[i];
				} else if (newSize[i] < this.pomNumber[i]) {
					change = true;
				}
			}
		}
		int start = 0;
		String part, temp = "";
		i = 0;
		part = this.version.substring(start, start + this.pomNumber[i + 3]);
		start += this.pomNumber[i + 3] + 1;
		temp += EntityUtil.strZero(Integer.valueOf(part), this.pomNumber[i]);
		i++;

		if (start + this.pomNumber[i + 3] < this.version.length()) {
			part = this.version.substring(start, start + this.pomNumber[i + 3]);
			start += this.pomNumber[i + 3] + 1;
			try {
				temp += EntityUtil.strZero(Integer.valueOf(part), this.pomNumber[i]);
			} catch (Exception e) {
			}
			i++;
		}
		if (start + this.pomNumber[i + 3] < this.version.length()) {
			try {
				part = this.version.substring(start, start + this.pomNumber[i + 3]);
			} catch (Exception e) {
			}
			try {
				temp += EntityUtil.strZero(Integer.valueOf(part), this.pomNumber[i]);
			} catch (Exception e) {
			}
			this.pomNumber[6] = Integer.valueOf(temp);
		}
		return change;
	}

	public int[] getPomNumber() {
		return pomNumber;
	}

	public int getPomMax() {
		return pomNumber[6];
	}

	public String getVersion() {
		return version;
	}

	public ArtifactFile withScope(String value) {
		this.scope = value;
		return this;
	}

	public String getScope() {
		return scope;
	}

	public ArtifactFile withTag(String value) {
		this.tag = value;
		return this;
	}

	@Override
	public String getTag() {
		return tag;
	}

	public ArtifactFile withDependency(ArtifactFile value) {
		if (value == null) {
			return this;
		}
		value.withTag("dependency");
		this.dependencies.add(value);
		return this;
	}

	@Override
	public boolean add(Object... values) {
		if (values == null || values.length % 2 == 1) {
			return false;
		}
		for (int i = 0; i < values.length; i += 2) {
			if (values[i] instanceof String) {
				setValue(this, (String) values[i], values[i + 1], NEW);
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
		if (sb == null) {
			return;
		}
		for (String property : getProperties()) {
			Object value = getValue(this, property);
			if (value != null) {
				sb.append(spaces);
				sb.append("<" + property.substring(0, property.length() - 1) + ">");
				sb.append(value);
				sb.append("</" + property.substring(0, property.length() - 1) + ">");
			}
		}
	}

	public ArtifactFile withArtifact(String groupId, String artifactId, String version) {
		withGroupId(groupId);
		withArtifactId(artifactId);
		withVersion(version);
		return this;
	}

	protected String toString(int indentFactor, int indent) {
		String spacesChild = "";
		String spaces = "";
		if (indentFactor > 0) {
			spacesChild = "\r\n" + EntityUtil.repeat(' ', indent + indentFactor);
		}
		spaces = EntityUtil.repeat(' ', indent);
		StringBuilder sb = new StringBuilder(spaces);
		if (tag == TAG) {
			sb.append("<" + tag
					+ " xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		} else {
			sb.append("<" + tag + ">");
		}
		addChildren(sb, spacesChild);

		if (dependencies.size() > 0) {
			sb.append(spacesChild + "<dependencies>");
			for (ArtifactFile item : dependencies) {
				sb.append("\r\n" + item.toString(indentFactor, indent + indentFactor + indentFactor));
			}
			sb.append(spacesChild + "</dependencies>");
		}
		if (indentFactor > 0) {
			sb.append("\r\n");
		}
		sb.append(spaces + "</" + tag + ">");

		return sb.toString();
	}

	public Object getValue(Object key) {
		return getValue(this, "" + key);
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new ArtifactFile();
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ArtifactFile();
	}

	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_MODELVERSION, PROPERTY_GROUPID, PROPERTY_ARTIFACTID, PROPERTY_VERSION,
				PROPERTY_SCOPE, PROPERTY_DEPENDENCIES };
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (PROPERTY_MODELVERSION.equals(attribute)) {
			return ((ArtifactFile) entity).getModelVersion();
		}
		if (PROPERTY_GROUPID.equals(attribute)) {
			return ((ArtifactFile) entity).getGroupId();
		}
		if (PROPERTY_ARTIFACTID.equals(attribute)) {
			return ((ArtifactFile) entity).getArtifactId();
		}
		if (PROPERTY_VERSION.equals(attribute)) {
			return ((ArtifactFile) entity).getVersion();
		}
		if (PROPERTY_SCOPE.equals(attribute)) {
			return ((ArtifactFile) entity).getScope();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (PROPERTY_MODELVERSION.equals(attribute)) {
			((ArtifactFile) entity).withModelVersion("" + value);
			return true;
		}
		if (PROPERTY_GROUPID.equals(attribute)) {
			((ArtifactFile) entity).withGroupId("" + value);
			return true;
		}
		if (PROPERTY_ARTIFACTID.equals(attribute)) {
			((ArtifactFile) entity).withArtifactId("" + value);
			return true;
		}
		if (PROPERTY_VERSION.equals(attribute)) {
			((ArtifactFile) entity).withVersion("" + value);
			return true;
		}
		if (PROPERTY_SCOPE.equals(attribute)) {
			((ArtifactFile) entity).withScope("" + value);
			return true;
		}
		return false;
	}

	@Override
	public String toString(Converter converter) {
		if (converter instanceof EntityStringConverter) {
			EntityStringConverter item = (EntityStringConverter) converter;
			return toString(item.getIndentFactor(), item.getIndent());
		}
		if (converter == null) {
			return null;
		}
		return converter.encode(this);
	}

	private Object getChild(XMLEntity xmlEntity, String value) {
		if (value == null || xmlEntity == null) {
			return null;
		}

		boolean isValue = false;
		String property;
		if (value.endsWith("?")) {
			property = value.substring(0, value.length() - 1);
			isValue = true;
		} else {
			property = value;
		}
		Entity child = xmlEntity.getElementBy(XMLEntity.PROPERTY_TAG, property);
		if (child != null) {
			if (isValue) {
				String newValue = ((XMLEntity) child).getValue();
				setValue(this, value, newValue, NEW);
				return newValue;
			}
			return child;
		}
		return null;
	}

	public ArtifactFile withValue(String value) {
		XMLEntity xmlEntity = new XMLEntity().withValue(value);
		return withValue(xmlEntity);
	}

	public ArtifactFile withValue(XMLEntity xmlEntity) {
		for (String property : getProperties()) {
			Object child = getChild(xmlEntity, property);
			if (PROPERTY_DEPENDENCIES.equals(property) && child != null) {
				// Parse Dependency
				XMLEntity children = (XMLEntity) child;
				for (int i = 0; i < children.size(); i++) {
					BaseItem dependency = children.getChild(i);
					ArtifactFile pomDependency = new ArtifactFile().withValue((XMLEntity) dependency);
					this.dependencies.add(pomDependency);
				}
			}
		}
		return this;
	}

	public int size() {
		return this.dependencies.size();
	}

	public SimpleList<ArtifactFile> getDependencies() {
		return dependencies;
	}

	public boolean isValid() {
		boolean valid = true;
		for (ArtifactFile dep : dependencies) {
			if (dep.getVersion().indexOf("+") > 0) {
				valid = false;
			}
		}
		return valid;
	}

	@Override
	public int compareTo(ArtifactFile o) {
		if (o == null) {
			return 1;
		}
		String[] version1 = getVersion().replace(".", "#").split("#");
		String[] version2 = o.getVersion().replace(".", "#").split("#");
		int compare;
		for (int i = 0; i < version1.length; i++) {
			if (i < version2.length) {
				compare = version1[i].compareTo(version2[i]);
				if (compare != 0) {
					return compare;
				}
			}
		}
		String classifier1 = this.getClassifier();
		String classifier2 = o.getClassifier();
		return classifier1.compareTo(classifier2);
	}

	public String getClassifier() {
		if (this.classifier.size() < 0) {
			return "";
		}
		return this.classifier.first();
	}

	/**
	 * @return the isSnapshot
	 */
	public boolean isSnapshot() {
		return isSnapshot;
	}

	public String getIndex() {
		return index;
	}

//	public static final ArtifactFile createContext(String version, String artifactId) {
	public static final ArtifactFile createContext(String fileName, String groupId, String time) {
		ArtifactFile artifactFile = new ArtifactFile();
		int len;
		artifactFile.fileName = fileName;
		String defaultClassifier = "jar";
		fileName = fileName.replace('\\', '/');
		if (fileName.indexOf("/") > 0) {
			// YEAH FILENAME
			int last = fileName.lastIndexOf('.');
			int path = fileName.lastIndexOf('/');
			if (path < 0) {
				path = 0;
			}
			if (last < 0) {
				last = fileName.length() - 1;
			}
			defaultClassifier = fileName.substring(last + 1);
			if (defaultClassifier.equalsIgnoreCase("asc")) {
				return null;
			}
			// REMOVE EXTENSION
			fileName = fileName.substring(path + 1, last);

		}
		// Find ArtifactId
		len = fileName.indexOf("-");
		if (len > 0) {
			artifactFile.artifactId = fileName.substring(0, len);
			fileName = fileName.substring(len + 1);
		}
		len = fileName.indexOf("-");
		if (len > 0) {
			artifactFile.withVersion(fileName.substring(0, len));
			fileName = fileName.substring(len + 1);
		} else {
			// MUST BE RELASE AND NO CLASSIFIER
			artifactFile.withVersion(fileName);
			fileName = defaultClassifier;
		}
		if (fileName.startsWith(SNAPSHOT)) {
			artifactFile.isSnapshot = true;
			if (fileName.length() == SNAPSHOT.length()) {
				fileName = defaultClassifier;
			} else {
				fileName = fileName.substring(SNAPSHOT.length() + 1);
			}
		}
		// REST IS CLASSIFIER
		artifactFile.classifier.add(fileName);
		artifactFile.time = time;

		artifactFile.groupId = groupId;
		artifactFile.index = artifactFile.version + artifactFile.artifactId;

		return artifactFile;
	}

	public ArtifactFile withFile(String name) {
		this.fileName = name;
		return this;
	}

	public String getFileName() {
		return fileName;
	}

	public String toPath() {
		CharacterBuffer path = new CharacterBuffer();
		if (groupId != null) {
			path.with(groupId.replace(".", "/"));
			path.with('/');
		}
		path.with(this.artifactId);
		path.with('/');
		path.with(this.version);
		if (isSnapshot) {
			path.with('-');
			path.with(SNAPSHOT);
		}
		path.with('/');
		return path.toString();
	}

	public SimpleList<String> getClassifiers() {
		return classifier;
	}

	public boolean addClassifier(String value) {
		return this.classifier.add(value);
	}

	public String toFile(boolean groupPath, String... classifier) {
		CharacterBuffer file = new CharacterBuffer();
		file.with(this.artifactId);
		if (groupPath) {
			file.with('-');
			file.with(this.version);
		}
		if (isSnapshot) {
			file.with('-');
			file.with(SNAPSHOT);
		}
		String myClassifier;
		if (classifier != null && classifier.length == 1 && classifier[0] != null) {
			myClassifier = classifier[0];
		} else {
			myClassifier = this.getClassifier();
		}
		if ("jar".equals(myClassifier)) {
			file.with(".jar");
		} else if ("pom".equals(myClassifier)) {
			file.with(".pom");
		} else {
			file.with('-');
			file.with(myClassifier);
			file.with(".jar");
		}
		return file.toString();
	}

	public String getBuildNumber() {
		if (this.version == null) {
			return "0";
		}
		int pos = version.lastIndexOf(".");
		if (pos < 0 || version.lastIndexOf(".", pos - 1) < 0) {
			return "0";
		}
		return version.substring(pos + 1);
	}

	public String getExtension() {
		if (fileName != null) {
			int pos = fileName.lastIndexOf(".");
			if (pos > 0) {
				return fileName.substring(pos + 1);
			}
		}
		return "jar";
	}

	public String getTime(String defaultTime) {
		if (time != null) {
			return time;
		}
		return defaultTime;
	}

	public String getPath() {
		if (fileName != null) {
			fileName = fileName.replace('\\', '/');
			int pos = fileName.lastIndexOf('/');
			if (pos > 0) {
				return fileName.substring(0, pos + 1);
			}
		}
		return "";
	}
}
