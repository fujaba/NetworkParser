package de.uniks.networkparser.ext.story;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.EntityComparator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * The Class StoryStepSourceCode.
 *
 * @author Stefan
 */
public class StoryStepSourceCode implements ObjectCondition {
	
	/** The Constant FULL. */
	public static final int FULL = -1;
	
	/** The Constant CURRENTPOSITION. */
	public static final int CURRENTPOSITION = 0;
	
	/** The Constant FORMAT_JAVA. */
	public static final String FORMAT_JAVA = "java";
	
	/** The Constant FORMAT_XML. */
	public static final String FORMAT_XML = "xml";
	
	/** The Constant FORMAT_JSON. */
	public static final String FORMAT_JSON = "json";
	
	/** The Constant TEMPLATESTART. */
	public static final String TEMPLATESTART = "<i class=\"conum\" data-value=\"";
	
	/** The Constant TEMPLATEEND. */
	public static final String TEMPLATEEND = "\"></i>";
	private String format = null;
	private String contentFile;
	private int startLine = -1;
	private int currentLine = -1;
	private int endLine = -1;
	private CharacterBuffer body;
	private String packageName;
	private String methodName;
	private String methodSignature;
	private SimpleKeyValueList<String, String> variables = new SimpleKeyValueList<String, String>()
			.withComparator(EntityComparator.createComparator());
	private String fileName;
	private int stepOverPackageName;

	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	private String getLineFromThrowable() {
		if (this.packageName == null) {
			return null;
		}
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		String search;
		if (this.fileName != null) {
			search = this.packageName + "." + this.fileName;

		} else {
			search = this.packageName;
		}
		int equalsPos = -1;
		int startPos = -1;
		StackTraceElement ste;
		for (int i = 0; i < stackTrace.length; i++) {
			ste = stackTrace[i];
			String name = ste.getClassName();
			if (name.equals(search)) {
				equalsPos = i;
				break;
			} else if (startPos < 0 && name.startsWith(search)) {
				startPos = i;
			}
		}
		if (equalsPos < 0 && startPos >= 0) {
			equalsPos = startPos;
		}
		if (equalsPos >= 0) {
			/* Steps */
			if (stepOverPackageName > 0 && equalsPos + stepOverPackageName < stackTrace.length) {
				ste = stackTrace[equalsPos + stepOverPackageName];
			} else {
				ste = stackTrace[equalsPos];
			}
			if (this.methodName == null) {
				/* StartLine */
				/* REWRITE METHOD */
				String full = ste.getClassName();
				int pos = full.lastIndexOf('.');
				if (pos > 0) {
					this.fileName = full.substring(pos + 1);
					this.packageName = full.substring(0, pos);
				}else {
					this.fileName = full;
					this.packageName = "";
				}

				this.contentFile = "src/test/java/" + this.packageName.replace('.', '/') + "/" + ste.getFileName();
				this.methodName = ste.getMethodName();
				this.currentLine = ste.getLineNumber() + 1;
			} else {
				this.endLine = ste.getLineNumber() - 1;
			}
			String name = ste.getClassName();
			return name + ".java:" + ste.getLineNumber();
		}
		/* Argh not found */
		return "";
	}

	/**
	 * Gets the method name.
	 *
	 * @return the method name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Finish.
	 *
	 * @return true, if successful
	 */
	public boolean finish() {
		getLineFromThrowable();
		this.readFile();
		return true;
	}

