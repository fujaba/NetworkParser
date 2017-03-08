package de.uniks.networkparser.ext.story;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;

public class Story {
	private String outputFile;
	private SimpleList<StoryStep> steps = new SimpleList<StoryStep>();

	public Story() {

	}

	public Story(Class<?> packageName) {
		String fileName = packageName.getName();
		int pos = fileName.lastIndexOf('.');
		if (pos > 0) {
			fileName = fileName.substring(0, pos);
		}
		startStory(fileName);
	}

	private void startStory(String fileName) {
		StoryStepSourceCode step = new StoryStepSourceCode(fileName);
		this.outputFile = step.getMethodName();
		this.addStep(new StoryStepTitle(step.getMethodName()));
		this.addStep(step);
	}

	public Story(String packageName) {
		startStory(packageName);
	}

	public void addStep(StoryStep step) {
		this.steps.add(step);
	}

	public Story withFileName(String name) {
		this.outputFile = name;
		return this;
	}

	public void finish() {
		this.steps.last().finish();
	}

	public boolean dumpHTML() {
		if (this.outputFile == null) {
			return false;
		}
		HTMLEntity output = new HTMLEntity();
		output.withHeader("../src/main/resources/de/uniks/networkparser/graph/diagramstyle.css");
		output.withEncoding(HTMLEntity.ENCODING_UTF8);

		output.withHeader("highlight.pack.js");
		output.withHeader("highlightjs-line-numbers.min.js");
		output.withHeader("github.css");
		output.withHeader("default.css");
		output.withScript("hljs.initHighlightingOnLoad();" + BaseItem.CRLF + "hljs.initLineNumbersOnLoad();");

		for (StoryStep step : steps) {
			step.dump(output);
		}

		File file = new File("doc/" + this.outputFile + ".html");
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
		return true;
	}
}
