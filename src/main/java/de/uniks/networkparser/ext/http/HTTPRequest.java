package de.uniks.networkparser.ext.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;

public class HTTPRequest implements Comparable<HTTPRequest> {
	public static final String HTTP__NOTFOUND = "HTTP 404";
	public static final String HTTP_OK = "HTTP/1.1 200 OK";
	public static final String HTTP_PERMISSION_DENIED = "HTTP 403";
	public static final String HTTP_CONTENT = "Content-Type:";
	public static final String HTTP_AUTHENTIFICATION = "Authentification";
	public static final String HTTP_REFRESH = "REFRESH";
	public static final String HTTP_CONTENT_HTML = "text/html";
	public static final String HTTP_CONTENT_CSS = "text/css";
	public static final String HTTP_CONTENT_ICON = "image/x-icon";
	public static final String HTTP_CHARSET = "charset=UTF-8";
	public static final String HTTP_CONTENT_FORM = "application/x-www-form-urlencoded";
	public static final String HTTP_LENGTH = "Content-Length:";
	public static final String BEARER = "Bearer";
	public static final Character STATIC = 'S';
	public static final Character VARIABLE = 'V';

	private BufferedReader inputStream;
	private PrintWriter outputStream;
	private Condition<Exception> errorListener;
	private Condition<HTTPRequest> updateCondition;
	private Socket socket;
	private String path;
	private SimpleList<String> headers = new SimpleList<String>();
	private SimpleKeyValueList<String, String> contentValues;
	private String content;

	private String http_Type; /* GET OR POST */
	private String contentType;

	private boolean writeHeader;
	private boolean writeBody;
	private SimpleList<String> partParameter;
	private SimpleList<String> partPath;
	private SimpleList<String> fullPath;
	private SimpleList<Character> pathType;
	private BaseItem bufferResponse;

	private SimpleKeyValueList<String, String> matchVariables = new SimpleKeyValueList<String, String>();
	private boolean matchValid = true;
	private int matchOfRequestPath;

	public void executeExeption(Exception e) {
		if (errorListener != null) {
			errorListener.update(e);
		} else {
			e.printStackTrace();
		}
	}

	HTTPRequest() {
	}

	private HTTPRequest(Socket socket) {
		this.socket = socket;
	}

	public BufferedReader getInput() {
		if (socket != null && this.inputStream == null) {
			try {
				this.inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				executeExeption(e);
			}
		}
		return inputStream;
	}

	public String readType() {
		this.http_Type = readTo(' ');
		return http_Type;
	}

	public String readTo(char splitStr) {
		int c;
		CharacterBuffer buffer = new CharacterBuffer();
		try {
			BufferedReader input = getInput();
			if (input != null) {
				while ((c = getInput().read()) != -1) {
					if (c == ' ') {
						break;
					}
					buffer.with((char) c);
				}
			}
		} catch (IOException e) {
			executeExeption(e);
		}
		return buffer.toString();
	}

	public PrintWriter getOutput() {
		if (this.socket != null && this.outputStream == null) {
			try {
				this.outputStream = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				executeExeption(e);
			}
		}
		return outputStream;
	}
	
	public HTTPRequest write(CharSequence value) {
		getOutput().append(value);
		return this;
	}
	
	public boolean writeHTTPResponse(String response, String... param) {
		PrintWriter writer = getOutput();
		String contentType = HTTP_CONTENT_HTML;
		if(param != null || param.length>0) {
			contentType = param[0];
		}
		writer.println(HTTP_OK);
		if(HTTP_CONTENT_HTML.equalsIgnoreCase(contentType)) {
			writer.println(HTTP_CONTENT + " " + contentType + ";" + HTTP_CHARSET + ";");
		}else {
			writer.println(HTTP_CONTENT + " " + contentType);
		}
		writer.println(HTTP_LENGTH + response.length());
		writer.write(BaseItem.CRLF);
		writer.print(response);
		writer.flush();
		return false;
	}
	
	
	
