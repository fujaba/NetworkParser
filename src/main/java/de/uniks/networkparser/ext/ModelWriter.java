package de.uniks.networkparser.ext;

import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.parser.TemplateResultFile;
import de.uniks.networkparser.parser.TemplateResultModel;

public class ModelWriter {
	private boolean useSDMLibParser = true;
	public void writeModel(String rootDir, TemplateResultModel model) {
		
		// IF FILE EXIST AND Switch is Enable only add missing value
		// Add missed value to Metamodel
		if(useSDMLibParser) {
			
		}
		
		for (TemplateResultFile file : model) {
			FileBuffer.writeFile(rootDir + file.getFileName(), file.toString());
		}
	}
	
	public boolean isSDMLibParser() {
		return useSDMLibParser;
	}
	public void withEnableSDMLibParser(boolean value) {
		this.useSDMLibParser = value;
	}
}
