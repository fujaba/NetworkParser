package de.uniks.networkparser.test.ant;

import de.uniks.networkparser.test.ant.sources.NetworkParserSources;

public class CopyStation {
	String source;
	String target;
	boolean createFiles=true;
	boolean createDirectory=true;

	public void execute() {
		// Task's log method
		System.out.println("Source: " + source);
		System.out.println("Target: " + target);

		 NetworkParserSources center= new NetworkParserSources();
		 center.copyFile(source, target, createDirectory, createFiles);
	}

	public void setCreateFile(boolean value) {
		this.createFiles = value;
	}

	public void setCreateDirectory(boolean value) {
		this.createDirectory = value;
	}

	public void setTarget(String value) {
		this.target = value;
	}

	public String getTarget() {
		return target;
	}

	public void setSource(String value) {
		this.source = value;
	}

	public String getSource() {
		return source;
	}

	public static void main(String[] args) {
		CopyStation copyStation = new CopyStation();
		copyStation.setSource("src/de/uniks/networkparser/");
		copyStation.setTarget("../SDMLib/SDMLib.net/src/org/sdmlib/serialization");
		copyStation.setCreateDirectory(false);
		copyStation.execute();
	}
}
