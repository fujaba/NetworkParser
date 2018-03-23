package de.uniks.networkparser.ext;

import java.io.File;

import de.uniks.networkparser.ext.sources.NetworkParserSources;

public class CopyRightSetter {
	String source;
	String copyrightFile;
	private String projectName;
	private boolean showDebug=false;

	public void execute() {
		try{
		// Task's log method
		System.out.println("Copyright Setter");
		System.out.println("Source: " + source);
		System.out.println("Copyright-File: " + copyrightFile);
		System.out.println("Project: " + projectName);

		File file = new File(copyrightFile);
		if (!file.exists()) {
			System.out.println("Copyrightfile does not exist");
			return;
		}

		NetworkParserSources center = new NetworkParserSources();
		center.createComment(copyrightFile, source, projectName, showDebug);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setFile(String value) {
		this.copyrightFile = value;
	}

	public String getFile() {
		return copyrightFile;
	}

	public void setSource(String value) {
		this.source = value;
	}

	public String getSource() {
		return source;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public static void main(String[] args) {

		CopyRightSetter copyRightSetter = new CopyRightSetter();
		copyRightSetter.setProjectName("NetworkParser");
		copyRightSetter.setSource("src/main/java/de/uniks/networkparser/");
		copyRightSetter.setFile("src/main/resources/Licence.txt");
		copyRightSetter.execute();
	}
}