	public boolean close() {
		if (this.writeBody == false && this.bufferResponse != null) {
			if(this.bufferResponse instanceof HTMLEntity) {
				this.write((HTMLEntity)this.bufferResponse);
			}else {
				writeBody(this.bufferResponse.toString());
			}
		}
		if (outputStream != null) {
			outputStream.close();
		}
		if (this.socket != null) {
			try {
				this.socket.close();
			} catch (IOException e) {
				executeExeption(e);
				return false;
			}
		}
		return true;
	}

	public static HTTPRequest create(Socket socket) {
		return new HTTPRequest(socket);
	}
	
	public HTMLEntity getHTMLEntity() {
		if(bufferResponse instanceof HTMLEntity) {
			return (HTMLEntity) bufferResponse;
		}
		return null;
	}

	public static HTTPRequest createRouting(String value) {
		HTTPRequest httpRequest = new HTTPRequest();
		if (value != null) {
			StringReader stringReader = new StringReader(value);
			httpRequest.parsingPath(stringReader, "*");
		}
		return httpRequest;
	}

	public void readPath() {
		BufferedReader input = getInput();
		parsingPath(input, null);
	}

	/**
	 * Parsing Path
	 *
	 * bub/bla * bub/:id blub?id=1&name=bla
	 * 
	 * @param input        Reader
	 * @param defaultValue default Fallback
	 * @return success
	 */
	private boolean parsingPath(Reader input, String defaultValue) {
		this.partPath = new SimpleList<String>();
		this.partParameter = new SimpleList<String>();
		this.pathType = new SimpleList<Character>();
		this.fullPath = new SimpleList<String>();
		if (defaultValue == null) {
			defaultValue = "";
		}
		if (input == null) {
			this.path = defaultValue;
			return false;
		}
		CharacterBuffer buffer = new CharacterBuffer();
		int c;
		CharacterBuffer part = new CharacterBuffer();
		boolean isVariable = false;
		try {
			boolean isFirst = true;
			while ((c = input.read()) != -1) {
				if (c == ' ') {
					break;
				}
				buffer.with((char) c);
				/* Check for Paramter */
				if (c == ':' && part.length() == 0) {
					isVariable = true;
				}
				if (c == '?' && isVariable == false) {
					isVariable = true;
				}
				if (c == '&' && isVariable) {
					part.withStartPosition(1);
					String value = part.toString();
					this.fullPath.add(value);
					this.partParameter.add(value);
					this.pathType.add(VARIABLE);
					part.clear();
					continue;
				}
				if (c == '/' && isFirst == false) {
					/* Split for / */
					if (isVariable) {
						if (part.startsWith(":")) {
							part.withStartPosition(1);
							String value = part.toString();
							this.fullPath.add(value);
							this.partParameter.add(part.toString());
							this.pathType.add(VARIABLE);
							part.clear();
							continue;
						}
					} else {
						String value = part.toString();
						this.fullPath.add(value);
						if ("*".equals(value)) {
							this.partParameter.add(value);
							this.pathType.add(VARIABLE);
						} else {
							this.partPath.add(value);
							this.pathType.add(STATIC);
						}
					}
					part.clear();
					continue;
				}
				part.with((char) c);
				isFirst = false;
			}
			if (part.length() > 0) {
				if (isVariable) {
					part.withStartPosition(1);
					String value = part.toString();
					this.fullPath.add(value);
					this.partParameter.add(value);
					this.pathType.add(VARIABLE);
				} else {
					String value = part.toString();
					this.fullPath.add(value);
					if ("*".equals(value)) {
						this.partParameter.add(part.toString());
						this.pathType.add(VARIABLE);
					} else {
						this.partPath.add(value);
						this.pathType.add(STATIC);
					}
				}
			}
		} catch (IOException e) {
			executeExeption(e);
		}
		if (buffer.charAt(0) == '/') {
			buffer.withStartPosition(1);
		}
		this.path = buffer.toString();
		return true;
	}

	public String getHttp_Type() {
		return http_Type;
	}

	public String getPath() {
		return path;
	}

	public HTTPRequest withPath(String value) {
		this.path = value;
		return this;
	}