	private CharacterBuffer analyseLine(CharacterBuffer line) {
		if (line == null) {
			return null;
		}
		line = line.rtrim();
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

	boolean checkEnd(int linePos, CharacterBuffer line, FileBuffer fileBuffer) {
		if (endLine < 0 && FORMAT_JAVA.equals(format)) {
			/* End of Method */
			return linePos >= this.endLine && line.equalsText('}');
		} else if (endLine > 0) {
			return (linePos > this.endLine);
		}
		if (fileBuffer == null) {
			return true;
		}
		return fileBuffer.isEnd();
	}

	/**
	 * Read file.
	 *
	 * @return true, if successful
	 */
	public boolean readFile() {
		FileBuffer fileBuffer = new FileBuffer();
		fileBuffer.withFile(contentFile);
		if (fileBuffer.exists() == false) {
			return false;
		}
		CharacterBuffer indexText = new CharacterBuffer();

		CharacterBuffer line = new CharacterBuffer();
		if (this.methodSignature != null) {
			startLine = -1;
			endLine = -1;
		}
		if (startLine == -1) {
			this.format = FORMAT_JAVA;
		} else {
			/* First Line */
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
		}
		int linePos = 1;
		if (FORMAT_JAVA.equals(this.format) && startLine == -1) {
			String search = this.methodName + "(";
			if (this.methodSignature != null) {
				search = this.methodSignature;
			}
			int start = this.currentLine;
			while (line != null && linePos <= start) {
				line = fileBuffer.readLine();
				if (line.indexOf(search) > 0) {
					this.startLine = linePos;
					break;
				}
				linePos++;
				if (fileBuffer.isEnd()) {
					break;
				}
			}
		}
		if (startLine == 0) {
			line = fileBuffer.readLine();
		}
		while (line != null && linePos < this.startLine) {
			line = fileBuffer.readLine();
			linePos++;
		}

		while (line != null) {
			indexText.with(formatString(line));
			line = analyseLine(fileBuffer.readLine());
			linePos++;
			if (checkEnd(linePos, line, fileBuffer)) {
				indexText.with(BaseItem.CRLF).with(line);
				break;
			} else if(fileBuffer.isEnd()) {
				break;
			}
			indexText.with(BaseItem.CRLF);
		}
		fileBuffer.close();
		this.body = indexText;
		return true;
	}

	String formatString(CharacterBuffer buffer) {
		if (buffer == null) {
			return "";
		}
		if (FORMAT_JAVA.equals(format)) {
			String string = buffer.toString();
			return string.replaceAll("<", "&lt;");
		}
		return buffer.toString();
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (value == null || value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		if (evt.getNewValue() instanceof HTMLEntity == false || evt.getSource() instanceof Story == false) {
			return false;
		}
		HTMLEntity element = (HTMLEntity) evt.getNewValue();
		Story story = (Story) evt.getSource();
		return addToHTML(story, element);
	}

	/**
	 * Adds the to HTML.
	 *
	 * @param element the element
	 * @return true, if successful
	 */
	public boolean addToHTML(HTMLEntity element) {
		return addToHTML(null, element);
	}

	/**
	 * Adds the to HTML.
	 *
	 * @param story the story
	 * @param element the element
	 * @return true, if successful
	 */
	public boolean addToHTML(Story story, HTMLEntity element) {
		if (element == null || element.getBody() == null) {
			return false;
		}
		if (story != null) {
			for (String item : HTMLEntity.CODE_RESOURCES) {
				if (element.getHeader(item) == null) {
					/* DEFAULT TO EXTRACT TO DOC-FOLDER */
					Story.addScript(story.getPath(), item, element);
				}
			}
			element.withScript(element.getHeader(), "hljs.initHighlightingOnLoad();", "hljs.initLineNumbersOnLoad();");
		}

		XMLEntity pre = element.createChild("pre", element.getBody());
		XMLEntity code = element.createChild("code", pre);
		if (this.endLine < 1 && this.currentLine > 0) {
			/* Body is Empty add the full method */
			readFile();
		}
		if (this.body == null) {
			return false;
		}
		code.withValueItem(this.body.toString());
		code.withKeyValue("class", this.format);
		code.withKeyValue("data-lang", this.format);

		XMLEntity undertitle = element.createChild("div", pre);
		String strValue;
		String name;
		if (this.methodName == null) {
			name = "Code";
		} else if (this.methodName.startsWith("test")) {
			name = this.methodName.substring(4);
		} else {
			name = this.methodName;
		}
		if (this.contentFile != null) {
			strValue = "Code: <a href=\"../" + this.contentFile + "\">" + name + "</a>";
		} else {
			strValue = "Code: " + name;
		}
		undertitle.with(strValue);
		undertitle.with("class", "title");

		XMLEntity table = element.createChild("table", element.getBody());
		XMLEntity row;
		String key;
		for (int i = 0; i < this.variables.size(); i++) {
			key = this.variables.getKeyByIndex(i);
			strValue = this.variables.getValueByIndex(i);
			if (strValue == null) {
				System.err.println("Key: " + key + " has no value");
				continue;
			}
			row = element.createChild("tr", table);
			code = element.createChild("td", row);
			code.withValueItem(TEMPLATESTART + key + TEMPLATEEND);

			char charAt = strValue.charAt(0);
			if (charAt == '{' || charAt == '[') {
				code = element.createChild("td.pre.code", row);
				code.withKeyValue("class", FORMAT_JSON);
				code.withKeyValue("data-lang", FORMAT_JSON);
			} else if (charAt == '<') {
				code = element.createChild("td.pre.code", row);
				code.withKeyValue("class", FORMAT_XML);
				code.withKeyValue("data-lang", FORMAT_XML);
			} else {
				code = element.createChild("td", row);
			}
			code.withValue(strValue);
		}
		return true;
	}

	/**
	 * With code.
	 *
	 * @param packageName the package name
	 * @param stepOver the step over
	 * @return the story step source code
	 */
	public StoryStepSourceCode withCode(Class<?> packageName, int stepOver) {
		if (packageName == null) {
			return this;
		}
		String packagePath = packageName.getName();
		String fileName = null;
		int pos = packagePath.lastIndexOf('.');
		if (pos > 0) {
			fileName = packagePath.substring(pos + 1);
			packagePath = packagePath.substring(0, pos);
		}
		this.packageName = packagePath;
		this.fileName = fileName;
		this.stepOverPackageName = stepOver;
		getLineFromThrowable();
		return this;
	}

	/**
	 * With code.
	 *
	 * @param packageName the package name
	 * @return the story step source code
	 */
	public StoryStepSourceCode withCode(Class<?> packageName) {
		withCode(packageName, 0);
		return this;
	}

	/**
	 * With code.
	 *
	 * @param path the path
	 * @param packageName the package name
	 * @return the story step source code
	 */
	public StoryStepSourceCode withCode(String path, Class<?> packageName) {
		if (packageName == null) {
			return this;
		}
		String fileName = packageName.getTypeName();

		this.contentFile = path + "/" + fileName.replace('.', '/') + ".java";
		this.currentLine = Integer.MAX_VALUE;
		return this;
	}

	/**
	 * Adds the description.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean addDescription(String key, String value) {
		int pos = this.variables.indexOf(key);
		if (pos >= 0) {
			this.variables.setValue(pos, value);
			return true;
		}
		return false;

	}

	/**
	 * With start.
	 *
	 * @param position the position
	 * @return the story step source code
	 */
	public StoryStepSourceCode withStart(int position) {
		if (position < -1) {
			this.startLine = -1;
		} else {
			this.startLine = position;
		}
		return this;
	}

	/**
	 * With end.
	 *
	 * @return the story step source code
	 */
	public StoryStepSourceCode withEnd() {
		return withEnd(0);
	}

	/**
	 * With end.
	 *
	 * @param position the position
	 * @return the story step source code
	 */
	public StoryStepSourceCode withEnd(int position) {
		if (position < -1) {
			this.endLine = -1;
		} else {
			if (position == 0 && startLine == 0 && this.currentLine > 0) {
				this.startLine = this.currentLine + 1;
				getLineFromThrowable();
				readFile();
			} else {
				this.endLine = position;
			}
		}
		return this;
	}

	/**
	 * Gets the method signature.
	 *
	 * @return the method signature
	 */
	public String getMethodSignature() {
		return methodSignature;
	}

	/**
	 * With method signature.
	 *
	 * @param value the value
	 * @return the story step source code
	 */
	public StoryStepSourceCode withMethodSignature(String value) {
		this.methodSignature = value;
		if (value != null) {
			int pos = this.methodSignature.indexOf("(");
			if (pos > 0) {
				this.methodName = this.methodSignature.substring(0, pos);
			}
		}
		return this;
	}

	/**
	 * With code.
	 *
	 * @param value the value
	 * @return the story step source code
	 */
	public StoryStepSourceCode withCode(String value) {
		this.body = new CharacterBuffer();
		this.body.with(value);
		return this;
	}
}
