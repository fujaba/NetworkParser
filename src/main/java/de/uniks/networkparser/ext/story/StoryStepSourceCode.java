package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.EntityComparator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepSourceCode implements StoryStep {
	public static final String FORMAT_JAVA = "java";
	public static final String FORMAT_XML = "xml";
	public static final String FORMAT_JSON = "json";
	public static final String TEMPLATESTART = "<i class=\"conum\" data-value=\"";
	public static final String TEMPLATEEND = "\"></i>";
	private String format = null;
	private String contentFile;
	private int startLine;
	private int endLine;
	private CharacterBuffer body;
	private String packageName;
	private String methodName;
	private SimpleKeyValueList<String, String> variables = new SimpleKeyValueList<String, String>()
			.withComparator(EntityComparator.createComparator());
	private String fullPath;

	private void startStory(String path) {
		this.packageName = path;
		getLineFromThrowable();
	}

	private String getLineFromThrowable() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (StackTraceElement ste : stackTrace) {
			String name = ste.getClassName();
			if (name.startsWith(this.packageName)) {
				if (this.methodName == null) {
					// StartLine
					this.contentFile = ste.getFileName();
					this.methodName = ste.getMethodName();
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
		getLineFromThrowable();
		this.readFile();
	}

	private CharacterBuffer analyseLine(CharacterBuffer line) {
		line = line.trim();
		int pos = line.indexOf("//");
		if (pos >= 0) {
			line.withPosition(pos + 1);
			char character = line.getChar();
			if (character == '<') {
				line.skip();
				pos = line.position();
				while (character != '>' && character != 0) {
					character = line.getChar();
				}
				String variable = line.substring(pos, line.position());
				this.variables.add(variable, null);
				line.replace(pos - 3, line.position() + 1, TEMPLATESTART + variable + TEMPLATEEND);
			}
		}
		return line;
	}

	public void readFile() {
		fullPath = "src/test/java/" + this.packageName.replace('.', '/') + "/" + this.contentFile;
		int linePos = 1;
		FileBuffer fileBuffer = new FileBuffer();
		fileBuffer.withFile(fullPath);
		CharacterBuffer indexText = new CharacterBuffer();

		CharacterBuffer line = new CharacterBuffer();
		
		if(endLine == 0) {
			String search=this.methodName+"(";
			int start = this.startLine;
			while (line != null && linePos <= start) {
				line = fileBuffer.readLine();
				if(line.indexOf(search)>0) {
					this.startLine = linePos;
					break;
				}
				linePos++;
			}
			this.endLine = start;
			line = analyseLine(line);
			while (line != null) {
				indexText.with(line);
				line = analyseLine(fileBuffer.readLine());
				linePos++;
				if(linePos>=this.endLine && line.trim().equals("}")) {
					indexText.with(BaseItem.CRLF).with(line);
					break;
				}
				indexText.with(BaseItem.CRLF);
			}

			this.body = indexText;
			fileBuffer.close();
			return;
		}
		
		while (line != null && linePos <= this.startLine) {
			line = fileBuffer.readLine();
			linePos++;
		}
		// First Line
		line = analyseLine(line);

		if (this.format == null) {
			char firstChar = line.getCurrentChar();
			if (firstChar == '<') {
				this.format = FORMAT_XML;
			} else if (firstChar == '{' || firstChar == '[') {
				this.format = FORMAT_JSON;
			} else {
				this.format = FORMAT_JAVA;
			}
		}

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
	public boolean dump(Story story, HTMLEntity element) {
		XMLEntity pre = element.createBodyTag("pre");
		XMLEntity code = element.createBodyTag("code", pre);
		if(this.endLine<1 && this.startLine>0) {
			// Body is Empty add the full method
			readFile();
		}
		code.withValue(this.body);
		code.withKeyValue("class", this.format);
		code.withKeyValue("data-lang", this.format);

		XMLEntity undertitle = element.createBodyTag("div", pre);
		String value;
		String name;
		if (this.methodName.startsWith("test")) {
			name = this.methodName.substring(4);
		} else {
			name = this.methodName;
		}
		if (this.fullPath != null) {
			value = "Code: <a href=\"../" + this.fullPath + "\">" + name + "</a>";
		} else {
			value = "Code: " + name;
		}
		undertitle.with(value);
		undertitle.with("class", "title");

		XMLEntity table = element.createBodyTag("table");
		XMLEntity row;
		String key;
		for (int i = 0; i < this.variables.size(); i++) {
			key = this.variables.getKeyByIndex(i);
			value = this.variables.getValueByIndex(i);
			if (value == null) {
				System.err.println("Key: " + key + " has no value");
				continue;
			}
			row = element.createBodyTag("tr", table);
			code = element.createBodyTag("td", row);
			code.withValueItem(TEMPLATESTART + key + TEMPLATEEND);

			char charAt = value.charAt(0);
			if (charAt == '{' || charAt == '[') {
				code = element.createBodyTag("td.pre.code", row);
				code.withKeyValue("class", FORMAT_JSON);
				code.withKeyValue("data-lang", FORMAT_JSON);
			} else if (charAt == '<') {
				code = element.createBodyTag("td.pre.code", row);
				code.withKeyValue("class", FORMAT_XML);
				code.withKeyValue("data-lang", FORMAT_XML);
			} else {
				code = element.createBodyTag("td", row);
			}
			code.withValue(value);
		}
		return true;
	}

	public StoryStepSourceCode withCode(String path) {
		this.startStory(path);
		return this;
	}

	public StoryStepSourceCode withCode(Class<?> packageName) {
		String fileName = packageName.getName();
		int pos = fileName.lastIndexOf('.');
		if (pos > 0) {
			fileName = fileName.substring(0, pos);
		}
		this.startStory(fileName);
		return this;
	}

	public boolean addDescription(String key, String value) {
		int pos = this.variables.indexOf(key);
		if (pos >= 0) {
			this.variables.setValue(pos, value);
			return true;
		}
		return false;

	}
}
