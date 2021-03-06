package de.uniks.networkparser.xml;

import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedSet;

/**
 * May be a List of Pom of one or more Libaries
 * 
 * @author Stefan Lindel
 */
public class ArtifactList extends SortedSet<ArtifactFile> {
	public boolean isShowMetaData;
	public ArtifactFile biggestSnapShot;
	public ArtifactFile biggestRelease;
	private String groupId;
	private String artifactId;
	private SimpleList<ArtifactList> children;

	public ArtifactList() {
		this(true);
	}

	public ArtifactList(boolean comparator) {
		super(comparator);
	}

	@Override
	public boolean add(ArtifactFile value) {
		if (value == null) {
			return false;
		}
		if (this.children == null) {
			if (this.groupId == null) {
				addItem(value);
			} else if (this.groupId.equals(value.getGroupId())) {
				/* Check For Dupplicate */
				boolean found = false;
				for (ArtifactFile child : this) {
					if (value.isSnapshot() != child.isSnapshot()) {
						continue;
					}
					if (value.getArtifactId() == null) {
						continue;
					}
					if (value.getArtifactId().equals(child.getArtifactId()) == false) {
						continue;
					}
					if (value.getVersion().equals(child.getVersion()) == false) {
						continue;
					}
					child.addClassifier(value.getClassifier());
					found = true;
					break;
				}
				if (found == false) {
					addItem(value);
				}
			} else {
				/* REFACTORING !!! */
				ArtifactList subList;
				if (this.children == null) {
					children = new SimpleList<ArtifactList>();
				}
				if (this.size() > 0) {
					subList = new ArtifactList(true);
					subList.addAll(this);
					this.children.add(value);
					this.clear();
				}
				subList = new ArtifactList(true);
				subList.add(value);
				this.children.add(subList);
			}
		} else {
			/* PARENT LIST CHECK FOR CHILD */
			String id = value.getGroupId();
			for (ArtifactList childList : children) {
				if (childList.getGroup().equals(id)) {
					childList.add(value);
					id = null;
					break;
				}
			}
			if (id != null) {
				/* So Element not added */
				ArtifactList subList = new ArtifactList(true);
				subList.add(value);
				this.children.add(subList);
			}
		}
		return true;
	}

	private boolean addItem(ArtifactFile pom) {
		if (pom == null) {
			return false;
		}
		super.add(pom);
		if (this.groupId == null) {
			this.groupId = pom.getGroupId();
		}
		if (this.artifactId == null) {
			this.artifactId = pom.getArtifactId();
		}
		if (size() > 0) {
			if (pom.calculatePomNumber(first().getPomNumber())) {
				/* Must be recalculate */
				for (ArtifactFile child : this) {
					child.calculatePomNumber(pom.getPomNumber());
				}
			}
		}
		if (pom.isSnapshot()) {
			if (this.biggestSnapShot == null) {
				this.biggestSnapShot = pom;
			} else if (this.biggestSnapShot.getPomMax() < pom.getPomMax()) {
				this.biggestSnapShot = pom;
			}
		} else {
			if (this.biggestRelease == null) {
				this.biggestRelease = pom;
			} else if (this.biggestRelease.getPomMax() < pom.getPomMax()) {
				this.biggestRelease = pom;
			}
		}
		return true;
	}

	@Override
	public void clear() {
		super.clear();
		this.artifactId = null;
		this.groupId = null;
	}

	public String getGroup() {
		return groupId;
	}

	public String getArtifact() {
		return artifactId;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("groupid", this.groupId);
		json.add("name", this.artifactId);
		JsonArray versions = new JsonArray();
		json.add("versions", versions);
		if (this.biggestRelease != null) {
			json.add("update", this.biggestRelease.toPath() + this.biggestRelease.toFile(true));
		}
		for (ArtifactFile file : this) {
			JsonObject jsonVersion = new JsonObject();
			SimpleList<String> classifiers = file.getClassifiers();
			jsonVersion.add("classifier", new JsonArray().withList(classifiers));
			jsonVersion.add("version", file.getVersion());
			if (file.isSnapshot()) {
				jsonVersion.add("snapshot", true);
			}
			versions.add(jsonVersion);
		}
		return json;
	}

	public String toString() {
		return toJson().toString();
	}

	public String getVersion() {
		if (this.biggestRelease != null) {
			return this.biggestRelease.getVersion();
		}
		if (this.biggestSnapShot != null) {
			return this.biggestSnapShot.getVersion();
		}
		return "";
	}
}
