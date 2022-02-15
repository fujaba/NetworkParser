package de.uniks.networkparser.ext.story;

import java.io.PrintStream;
/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.uniks.networkparser.EntityStringConverter;
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * The Class Story.
 *
 * @author Stefan
 */
public class Story extends StoryElement implements Comparable<Story> {
	private String outputFile;
	private SimpleList<ObjectCondition> steps = new SimpleList<ObjectCondition>();
	private int counter = -1;
	private boolean breakOnAssert = true;
	private IdMap map;
	private String path = "doc/";
	private HTMLEntity elements;

	/**
	 * Instantiates a new story.
	 */
	public Story() {
		this.add(new StoryStepTitle());
	}

	/**
	 * Adds the resource.
	 *
	 * @param entity the entity
	 * @param name the name
	 * @param include the include
	 * @return the string
	 */
	public static String addResource(HTMLEntity entity, String name, boolean include) {
		name = name.replace('\\', '/');
		if (!name.toLowerCase().endsWith(".html")) {
			name += ".html";
		}
		String path = "";
		if (name.indexOf('/') < 0) {
			path = "doc/";
		} else {
			path = name.substring(0, name.lastIndexOf("/")) + "/";
		}
		boolean absolutePath = path.toLowerCase().startsWith("file:");
		Class<?> listClass = GraphList.class;
		for (String item : HTMLEntity.GRAPH_RESOURCES) {
			String content = FileBuffer.readResource(listClass.getResourceAsStream(item)).toString();
			if (!include) {
			  if(path.length()>0 && absolutePath) {
			    entity.addResources(include, path + item, content);
			  }else {
			    entity.addResources(include, item, content);
			  }
				if (path.length() > 0) {
					FileBuffer.writeFile(path + item, content);
				} else {
					FileBuffer.writeFile(item, content);
				}
			}else {
			  entity.addResources(include, path + item, content);
			}
		}
		return name;
	}

	/**
	 * Adds the.
	 *
	 * @param step the step
	 */
	public void add(ObjectCondition step) {
		this.steps.add(step);
	}

