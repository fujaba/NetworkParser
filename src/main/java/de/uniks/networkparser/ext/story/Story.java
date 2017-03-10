package de.uniks.networkparser.ext.story;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.xml.HTMLEntity;

public class Story {
	private String outputFile;
	private SimpleList<StoryStep> steps = new SimpleList<StoryStep>();
	private int counter=-1;
	private boolean breakOnAssert=true;

	// COUNTER
	// ADDTABLE
	//ADDBARCHART
	//ADDLINECHART
	//ADDCLASSDIAGRAMM
	//ADDOBJECTDIAGRAMM
	//ADDPATTERN
	//ADDIMAGE
	//ADDSVG
	// ASSERTS

	public Story() {
		this.add(new StoryStepTitle());
	}


	public void add(StoryStep step) {
		this.steps.add(step);
	}
	
	public StoryStepSourceCode addSourceCode(Class<?> className) {
		StoryStepSourceCode step = new StoryStepSourceCode();
		step.withCode(className);
		this.add(step);
		if(this.outputFile == null) {
			this.withFileName(step.getMethodName());
		}
		StoryStep firstStep = this.steps.first();
		if(firstStep instanceof StoryStepTitle) {
			StoryStepTitle titleStep = (StoryStepTitle) firstStep;
			if(titleStep.getTitle() == null) {
				titleStep.setTitle(step.getMethodName());
			}
		}
		return step;
	}

	public Story withFileName(String name) {
		
		if(name.toLowerCase().endsWith(".html")) {
			this.outputFile = name;
		}else {
			this.outputFile = name + ".html";
		}
		return this;
	}

	public void finish() {
		this.steps.last().finish();
	}

	public boolean dumpHTML() {
		if (this.outputFile == null) {
			return false;
		}
		boolean success=true;
		HTMLEntity output = new HTMLEntity();
		output.withHeader("../src/main/resources/de/uniks/networkparser/graph/diagramstyle.css");
		output.withEncoding(HTMLEntity.ENCODING_UTF8);

		output.withHeader("highlight.pack.js");
		output.withHeader("highlightjs-line-numbers.min.js");
		output.withHeader("github.css");
		output.withHeader("default.css");
		output.withScript("hljs.initHighlightingOnLoad();" + BaseItem.CRLF + "hljs.initLineNumbersOnLoad();");

		for (StoryStep step : steps) {
			if(step.dump(this, output) == false) {
				success = false;
				break;
			}
		}
		this.writeFile(output);
		return success;
	}

	protected void writeFile(HTMLEntity output) {
		File file = new File("doc/" + this.outputFile);
		FileOutputStream fop;
		try {
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			// get the content in bytes
			byte[] contentInBytes = output.toString().getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

	}


	public boolean addDescription(String key, String value) {
		StoryStepSourceCode source = null;
		for(int i=this.steps.size() - 1;i>=0;i--) {
			StoryStep step = this.steps.get(i);
			if(this.steps.get(i) instanceof StoryStepSourceCode) {
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
		step.setText(text);
		this.add(step);
		return this;
	}
	
	protected Story withBreakOnAssert(boolean value) {
		this.breakOnAssert = value;
		return this;
	}

//	this.add("Check: " + message + " " + expected + " +-" + delta + " actual " + actual);
//	  Assert.assertEquals("FAILED: " + message, expected, actual, delta);
//    this.add("Check: " + message + " " + condition);
//    if (!condition)
//    {
//       this.dumpHTML();
//    }
	private void checkCondition(StoryStepCondition step) {
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
		step.withMessage(message);
		step.withCondition(new Equals().withValue(expected, delta));
		this.add(step);
		this.checkCondition(step);
	}
	
	public void assertEquals(String message, int expected, int actual) {
	}

	public void assertEquals(String message, long expected, long actual) {
	}
	
	public void assertEquals(String message, Object expected, Object actual) {
	}

	public void assertTrue(String message, boolean condition) {
	}

	public void assertFalse(String message, boolean condition) {
	}

	public void assertNull(String message, Object obj) {
	}
	
	public void assertNotNull(String message, Object obj) {
	}
}
