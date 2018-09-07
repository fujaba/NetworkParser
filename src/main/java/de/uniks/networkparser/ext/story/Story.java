package de.uniks.networkparser.ext.story;

import java.io.PrintStream;
/*
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.xml.HTMLEntity;

public class Story extends StoryElement implements Comparable<Story>{
	private String outputFile;
	private String label;
	private SimpleList<ObjectCondition> steps = new SimpleList<ObjectCondition>();
	private int counter=-1;
	private boolean breakOnAssert=true;
	private IdMap map;
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

	public Story withPath(String value) {
		if(value == null) {
			this.path = "";
			return this;
		}
		if(value.endsWith("/") || value.endsWith("\\") ) {
			this.path = value;
		}else {
			this.path = value + "/";
		}
		return this;
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
	
	/**
	 * Add JavaCode to Story board
	 *
	 * @param position Position of Code StartPosition, Endposition
	 * 			if positon == null Full Method
	 * 			StartPosition == -1 // Start at Method
	 * 			EndPosition == -1 End of Method
	 * 	 		EndPosition == 0 End of File
	 * @return the SourceCodeStep
	 */
	public StoryStepSourceCode addSourceCode(int... position) {
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
		step.withCode(this.getClass(), 1);
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
	public boolean dumpHTML(String fileName) {
		return writeToFile(fileName);
	}

	protected boolean writeToFile(String fileName) {
		if (fileName == null || fileName.length() < 1) {
			if(steps.size()<1) {
				return false;
			}
			// get FileName from Stack
			StoryStepSourceCode step = new StoryStepSourceCode().withCode(this.getClass(), 2);
			fileName = step.getFileName();
			if(fileName == null || fileName.length()<1) {
				return false;
			}
		}
		HTMLEntity output = new HTMLEntity();
		addScript(path, "diagramstyle.css", output);

		addScript(path, "highlight.pack.js", output);
		addScript(path, "highlightjs-line-numbers.min.js", output);

		output.withEncoding(HTMLEntity.ENCODING);

		output.withScript("hljs.initHighlightingOnLoad();" + BaseItem.CRLF + "hljs.initLineNumbersOnLoad();", output.getHeader());

		SimpleEvent evt = new SimpleEvent(this, null, null, output);
		for (ObjectCondition step : steps) {
			if(step.update(evt) == false) {
				return false;
			}
		}
		EntityStringConverter converter = new EntityStringConverter(2);
		converter.withPath(path);
		return FileBuffer.writeFile(path + fileName, output.toString(converter))>=0;
	}

	public static boolean addScript(String path, String name, HTMLEntity entry) {
		if(path == null || name == null) {
			return false;
		}
		// CHECK FOR CHANGES
		CharacterBuffer content = new FileBuffer().readResource("graph/"+name);
		if(content == null) {
			return false;
		}
		CharacterBuffer oldContent =FileBuffer.readFile(path + name);
		if(oldContent != null && content.equals(oldContent.toString())) {
			return true;
		}
		int len = FileBuffer.writeFile(path + name, content.toString(), FileBuffer.NONE);
		entry.withHeader(name);
		return len>0;
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

	public boolean showDebugInfos(BaseItem entity, int len, PrintStream stream) {
		if(entity == null) {
			return false;
		}
		return showDebugInfos(entity.toString(new EntityStringConverter(2)), len, stream);
	}
	public boolean showDebugInfos(String value, int len, PrintStream stream, String...messages) {
		if (stream != null) {
			stream.println("###############################");
			stream.println(value);
			stream.println("###############################");
		}
		String msg = null;
		if(messages != null && messages.length>0) {
			msg = messages[0];
		}
		StoryStepCondition step = new StoryStepCondition();
		step.withCondition(msg, value.length(), new Equals().withValue(len));
		this.addCondition(step);
		return step.checkCondition();
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

	public String getOutputFile() {
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

	public StoryStepText addText(String text) {
		return addText(text, true, false);
	}

	public StoryStepText addText(String text, boolean isStep) {
		return addText(text, isStep, false);
	}
	public StoryStepText addText(String text, boolean isStep, boolean html) {
		StoryStepText step = new StoryStepText();
		if(html) {
			step.withHTMLCode(text);
		}else {
			step.withText(text);
		}
		if(isStep) {
			step.setStep(isStep);
		}
		this.add(step);
		return step;
	}
}
