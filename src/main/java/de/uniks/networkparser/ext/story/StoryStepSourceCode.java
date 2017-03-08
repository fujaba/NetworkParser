package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepSourceCode implements StoryStep{
	private String contentFile;
	private int startLine;
	private int endLine;
	private CharacterBuffer body;
	private String packageName;
	private String methodName;
	private SimpleKeyValueList<String, String> variables = new SimpleKeyValueList<String, String>(); 
	
	public StoryStepSourceCode(String path) {
		this.packageName = path;
		try {
			throw new Exception("get File");
		} catch (Exception e) {
			getLineFromThrowable(e);
		}
	}
	
	private String getLineFromThrowable(Throwable e) {
		StackTraceElement[] stackTrace = e.getStackTrace();
		for (StackTraceElement ste : stackTrace) {
			String name = ste.getClassName();
			if (name.startsWith(this.packageName)) {
				if (this.methodName == null) {
					// StartLine
					this.contentFile = ste.getFileName();
					this.methodName = ste.getMethodName();
					if(this.methodName.startsWith("test")) {
						this.methodName = this.methodName.substring(4);
					}
					this.startLine = ste.getLineNumber() + 1;
				} else {
					this.endLine = ste.getLineNumber() - 1;
				}
				return name + ".java:" + ste.getLineNumber();
			}
		}
		return "";
	}

	public String getMethodName() {
		return methodName;
	}

	@Override
	public void finish() {
		try {
			throw new Exception("get File");
		} catch (Exception e) {
			getLineFromThrowable(e);
			this.readFile();
		}		
	}
	
	private CharacterBuffer analyseLine(CharacterBuffer line) {
		line = line.trim();
		int pos = line.indexOf("//");
		if(pos >= 0) {
			line.withPosition(pos+1);
			char character = line.getChar();
			if(character == '<') {
				line.skip();
				pos = line.position();
				while(character != '>' && character != 0) {
					character = line.getChar();
				}
				String variable = line.substring(pos, line.position());
				this.variables.add(variable, null);
//				line.replace(search, replace);
			}
		}
		return line;
	}
	
	public void readFile(){
		String fullPath = "src/test/java/" + this.packageName.replace('.', '/') + "/" + this.contentFile;
		int linePos = 1;
		FileBuffer fileBuffer = new FileBuffer();
		fileBuffer.withFile(fullPath);
		CharacterBuffer indexText = new CharacterBuffer();

		CharacterBuffer line = new CharacterBuffer();
		while (line != null && linePos <= this.startLine) {
			line = fileBuffer.readLine();
			linePos++;
		}
		line = analyseLine(line);
		while (line != null && linePos < this.endLine) {
			indexText.with(line);
			line = analyseLine(fileBuffer.readLine());
			linePos++;
			if (linePos < this.endLine) {
				indexText.with(BaseItem.CRLF);
			}
		}
		fileBuffer.close();
		this.body = indexText;
	}

	@Override
	public void dump(HTMLEntity element) {
		XMLEntity code = element.createBodyTag("pre.code");
		code.withValue(this.body);
		code.withKeyValue("class", "java");
	}
}