	public boolean write(HTMLEntity entity) {
		if (entity == null) {
			return false;
		}
		String content = entity.toString();
		PrintWriter output = getOutput();
		if (output != null) {
			this.writeHeader = true;
			this.writeBody = true;
			output.println(HTTP_OK);
			output.println(HTTP_CONTENT + " " + HTTP_CONTENT_HTML + ";" + HTTP_CHARSET + ";");
			output.println(HTTP_LENGTH + content.length());
			output.write(BaseItem.CRLF);
			output.print(content);
			output.flush();
			return true;
		}
		return false;
	}

	public boolean writeHeader(String... header) {
		PrintWriter output = getOutput();
		if (output != null) {
			if (this.writeHeader == false) {
				output.println(HTTP_OK);
				output.println(HTTP_CONTENT + " " + HTTP_CONTENT_HTML + ";" + HTTP_CHARSET + ";");
				this.writeHeader = true;
			}
			if (header != null) {
				for (String item : header) {
					if (item != null) {
						output.println(item);
					}
				}
			}
		}
		return true;
	}

	public boolean writeBody(String... body) {
		PrintWriter output = getOutput();
		if (output != null) {
			if (this.writeHeader == false) {
				this.writeHeader();
			}
			if (body != null) {
				int len = 0;
				for (String item : body) {
					len += item.length() + 2;
				}
				output.println(HTTP_LENGTH + len);
				output.write(BaseItem.CRLF);
				for (String item : body) {
					output.println(item);
				}
				output.flush();
				this.writeHeader = true;
			}
			return true;
		}
		return false;
	}

	public boolean readHeader() {
		BufferedReader input = getInput();
		if (input == null) {
			return false;
		}
		int c, pos = 0;
		boolean isEnd = false;
		CharacterBuffer buffer = new CharacterBuffer();
		int length = 0;
		try {
			while ((c = input.read()) != -1) {
				if (c == 10 && buffer.size() < 1) {
					continue;
				}
				if (c == HTTP_LENGTH.charAt(pos)) {
					pos++;
					if (pos == HTTP_LENGTH.length()) {
						length = 1;
						break;
					}
				} else {
					pos = 0;
				}
				if (c == 13) {
					String value = buffer.toString();
					if (value.length() < 1) {
						isEnd = true;
						break;
					}
					this.withHeader(value);
					buffer.clear();
				} else {
					buffer.with((char) c);
				}
			}
			if (length < 1) {
				this.withHeader(buffer.toString());
			} else {
				length = 0;
				while ((c = input.read()) != -1) {
					if (c == ' ') {
						continue;
					}
					if (c >= '0' && c <= '9') {
						length = length * 10 + c - '0';
					} else {
						break;
					}
				}
				this.withHeader(HTTP_LENGTH + " " + length);
				if (c == 13) {
					c = input.read();
				}
			}
			if (isEnd == false) {
				String line;
				do {
					line = input.readLine();
					this.withHeader(line);
				} while (line != null && line.trim().length() > 0);
			}
			if (length > 0) {
				char[] item = new char[length];
				while ((c = input.read()) != -1) {
					if (c != 13 && c != 10 && c != ' ') {
						break;
					}
				}
				item[0] = (char) c;
				input.read(item, 1, item.length - 1);
				CharacterBuffer entry = new CharacterBuffer();
				entry.with(item, 0, item.length);
				this.content = entry.toString();
			}
		} catch (IOException e) {
			executeExeption(e);
		}
		return true;
	}

	public boolean isWriteBody() {
		return writeBody;
	}

	public boolean isWriteHeader() {
		return writeHeader;
	}

	public SimpleKeyValueList<String, String> parseForm() {
		contentValues = new SimpleKeyValueList<String, String>();
		if (HTTP_CONTENT_FORM.equals(this.contentType) && this.content != null) {
			CharacterBuffer buffer = new CharacterBuffer();
			char c;
			String key = null;
			for (int i = 0; i < this.content.length(); i++) {
				c = this.content.charAt(i);
				if (c == '=') {
					key = buffer.toString();
					buffer.clear();
					continue;
				}
				if (c == '&') {
					contentValues.add(key, buffer.toString());
					buffer.clear();
					continue;
				}
				buffer.with(c);
			}
			if (buffer.length() > 0) {
				contentValues.add(key, buffer.toString());
			}
		}
		return contentValues;
	}
	
