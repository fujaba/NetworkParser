package de.uniks.networkparser.ext.story;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedSet;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class Story implements Comparable<Story> {
	private String outputFile;
	private String label;
	private SimpleList<ObjectCondition> steps = new SimpleList<ObjectCondition>();
	private int counter=-1;
	private boolean breakOnAssert=true;
	private IdMap map;
	private SortedSet<Story> stories = new SortedSet<Story>(true);
	private String path="doc/";

	// COUNTER
	// ADDTABLE
	//ADDBARCHART
	//ADDLINECHART
	//ADDOBJECTDIAGRAMM
	//ADDPATTERN
	//ADDSVG

	public Story() {
		this.add(new StoryStepTitle());
	}


	public void add(ObjectCondition step) {
		this.steps.add(step);
	}
	
	
	 public String getLabel() {
		 if(this.label == null && this.outputFile != null) {
			 int pos = this.outputFile.lastIndexOf('/');
			 int temp = this.outputFile.lastIndexOf('\\');
			 if(temp>pos) {
				 pos =temp;
			 }
			 if(pos >= 0) {
				 this.label = this.outputFile.substring(pos + 1);
			 }
		 }
		 return this.label;
	 }
	 
	 public Story withLabel(String value) {
		 this.label = value;
		 return this;
	 }
	 
	/**
	 * Add JavaCode to Story board
	 * 
	 * @param className ClassName of SourceCOde
	 * @param position Position of Code StartPosition, Endposition
	 * 			if positon == null Full Method
	 * 			StartPosition == -1 // Start at Method
	 * 			EndPosition == -1 End of Method
	 * 	 		EndPosition == 0 End of File
	 * @return the SourceCodeStep
	 */
	public StoryStepSourceCode addSourceCode(Class<?> className, int... position) {
		StoryStepSourceCode step = new StoryStepSourceCode();
		if(position != null) {
			if(position.length>0) {
				int start = position[0];
				step.withStart(start);
			}
			if(position.length>1) {
				int start = position[1];
				step.withEnd(start);
			}
		}
		step.withCode(className);
		addSourceCodeStep(step);
		return step;
	}
	public StoryStepSourceCode addSourceCode(String rootDir, Class<?> className, String methodSignature) {
		StoryStepSourceCode step = new StoryStepSourceCode();
		step.withMethodSignature(methodSignature);
		step.withCode(rootDir, className);
		addSourceCodeStep(step);
		return step;
	}

	private void addSourceCodeStep(StoryStepSourceCode step) {
		this.add(step);
		if(this.outputFile == null) {
			this.withName(step.getMethodName());
		}
		ObjectCondition firstStep = this.steps.first();
		if(firstStep instanceof StoryStepTitle) {
			StoryStepTitle titleStep = (StoryStepTitle) firstStep;
			if(titleStep.getTitle() == null) {
				titleStep.setTitle(step.getMethodName());
			}
		}
	}
	public StoryStepDiagram addDiagram(ClassModel model) {
		StoryStepDiagram step = new StoryStepDiagram();
		step.withModel(model);
		this.add(step);
		return step;
	}
	
	public StoryStepDiagram addDiagram(StoryObjectFilter filter) {
		StoryStepDiagram step = new StoryStepDiagram();
		step.withFilter(filter);
		this.add(step);
		return step;
	}
	
	public Story withName(String name) {
		if(name == null || name.length() <1) {
			return this;
		}
		if(name.toLowerCase().endsWith(".html")) {
			this.outputFile = name;
		}else {
			this.outputFile = name + ".html";
		}
		return this;
	}
	
	public void addImage(String imageFile) {
		add(new StoryStepImage().withFile(imageFile));
	}

	public boolean dumpHTML() {
		return writeToFile(this.outputFile);
	}
	protected boolean writeToFile(String fileName) {
		if (fileName == null || fileName.length() < 1) {
			return false;
		}
		HTMLEntity output = new HTMLEntity();
		output.withHeader("../src/main/resources/de/uniks/networkparser/graph/diagramstyle.css");
		output.withEncoding(HTMLEntity.ENCODING);

		output.withHeader("highlight.pack.js");
		output.withHeader("highlightjs-line-numbers.min.js");
		output.withHeader("github.css");
		output.withHeader("default.css");
		output.withScript("hljs.initHighlightingOnLoad();" + BaseItem.CRLF + "hljs.initLineNumbersOnLoad();", output.getHeader());

		SimpleEvent evt = new SimpleEvent(this, null, null, output);
		for (ObjectCondition step : steps) {
			if(step.update(evt) == false) {
				return false;
			}
		}
		return FileBuffer.writeFile(path + fileName, output.toString(2));
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean addDescription(String key, String value) {
		StoryStepSourceCode source = null;
		for(int i=this.steps.size() - 1;i>=0;i--) {
			ObjectCondition step = this.steps.get(i);
			if(step instanceof StoryStepSourceCode) {
				source =(StoryStepSourceCode) step;
				break;
			}
		}
		if(source != null) {
			source.addDescription(key, value);
			return true;
		}
		return false;
	}
	
	public Story withCounter(int counter) {
		this.counter = counter;
		return this;
	}
	
	public int getCounter() {
		int value=this.counter;
		if(value >=0) {
			this.counter = this.counter + 1;
		}
		return value;
	}
	
	public Story addText(String text) {
		StoryStepText step = new StoryStepText();
		step.withText(text);
		this.add(step);
		return this;
	}
	
	protected Story withBreakOnAssert(boolean value) {
		this.breakOnAssert = value;
		return this;
	}
	
	public Story withMap(IdMap map) {
		this.map = map;
		return this;
	}
	
	public IdMap getMap() {
		if(map == null) {
			map = new IdMap();
		}
		return map;
	}
	
	public void finish() {
		for (ObjectCondition step : steps) {
			if(step instanceof StoryStepSourceCode) {
				StoryStepSourceCode sourceCode = (StoryStepSourceCode) step;
				sourceCode.finish();
			}
		}
	}

	private void addCondition(StoryStepCondition step) {
		this.add(step);
		if(step.checkCondition() == false && breakOnAssert) {
			this.dumpHTML();
			Method assertClass = null;
			try {
				assertClass = Class.forName("org.junit.Assert").getMethod("assertTrue", String.class, boolean.class);
				if(assertClass != null) {
					assertClass.invoke(null, "FAILED: " + step.getMessage(), false);
				}
			} catch (ReflectiveOperationException e) {
				if(e instanceof InvocationTargetException) {
					Throwable targetException = ((InvocationTargetException)e).getTargetException();
					StoryUtil.throwException(targetException);
				}
			}
			throw new RuntimeException(step.getMessage());
		}
	}
	
	public void assertEquals(String message, double expected, double actual, double delta) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new Equals().withValue(expected, delta));
		this.addCondition(step);
	}
	
	public void assertEquals(String message, int expected, int actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new Equals().withValue(expected));
		this.addCondition(step);
	}

	public void assertEquals(String message, long expected, long actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new Equals().withValue(expected));
		this.addCondition(step);
	}
	
	public void assertEquals(String message, Object expected, Object actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new Equals().withValue(expected));
		this.addCondition(step);
	}

	public void assertTrue(String message, boolean actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new BooleanCondition().withValue(true));
		this.addCondition(step);
	}

	public void assertFalse(String message, boolean actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new BooleanCondition().withValue(false));
		this.addCondition(step);
	}

	public void assertNull(String message, Object actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, Equals.createNullCondition());
		this.addCondition(step);
	}
	
	public void assertNotNull(String message, Object actual) {
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(message, actual, new Not().with(Equals.createNullCondition()));
		this.addCondition(step);
	}
	public boolean dumpIndexHTML() {
		return dumpIndexHTML("");
	}
	public boolean dumpIndexHTML(String subDir) {
		if(this.stories.size() <1 ) {
			return this.dumpHTML();
		}
		HTMLEntity output = new HTMLEntity();
		// INDEX HTML
		output.withEncoding(HTMLEntity.ENCODING);
		XMLEntity frameset = XMLEntity.TAG("frameset").withKeyValue("cols", "250,*");
		frameset.createChild("frame").withKeyValue("src", "refs.html").withKeyValue("name", "Index");
		XMLEntity mainFrame = frameset.createChild("frame").withKeyValue("name", "Main");
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
		
		if(this.steps.size()>0) {
			// has main
			this.writeToFile(fileName+"main.html");
			mainFrame.withKeyValue("src", fileName);
		}
		for(Story subStory : stories) {
			XMLEntity link = refHtml.createTag("A", refHtml.getBody());
			link.add("href", subStory.getOutputFile());
			link.withValueItem(subStory.getLabel());
			if(subStory.size() > 0) {
				subStory.dumpIndexHTML(fileName);
			}
		}
		return FileBuffer.writeFile(fileName+"index.html", output.toString());
	}
	
	private int size() {
		return this.stories.size();
	}
	
	private Object getOutputFile() {
		return outputFile;
	}


//	o1.compareTo( o2 ) < 0 o1 < o2
//	o1.compareTo( o2 ) == 0 o1 == o2
//	o1.compareTo( o2 ) > 0 o1 > o2
	@Override
	public int compareTo(Story story) {
		String label = this.getLabel();
		String otherLabel = null;
		if(story != null ) {
			otherLabel = story.getLabel();
		}
		if(label == null) {
			if(otherLabel != null) {
				return -1;
			}
			return 0;
		}
		return label.compareTo(otherLabel);
	}


	public Story with(Story value) {
		this.stories.add(value);
		return this;
	}
}