	/**
	 * With path.
	 *
	 * @param value the value
	 * @return the story
	 */
	public Story withPath(String value) {
		if (value == null) {
			this.path = "";
			return this;
		}
		if (value.endsWith("/") || value.endsWith("\\")) {
			this.path = value;
		} else {
			this.path = value + "/";
		}
		return this;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public StoryStepTitle getTitle() {
		for(ObjectCondition element : steps) {
			if (element instanceof StoryStepTitle) {
				return (StoryStepTitle) element;
			}
		}
		return null;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	@Override
   public String getLabel() {
		StoryStepTitle title = getTitle();
		String label = null;
		if(title != null) {
			label = title.getTitle();
			if(label != null) {
				return label;
			}
		}
		if (this.outputFile != null) {
			int pos = this.outputFile.lastIndexOf('/');
			int temp = this.outputFile.lastIndexOf('\\');
			if (temp > pos) {
				pos = temp;
			}
			if (pos >= 0) {
				label = this.outputFile.substring(pos + 1);
				if(title != null && label != null) {
					title.withTitle(label);
				}
			}
		}
		return label;
	}


	/**
	 * With title.
	 *
	 * @param text the text
	 * @return the story
	 */
	public Story withTitle(String text) {
		StoryStepTitle title = getTitle();
		if(title != null) {
			title.withTitle(text);
		}else {
			steps.add(new StoryStepTitle().withTitle(text));
		}
		return this;
	}

	/**
	 * Add JavaCode to Story board.
	 *
	 * @param className ClassName of SourceCOde
	 * @param position  Position of Code StartPosition, Endposition
	 *                  if positon == null Full Method
	 *                  StartPosition == -1   Start at Method
	 *                  EndPosition == -1 End of Method
	 *                  EndPosition == 0 End of File
	 * @return the SourceCodeStep
	 */
	public StoryStepSourceCode addSourceCode(Class<?> className, int... position) {
		StoryStepSourceCode step = new StoryStepSourceCode();
		if (position != null) {
			if (position.length > 0) {
				int start = position[0];
				step.withStart(start);
			}
			if (position.length > 1) {
				int start = position[1];
				step.withEnd(start);
			}
		}
		step.withCode(className);
		addSourceCodeStep(step);
		return step;
	}

	/**
	 * Add JavaCode to Story board.
	 *
	 * @param position Position of Code StartPosition, Endposition
	 *                 if positon == null Full Method
	 *                 StartPosition == -1  Start at Method
	 *                 EndPosition == -1 End of Method
	 *                 EndPosition == 0 End of File
	 * @return the SourceCodeStep
	 */
	public StoryStepSourceCode addSourceCode(int... position) {
		StoryStepSourceCode step = new StoryStepSourceCode();
		if (position != null) {
			if (position.length > 0) {
				int start = position[0];
				step.withStart(start);
			}
			if (position.length > 1) {
				int start = position[1];
				step.withEnd(start);
			}
		}
		step.withCode(this.getClass(), 1);
		addSourceCodeStep(step);
		return step;
	}

	/**
	 * Adds the source code.
	 *
	 * @param rootDir the root dir
	 * @param className the class name
	 * @param methodSignature the method signature
	 * @return the story step source code
	 */
	public StoryStepSourceCode addSourceCode(String rootDir, Class<?> className, String methodSignature) {
		StoryStepSourceCode step = new StoryStepSourceCode();
		step.withMethodSignature(methodSignature);
		step.withCode(rootDir, className);
		addSourceCodeStep(step);
		return step;
	}

	private void addSourceCodeStep(StoryStepSourceCode step) {
		this.add(step);
		if (this.outputFile == null) {
			this.withName(step.getMethodName());
		}
		ObjectCondition firstStep = this.steps.first();
		if (firstStep instanceof StoryStepTitle) {
			StoryStepTitle titleStep = (StoryStepTitle) firstStep;
			if (titleStep.getTitle() == null) {
				titleStep.withTitle(step.getMethodName());
			}
		}
	}

	/**
	 * Adds the diagram.
	 *
	 * @param model the model
	 * @return the story step diagram
	 */
	public StoryStepDiagram addDiagram(GraphModel model) {
		StoryStepDiagram step = new StoryStepDiagram();
		step.withModel(model);
		this.add(step);
		return step;
	}

	/**
	 * Adds the diagram.
	 *
	 * @param filter the filter
	 * @return the story step diagram
	 */
	public StoryStepDiagram addDiagram(StoryObjectFilter filter) {
		StoryStepDiagram step = new StoryStepDiagram();
		step.withFilter(filter);
		this.add(step);
		return step;
	}

	/**
	 * With name.
	 *
	 * @param name the name
	 * @return the story
	 */
	public Story withName(String name) {
		if (name == null || name.length() < 1) {
			return this;
		}
		if (name.toLowerCase().endsWith(".html")) {
			this.outputFile = name;
		} else {
			this.outputFile = name + ".html";
		}
		return this;
	}

	/**
	 * Adds the image.
	 *
	 * @param imageFile the image file
	 */
	public void addImage(String imageFile) {
		add(new StoryStepImage().withFile(imageFile));
	}

	/**
	 * Write to file.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 */
	@Override
   public boolean writeToFile(String... fileName) {
		String file = null;
		if(fileName == null) {
			file = this.outputFile;
		}else if(fileName.length>0) {
			file = fileName[0];
		}else if(this.getTitle() != null) {
			StoryStepTitle title = this.getTitle();
			if(title.getTitle() != null) {
				file = StringUtil.getValidChars(title.getTitle().trim(), 50);
			}
		}

		if (file == null || file.length() < 1) {
			if (steps.isEmpty()) {
				return false;
			}
			/* get FileName from Stack */
			StoryStepSourceCode step = new StoryStepSourceCode().withCode(this.getClass(), 1);
			file = step.getFileName();
			if (file == null || file.length() < 1) {
				return false;
			}
		}
		HTMLEntity output = new HTMLEntity();
		output.withEncoding(HTMLEntity.ENCODING);
		if(elements != null) {
			XMLEntity body = elements.getBody();
			if(body != null) {
				for(int i=0;i<body.sizeChildren();i++) {
					BaseItem child = body.getChild(i);
					output.add(child);
				}
			}
		}

		SimpleEvent evt = new SimpleEvent(this, null, null, output);
		for (ObjectCondition step : steps) {
			if (!step.update(evt)) {
				return false;
			}
		}
		EntityStringConverter converter = new EntityStringConverter(2);
		converter.withPath(path);
		if(file.indexOf(".")<0) {
			file = file + ".html";
		}
		if(output.getBody() == null || output.getBody().sizeChildren()<1) {
			return false;
		}
		return FileBuffer.writeFile(path + file, output.toString(converter)) >= 0;
	}

	/**
	 * Adds the script.
	 *
	 * @param path the path
	 * @param name the name
	 * @param entry the entry
	 * @return true, if successful
	 */
	public static boolean addScript(String path, String name, HTMLEntity entry) {
		if (path == null || name == null) {
			return false;
		}
		/* CHECK FOR CHANGES */
		CharacterBuffer content = new FileBuffer().readResource("graph/" + name);
		if (content == null) {
			return false;
		}
		entry.withHeader(name);
		CharacterBuffer oldContent = FileBuffer.readFile(path + name);
		if (oldContent != null && content.equals(oldContent.toString())) {
			return true;
		}
		int len = FileBuffer.writeFile(path + name, content.toString(), FileBuffer.NONE);
		return len > 0;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Adds the description.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean addDescription(String key, String value) {
		StoryStepSourceCode source = null;
		for (int i = this.steps.size() - 1; i >= 0; i--) {
			ObjectCondition step = this.steps.get(i);
			if (step instanceof StoryStepSourceCode) {
				source = (StoryStepSourceCode) step;
				break;
			}
		}
		if (source != null) {
			source.addDescription(key, value);
			return true;
		}
		return false;
	}

	/**
	 * With counter.
	 *
	 * @param counter the counter
	 * @return the story
	 */
	public Story withCounter(int counter) {
		this.counter = counter;
		return this;
	}

	/**
	 * Gets the counter.
	 *
	 * @return the counter
	 */
	public int getCounter() {
		int value = this.counter;
		if (value >= 0) {
			this.counter = this.counter + 1;
		}
		return value;
	}

	protected Story withBreakOnAssert(boolean value) {
		this.breakOnAssert = value;
		return this;
	}

	/**
	 * With map.
	 *
	 * @param map the map
	 * @return the story
	 */
	public Story withMap(IdMap map) {
		this.map = map;
		return this;
	}

	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public IdMap getMap() {
		if (map == null) {
			map = new IdMap();
		}
		return map;
	}

	/**
	 * Finish.
	 *
	 * @return true, if successful
	 */
	public boolean finish() {
		for (ObjectCondition step : steps) {
			if (step instanceof StoryStepSourceCode) {
				StoryStepSourceCode sourceCode = (StoryStepSourceCode) step;
				sourceCode.finish();
			}
		}
		return true;
	}

	private boolean addCondition(StoryStepCondition step) {
		if (step == null) {
			return false;
		}
		this.add(step);
		if (!step.checkCondition() && breakOnAssert) {
			this.writeToFile();
			Method assertClass = null;
			try {
				assertClass = Class.forName("org.junit.Assert").getMethod("assertTrue", String.class, boolean.class);
				if (assertClass != null) {
					assertClass.invoke(null, "FAILED: " + step.getMessage(), false);
				}
			} catch (InvocationTargetException e) {
				Throwable targetException = e.getTargetException();
				StoryUtil.throwException(targetException);
			} catch (ReflectiveOperationException e) {//EMPTY
			}
			throw new SimpleException(step.getMessage(), this);
		}
		return true;
	}

	/**
	 * Assert equals.
	 *
	 * @param message the message
	 * @param expected the expected
	 * @param actual the actual
	 * @param delta the delta
	 */
	public void assertEquals(String message, double expected, double actual, double delta) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new Equals().withValue(expected, delta));
		this.addCondition(step);
	}

	/**
	 * Assert fail.
	 *
	 * @param message the message
	 */
	public void assertFail(String message) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, true, new BooleanCondition());
		this.addCondition(step);
	}

	/**
	 * Assert equals.
	 *
	 * @param message the message
	 * @param expected the expected
	 * @param actual the actual
	 */
	public void assertEquals(String message, int expected, int actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new Equals().withValue(expected));
		this.addCondition(step);
	}

	/**
	 * Assert equals.
	 *
	 * @param message the message
	 * @param expected the expected
	 * @param actual the actual
	 */
	public void assertEquals(String message, long expected, long actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new Equals().withValue(expected));
		this.addCondition(step);
	}

	/**
	 * Assert equals.
	 *
	 * @param message the message
	 * @param expected the expected
	 * @param actual the actual
	 */
	public void assertEquals(String message, Object expected, Object actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new Equals().withValue(expected));
		this.addCondition(step);
	}

	/**
	 * Show debug infos.
	 *
	 * @param entity the entity
	 * @param len the len
	 * @param stream the stream
	 * @return true, if successful
	 */
	public boolean showDebugInfos(BaseItem entity, int len, PrintStream stream) {
		if (entity == null) {
			return false;
		}
		return showDebugInfos(entity.toString(new EntityStringConverter(2)), len, stream);
	}

	/**
	 * Show debug infos.
	 *
	 * @param value the value
	 * @param len the len
	 * @param stream the stream
	 * @param messages the messages
	 * @return true, if successful
	 */
	public boolean showDebugInfos(String value, int len, PrintStream stream, String... messages) {
		if (stream != null) {
			stream.println("###############################");
			stream.println(value);
			stream.println("###############################");
		}
		String msg = null;
		if (messages != null && messages.length > 0) {
			msg = messages[0];
		}
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(msg, value.length(), new Equals().withValue(len));
		this.addCondition(step);
		return step.checkCondition();
	}

	/**
	 * Assert true.
	 *
	 * @param message the message
	 * @param actual the actual
	 */
	public void assertTrue(String message, boolean actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new BooleanCondition().withValue(true));
		this.addCondition(step);
	}

	/**
	 * Assert false.
	 *
	 * @param message the message
	 * @param actual the actual
	 */
	public void assertFalse(String message, boolean actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new BooleanCondition().withValue(false));
		this.addCondition(step);
	}

	/**
	 * Assert null.
	 *
	 * @param message the message
	 * @param actual the actual
	 */
	public void assertNull(String message, Object actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, Equals.createNullCondition());
		this.addCondition(step);
	}

	/**
	 * Assert not null.
	 *
	 * @param message the message
	 * @param actual the actual
	 */
	public void assertNotNull(String message, Object actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new Not().with(Equals.createNullCondition()));
		this.addCondition(step);
	}

	/**
	 * Gets the output file.
	 *
	 * @param calculate the calculate
	 * @return the output file
	 */
	@Override
   public String getOutputFile(boolean calculate) {
		if(!calculate || outputFile != null) {
			return outputFile;
		}
		StoryStepTitle title = this.getTitle();
		if(title != null && title.getTitle() != null) {
			return StringUtil.getValidChars(title.getTitle().trim(), 50);
		}
		return null;
	}

	/**
	 * CompareTo Story
	 * o1.compareTo( o2 ) &lt; 0 o1 &lt; o2
	 * o1.compareTo( o2 ) == 0 o1 == o2
	 * o1.compareTo( o2 ) &gt; 0 o1 &gt; o2
	 *
	 * @param story the story
	 * @return the int
	 */
	@Override
	public int compareTo(Story story) {
		String label = this.getLabel();
		String otherLabel = null;
		if (story != null) {
			otherLabel = story.getLabel();
		}
		if (label == null) {
			if (otherLabel != null) {
				return -1;
			}
			return 0;
		}
		return label.compareTo(otherLabel);
	}

	/**
	 * Adds the text.
	 *
	 * @param text the text
	 * @return the story step text
	 */
	public StoryStepText addText(String text) {
		return addText(text, true, false);
	}

	/**
	 * Adds the step.
	 *
	 * @param text the text
	 * @return the story step text
	 */
	public StoryStepText addStep(String text) {
		return addText(text, true, false);
	}

	/**
	 * Adds the text.
	 *
	 * @param text the text
	 * @param isStep the is step
	 * @return the story step text
	 */
	public StoryStepText addText(String text, boolean isStep) {
		return addText(text, isStep, false);
	}

	/**
	 * Adds the text.
	 *
	 * @param text the text
	 * @param isStep the is step
	 * @param html the html
	 * @return the story step text
	 */
	public StoryStepText addText(String text, boolean isStep, boolean html) {
		StoryStepText step = new StoryStepText();
		if (html) {
			step.withHTMLCode(text);
		} else {
			step.withText(text);
		}
		if (isStep) {
			step.setStep(isStep);
		}
		this.add(step);
		return step;
	}

	/**
	 * Create a new Scenario with caption.
	 *
	 * @param caption The Title of the new Scenario
	 * @return a new Cucumber Scenario
	 */
	public Cucumber createScenario(String caption) {
		Cucumber cucumber = Cucumber.createScenario(caption);
		if(caption != null) {
			int pos = caption.indexOf(":");
			if(pos>0) {
				this.withTitle(caption.substring(pos+1));
			}else {
				this.withTitle(caption);
			}
		}
		this.add(cucumber);
		return cucumber;
	}

	/**
	 * Adds the refresh button.
	 *
	 * @return the story
	 */
	public Story addRefreshButton() {
		if(this.elements == null) {
			this.elements = new HTMLEntity();
		}
		XMLEntity btn = this.elements.createChild("button");
		btn.with("onclick", "window.location.reload();", "style", "position:absolute;right:20px;");
		btn.withValueItem("&#8635;");
		return this;
	}

	/**
	 * Gets the steps.
	 *
	 * @return the steps
	 */
	public SimpleList<ObjectCondition> getSteps() {
		return steps;
	}
}
