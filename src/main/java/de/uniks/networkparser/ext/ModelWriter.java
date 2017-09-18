package de.uniks.networkparser.ext;

import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.parser.TemplateResultFile;
import de.uniks.networkparser.parser.TemplateResultModel;

public class ModelWriter {

	public void writeModel(String rootDir, TemplateResultModel model) {
		//TODO Change FOR PARSING FILE
		for (TemplateResultFile file : model) {
			FileBuffer.writeFile(rootDir + file.getFileName(), file.toString());
		}
	}
}
