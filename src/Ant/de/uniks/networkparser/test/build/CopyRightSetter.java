package de.uniks.networkparser.test.build;

import java.io.File;

import de.uniks.networkparser.test.build.sources.NetworkParserSources;

public class CopyRightSetter {
	String source;
	String copyrightFile;
	private String projectName;

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
		center.createComment(copyrightFile, source, projectName);
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
		copyRightSetter.setSource("src/de/uniks/networkparser/");
		copyRightSetter.setFile("Ant/de/uniks/networkparser/test/build/Licence.txt");
		copyRightSetter.execute();
	}
}