	public String getContentValue(String key) {
		return contentValues.get(key);
	}

	public HTTPRequest withHeader(String value) {
		if (value != null) {
			value = value.trim();
			if (value.length() > 0) {
				if (value.startsWith(HTTP_CONTENT)) {
					this.contentType = value.substring(HTTP_CONTENT.length() + 1);
				}
				this.headers.add(value);
			}
		}
		return this;
	}

	public String getHeader(String filter) {
		if (filter == null) {
			return null;
		}
		for (String item : headers) {
			if (item != null && item.startsWith(filter)) {
				return item;
			}
		}
		return null;
	}

	public String getContent() {
		return content;
	}
	
	public HTTPRequest parse() {
		readHeader();
		parseForm();
		return this;
	}

	public HTTPRequest withExceptionListener(Condition<Exception> value) {
		this.errorListener = value;
		return this;
	}

	public boolean writeCookie(String key, String value, int expriration) {
		PrintWriter output = getOutput();
		if (output != null) {
			output.println("Set-Cookie: " + key + "=" + value + "; Max-Age=" + expriration);
			return true;
		}
		return false;
	}

	public SimpleList<String> getPathParts() {
		return this.partPath;
	}

	public SimpleList<String> getPathParameter() {
		return this.partParameter;
	}

	public SimpleList<Character> getPathType() {
		return pathType;
	}

	public int getMatchOfRequestPath() {
		return matchOfRequestPath;
	}

	public boolean isValid() {
		return matchValid;
	}

	@Override
	public int compareTo(HTTPRequest o) {
		if (o == null) {
			return 1;
		}
		if (isValid() && o.isValid() == false) {
			return 1;
		}
		if (isValid() == false && o.isValid()) {
			return -1;
		}
		if (o.getMatchOfRequestPath() < this.matchOfRequestPath) {
			return 1;
		}
		if (this.matchOfRequestPath > o.getMatchOfRequestPath()) {
			return -1;
		}
		return 0;
	}

	public HTTPRequest withBufferResponse(String... values) {
		if (bufferResponse == null) {
			bufferResponse = new CharacterBuffer();
		}
		if (values != null && bufferResponse instanceof CharacterBuffer) {
			CharacterBuffer sb = (CharacterBuffer) bufferResponse;
			for (String item : values) {
				if (item != null) {
					sb.append(item);
				}
			}
		}
		return this;
	}
	
	public HTTPRequest withBufferResponse(HTMLEntity entity) {
		bufferResponse = entity;
		return this;
	}


	public boolean match(HTTPRequest routing) {
		this.matchOfRequestPath = 0;
		this.matchValid = false;
		this.matchVariables.clear();

		if (routing == null || pathType == null) {
			return false;
		}
		SimpleList<String> paths = routing.getFullPath();
		for (matchOfRequestPath = 0; matchOfRequestPath < this.pathType.size(); matchOfRequestPath++) {
			Character type = this.pathType.get(matchOfRequestPath);
			String currentPathpart = this.fullPath.get(getMatchOfRequestPath());
			if (HTTPRequest.STATIC.equals(type)) {
				if (paths.size() < matchOfRequestPath) {
					break;
				}
				/* Must be the Same */
				if (currentPathpart.equalsIgnoreCase(paths.get(matchOfRequestPath)) == false) {
					break;
				}
			} else if (HTTPRequest.VARIABLE.equals(type)) {
				/* NEW ONE */
				this.matchVariables.put(paths.get(matchOfRequestPath), currentPathpart);
			}
		}
		matchValid = matchOfRequestPath == paths.size();
		return true;
	}

	public HTTPRequest withUpdateCondition(Condition<HTTPRequest> condition) {
		this.updateCondition = condition;
		return this;
	}

	public boolean update(HTTPRequest value) {
		if (updateCondition != null) {
			return updateCondition.update(value);
		}
		return false;
	}

	public SimpleList<String> getFullPath() {
		return fullPath;
	}
}
