package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SendableItem;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.DiagramEditor;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.ModelSet;
import de.uniks.networkparser.list.SimpleList;
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

	public boolean writeToFile(String... directory) {
		String subDir  = "";
		if(directory != null) {
			if(directory.length>0) {
				subDir =directory[0]; 
			}
			if(directory.length>0) {
				this.outputFile = directory[1];
			}
		}
		if (this.outputFile == null || subDir == null) {
			return false;
		}
		XMLEntity frameset = XMLEntity.TAG("frameset").withKeyValue("cols", "250,*");
		frameset.createChild("frame").withKeyValue("src", "refs.html").withKeyValue("name", "Index");
		frameset.createChild("frame").withKeyValue("name", "Main");
		
		HTMLEntity refHtml = new HTMLEntity();
		DiagramEditor.addGraphType(new FileBuffer(), refHtml, ".css", this.outputFile);
		refHtml.withEncoding(HTMLEntity.ENCODING);
		int pos = this.outputFile.lastIndexOf('/');
		String fileName = "";
		if (pos > 0) {
			fileName = subDir + this.outputFile.substring(0, pos) + "/";
		}

		XMLEntity body = refHtml.getBody();
		body.createChild("p", "<a href='title.html' target=\"Main\">Index</a>");
		for (StoryElement subStory : children) {
			if(subStory.writeToFile()) {
				String subFile = subStory.getOutputFile(true);
				if(subFile != null) {
					
					refHtml.createChild("p");
					XMLEntity link = refHtml.createTag("A", refHtml.getBody());
					if(subFile.endsWith(".html")) {
						link.add("href", subFile);
					}else {
						link.add("href", subFile+".html");
					}
					link.add("target", "Main");
					link.withValueItem(subStory.getLabel());
				}
			}
		}
		
		// CREATE MAIN
		HTMLEntity titleHtml = new HTMLEntity();
		titleHtml.withEncoding(HTMLEntity.ENCODING);
		
		
		CharacterBuffer licence = FileBuffer.readFile("licence.txt");
		String[] lines = licence.toString().split("\n");
		if(lines.length>0) {
			String[] words = lines[0].split(" ");
			titleHtml.createBodyTag("h1", words[words.length-1]);
			titleHtml.createBodyTag("h2", "created "+words[words.length-2]);
			if(licence.indexOf("Permission is hereby granted, free of charge,")>0) {
				String logoImage="<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"166\" width=\"321\"><g stroke-width=\"35\" stroke=\"#A31F34\"><path d=\"m17.5,0v166m57-166v113m57-113v166m57-166v33m58,20v113\"/><path d=\"m188.5,53v113\" stroke=\"#8A8B8C\"/><path d=\"m229,16.5h92\" stroke-width=\"33\"/></g></svg>";
				titleHtml.createBodyTag("div", logoImage);
			}
			
			HTMLEntity licenceHTML =new HTMLEntity();
			licenceHTML.withEncoding(HTMLEntity.ENCODING);
			licenceHTML.createBodyTag("div", licence.toString().replaceAll("\r\n", "<br/>"));
			body.createChild("p", "<a href='licence.html' target=\"Main\">Licence</a>");
			FileBuffer.writeFile(fileName + "licence.html", licenceHTML.toString());
		}
//		 
//		titleHtml.html'
		
		frameset.createChild("noframes", body.toString());

		/* INDEX HTML */
		CharacterBuffer output = new CharacterBuffer();
		output.append("<html>");
		output.append(frameset.toString());
		output.append("</html>");
		
		boolean result = FileBuffer.writeFile(fileName + "index.html", output.toString()) >= 0;
		FileBuffer.writeFile(fileName + "refs.html", refHtml.toString());
		FileBuffer.writeFile(fileName + "title.html", titleHtml.toString());
		return result;
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

	public StoryBook withoutTask(Task... values) {
		if (values != null) {
			for (Task item : values) {
				if (item != null) {
					this.children.remove((Object) item);
				}
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

	public StoryBook withoutStory(Story... values) {
		if (values == null) {
			return this;
		}
		for (Story item : values) {
			if (item != null) {
				this.children.remove((Object) item);
			}
		}
		return this;
	}
	
	public StoryStepJUnit createStoryStepJUnit(String... packageName) {
		StoryStepJUnit storyElement = new StoryStepJUnit();
		if(packageName != null && packageName.length>0) {
			storyElement.withPackageName(packageName[0]);
		}
		this.children.add(storyElement);
		return storyElement;
	}

	public Story createStory(String... title) {
		Story story = new Story();
		if(title != null && title.length>0) {
			story.withTitle(title[0]);
		}
		withStory(story);
		return story;
	}
	public Cucumber createScenario(String title) {
		Story value = new Story().withTitle(title);
		withStory(value);
		Cucumber scenario = value.createScenario(title);
		return scenario;
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

	public StoryBook withoutPart(Line... values) {
		if (values != null) {
			for (Line item : values) {
				if (this.part != null && item != null) {
					this.part.remove(item);
				}
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
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		return false;
	}

	public HTMLEntity createKanbanBoard() {
		HTMLEntity element = new HTMLEntity();
		if (part == null) {
			return element;
		}
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

	public StoryBook createFromFile(String fileName) {
		CharacterBuffer readFile = FileBuffer.readFile(fileName);
		SimpleList<String> lines = readFile.splitStrings('\n');
		if (lines.size() < 1) {
			return null;
		}
		// FOR EVERY STORY CREATE A NEW CUCUMBER
		for(int i=0;i<lines.size();i++) {
			String line = lines.get(i);
			String lCase = line.toLowerCase();
			if(lCase.startsWith(Cucumber.TYPE_TITLE)) {
				// NEW ONE
				String text = line.substring(6).trim();
				Story value = new Story().withTitle(text);
				withStory(value);
				Cucumber cucumber = value.createScenario(text);
				// Internal FOR
				// start situation, action, result situation
				CharacterBuffer sub=new CharacterBuffer();
				String type = null;
				for(i++;i<lines.size();i++) {
					line = lines.get(i).trim();
					if(line.length()<1) {
						continue;
					}
					lCase = line.toLowerCase();
					if(lCase.startsWith(Cucumber.TYPE_TITLE)) {
						i--;
						 break;
					}
					int pos =line.indexOf(":"); 
					if(pos>0) {
						if(type != null) {
							cucumber.withText(type, sub.toString());
						}
						type = line.substring(0, pos);
						sub.clear();
						sub.with(line.substring(pos+1).trim());
					} else {
						sub.with(' ').with(line);
					}
				}
				if(type != null) {
					cucumber.withText(type, sub.toString());
				}
			}
		}
		return this;
	}

	public StoryBook withPath(String string) {
		this.outputFile = string;
		return this;
	}

	public ClassModel getClassModel(String packageName) {
		ClassModel classModel = new ClassModel(packageName);
		for(StoryElement element :this.children) {
			if(element instanceof Story) {
				Story subStory = (Story) element;
				for(ObjectCondition condition : subStory.getSteps()) {
					if(condition instanceof Cucumber) {
						// Right ONe please Merge
						Cucumber cucumber = (Cucumber) condition;
						ClassModel subModel = cucumber.getClassModel(packageName);
						classModel.add(subModel);
					}
				}
			}
		}
		return classModel;
	}
}