package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.SendableItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.ModelSet;
import de.uniks.networkparser.list.SortedSet;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryBook extends SendableItem implements SendableEntityCreator {
	public static final String PROPERTY_STORIES = "stories";
	public static final String PROPERTY_PART = "part";
	public static final String[] properties = new String[] { PROPERTY_PART, PROPERTY_STORIES };

	private ModelSet<Line> part = null;
	private String outputFile;
	private SortedSet<StoryElement> children = new SortedSet<StoryElement>(true);

	public boolean dumpIndexHTML() {
		return dumpIndexHTML("");
	}

	public boolean dumpIndexHTML(String subDir) {
		if (this.outputFile == null) {
			return false;
		}
		HTMLEntity output = new HTMLEntity();
		// INDEX HTML
		output.withEncoding(HTMLEntity.ENCODING);
		XMLEntity frameset = XMLEntity.TAG("frameset").withKeyValue("cols", "250,*");
		frameset.createChild("frame").withKeyValue("src", "refs.html").withKeyValue("name", "Index");
//		XMLEntity mainFrame = 
		frameset.createChild("frame").withKeyValue("name", "Main");
		frameset.createChild("noframes")
				.withValue("<body><p><a href='refs.html'>Index</a> <a href='refs.html'>Main</a></p></body>");
		output.with(frameset);

		HTMLEntity refHtml = new HTMLEntity();
		refHtml.withHeader("../src/main/resources/de/uniks/networkparser/graph/diagramstyle.css");
		refHtml.withEncoding(HTMLEntity.ENCODING);
		int pos = this.outputFile.lastIndexOf('/');
		String fileName = "";
		if (pos > 0) {
			fileName = subDir + this.outputFile.substring(0, pos) + "/";
		}

		for (StoryElement subStory : children) {
			XMLEntity link = refHtml.createTag("A", refHtml.getBody());
			link.add("href", subStory.getOutputFile());
			link.withValueItem(subStory.getLabel());
		}
		return FileBuffer.writeFile(fileName + "index.html", output.toString()) >= 0;
	}

	public StoryBook withTask(Task... value) {
		if (value == null) {
			return this;
		}
		for (Task item : value) {
			if (item != null) {
				boolean changed = this.children.add(item);
				if (changed) {
					firePropertyChange(PROPERTY_STORIES, null, item);
				}
			}
		}
		return this;
	}

	public StoryBook withoutTask(Task... value) {
		for (Task item : value) {
			if (item != null) {
				this.children.remove((Object) item);
			}
		}
		return this;
	}

	public Task createTask(String description) {
		Task value = new Task().withDescription(description);
		withTask(value);
		return value;
	}

	public StoryBook withStory(Story... value) {
		if (value == null) {
			return this;
		}
		for (Story item : value) {
			if (item != null) {
				boolean changed = this.children.add(item);
				if (changed) {
					firePropertyChange(PROPERTY_STORIES, null, item);
				}
			}
		}
		return this;
	}

	public StoryBook withoutStory(Story... value) {
		for (Story item : value) {
			if (item != null) {
				this.children.remove((Object) item);
			}
		}
		return this;
	}

	public Story createStory(String title) {
		Story value = new Story().withLabel(title);
		withStory(value);
		return value;
	}

	public ModelSet<Line> getPart() {
		return this.part;
	}

	public StoryBook withPart(Line... value) {
		if (value == null) {
			return this;
		}
		for (Line item : value) {
			if (item != null) {
				if (this.part == null) {
					this.part = new ModelSet<Line>();
				}
				boolean changed = this.part.add(item);
				if (changed) {
					firePropertyChange(PROPERTY_PART, null, item);
				}
			}
		}
		return this;
	}

	public StoryBook withoutPart(Line... value) {
		for (Line item : value) {
			if (this.part != null && item != null) {
				this.part.remove(item);
			}
		}
		return this;
	}

	public Line createPart() {
		Line value = new Line();
		withPart(value);
		return value;
	}

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new StoryBook();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	public HTMLEntity createKanbanBoard() {
		HTMLEntity element = new HTMLEntity();
		XMLEntity parent = element.getBody();
		for (Line child : this.part) {
			XMLEntity swimLine = element.createTag("div", parent);
			XMLEntity header = element.createTag("div", swimLine).with("style", "width:100px");
			XMLEntity button = element.createTag("button", header).with("style",
					"width:15px;height:15px;margin:0;padding:0;border: none;");
			button.withValue("-");
			XMLEntity tag = element.createTag("div", header).with("style", "margin-left:5px;float:right;");
			tag.withValue(child.getCaption());

			for (Task task : child.getChildren()) {
				XMLEntity taskContent = element.createTag("div", swimLine).with("style",
						"width:100px;height: 200px;background-color:#ccc;");
				XMLEntity taskBody = element.createTag("div", taskContent).with("style",
						"width:100px;height: 200px;background-color:#ccc;");
				element.createTable(taskBody, "border:1px solid black", "background-color:#f00;width:10px", "", "",
						task.getName());
			}
		}
		return element;
	}
}