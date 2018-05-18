package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.petaf.SendableItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SortedSet;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.simplescrum.model.BoardElement;
import de.uniks.simplescrum.model.util.LineSet;
import de.uniks.simplescrum.model.util.TaskSet;

public class Board extends SendableItem implements SendableEntityCreator {
	public static final String PROPERTY_STORIES = "stories";
	public static final String PROPERTY_PART = "part";
	public static final String[] properties = new String[]{PROPERTY_PART, PROPERTY_STORIES};

	private LineSet part = null;
	private String outputFile;
	private SortedSet<StoryElement> children = new SortedSet<StoryElement>(true);

	public boolean dumpIndexHTML() {
		return dumpIndexHTML("");
	}
	
	public boolean dumpIndexHTML(String subDir) {
		HTMLEntity output = new HTMLEntity();
		// INDEX HTML
		output.withEncoding(HTMLEntity.ENCODING);
		XMLEntity frameset = XMLEntity.TAG("frameset").withKeyValue("cols", "250,*");
		frameset.createChild("frame").withKeyValue("src", "refs.html").withKeyValue("name", "Index");
//		XMLEntity mainFrame = 
		frameset.createChild("frame").withKeyValue("name", "Main");
		frameset.createChild("noframes").withValue("<body><p><a href='refs.html'>Index</a> <a href='refs.html'>Main</a></p></body>");
		output.with(frameset);


		HTMLEntity refHtml = new HTMLEntity();
		refHtml.withHeader("../src/main/resources/de/uniks/networkparser/graph/diagramstyle.css");
		refHtml.withEncoding(HTMLEntity.ENCODING);
		int pos = this.outputFile.lastIndexOf('/');
		String fileName = "";
		if(pos>0) {
			fileName = subDir+this.outputFile.substring(0, pos) + "/";
		}

		for(StoryElement subStory : children) {
			XMLEntity link = refHtml.createTag("A", refHtml.getBody());
			link.add("href", subStory.getOutputFile());
			link.withValueItem(subStory.getLabel());
		}
		return FileBuffer.writeFile(fileName+"index.html", output.toString());
	}

	public Board withTask(Task... value) {
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

	public Board withoutTask(Task... value) {
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
	
	public Board withStory(Story... value) {
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

	public Board withoutStory(Story... value) {
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

	public LineSet getPart() {
		return this.part;
	}

	public Board withPart(Line... value) {
		if (value == null) {
			return this;
		}
		for (Line item : value) {
			if (item != null) {
				if (this.part == null) {
					this.part = new LineSet();
				}
				boolean changed = this.part.add(item);
				if (changed) {
					firePropertyChange(PROPERTY_PART, null, item);
				}
			}
		}
		return this;
	}

	public Board withoutPart(Line... value) {
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
		return new Board();
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

	
}