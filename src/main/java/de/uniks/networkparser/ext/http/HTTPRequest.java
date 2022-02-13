package de.uniks.networkparser.ext.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.MapEntry;
import de.uniks.networkparser.list.SimpleEntity;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.StringEntry;
import de.uniks.networkparser.xml.HTMLEntity;

public class HTTPRequest implements Comparable<HTTPRequest> {
	public static final String HTTP_STATE_NOTFOUND = "HTTP 404";
	public static final String HTTP_STATE_OK = "HTTP/1.1 200 OK";
	public static final String HTTP_STATE_REDIRECT = "HTTP 302";
	public static final String HTTP_STATE_PERMISSION_DENIED = "HTTP 403";
	public static final String HTTP_CONTENT = "Content-Type";
	public static final String HTTP_LENGTH = "Content-Length";
	public static final String HTTP_REFRESH = "REFRESH";
	public static final String HTTP_CONTENT_HTML = "text/html";
	public static final String HTTP_CONTENT_PLAIN = "text/plain";
	public static final String HTTP_CONTENT_JSON = "application/json";
	public static final String HTTP_CONTENT_CSS = "text/css";
	public static final String HTTP_CONTENT_ICON = "image/x-icon";
	public static final String HTTP_CONTENT_FORM = "application/x-www-form-urlencoded";
	public static final String HTTP_CONTENT_MULTIFORM = "multipart/form-data;";
	public static final String HTTP_CHARSET = "charset=UTF-8";
	public static final String HTTP_DISPOSITION = "Content-Disposition:";
	public static final String HTTP_CONTENT_HEADER = "plainHeader";

    public static final String HTTP_TYPE_POST = "POST";
    public static final String HTTP_TYPE_GET = "GET";
    public static final String HTTP_TYPE_PUT = "PUT";
    public static final String HTTP_TYPE_PATCH = "PATCH";
    public static final String HTTP_TYPE_DELETE = "DELETE";
	
	public static final String BEARER = "Bearer";
	public static final String HTTP_AUTHENTIFICATION = "Authentification";
	public static final String STATIC = "S";
	public static final String VARIABLE = "V";

	private BufferedReader inputStream;
	private PrintWriter outputStream;
	private Condition<Exception> errorListener;
	private Condition<HTTPRequest> updateCondition;
	private Socket socket;
	private SimpleList<String> headers = new SimpleList<String>();

	private Map<String, Object> contentValues;
	private BaseItem content;
	private String contentType;

	private String http_Type; /* GET OR POST */

	private boolean writeHeader;
	private boolean writeBody;
	private SimpleList<MapEntry> part;
    private String url;
	private String tag;
	
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

	public HTTPRequest(String... params) {
	    if(params != null) {
	        if(params.length>0) {
	            this.url = params[0];
	        }
            if(params.length>1) {
                this.http_Type = params[1];
            }
	    }
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
	
	public boolean redirect(String url) {
		PrintWriter writer = getOutput();
		writer.println(HTTP_STATE_REDIRECT);
		writer.println("Location: "+url);
		writer.flush();
		return true;
	}
	
	public boolean writeHTTPResponse(String response, String... param) {
		PrintWriter writer = getOutput();
		String contentType = HTTP_CONTENT_HTML;
		if(param != null && param.length>0) {
			contentType = param[0];
		}
		writer.println(HTTP_STATE_OK);
		if(HTTP_CONTENT_HTML.equalsIgnoreCase(contentType)) {
			writer.println(HTTP_CONTENT + ": " + contentType + ";" + HTTP_CHARSET + ";");
		}else {
			writer.println(HTTP_CONTENT + ": " + contentType);
		}
		writer.println(HTTP_LENGTH + ": " +response.length());
		writer.write(BaseItem.CRLF);
		writer.print(response);
		writer.flush();
		return true;
	}
	
	
	
	public boolean close() {
		if (this.writeBody == false && this.content != null) {
			if(this.content instanceof HTMLEntity) {
				this.write((HTMLEntity)this.content);
			}else {
				writeBody(this.content.toString());
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
	
	public HTMLEntity getContentHTML() {
		if(content instanceof HTMLEntity) {
			return (HTMLEntity) content;
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
		this.part = new SimpleList<MapEntry>();
		if (defaultValue == null) {
			defaultValue = "";
		}
		if (input == null) {
			this.url = defaultValue;
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
					this.part.add(MapEntry.create(VARIABLE, value));
					part.clear();
					continue;
				}
				if (c == '/' && isFirst == false) {
					/* Split for / */
					if (isVariable) {
						if (part.startsWith(":")) {
							part.withStartPosition(1);
		                    this.part.add(MapEntry.create(VARIABLE, part.toString()));
							part.clear();
							continue;
						}
					} else {
						String value = part.toString();
						if ("*".equals(value)) {
                            this.part.add(MapEntry.create(VARIABLE, value));
						} else {
                            this.part.add(MapEntry.create(STATIC, value));
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
                    this.part.add(MapEntry.create(VARIABLE, value));
				} else {
					String value = part.toString();
					if ("*".equals(value)) {
					    this.part.add(MapEntry.create(VARIABLE, part.toString()));
					} else {
                        this.part.add(MapEntry.create(STATIC, value));
					}
				}
			}
		} catch (IOException e) {
			executeExeption(e);
		}
		if (buffer.charAt(0) == '/') {
			buffer.withStartPosition(1);
		}
		this.url = buffer.toString();
		return true;
	}

	public String getHttp_Type() {
		return http_Type;
	}

	public String getUrl() {
		return url;
	}

	public String getAbsolutePath(String... sub) {
		String result="";
		if(url != null) {
			if(url.startsWith("/")) {
				result = url;
			}else {
				result = "/"+url;
			}
		}
		if(sub != null && sub.length>0) {
			if(url.endsWith("/")) {
				result += sub[0];
			}else {
				result += "/"+sub[0];
			}
		}
		return result;
	}
	public HTTPRequest withURL(String value) {
		this.url = value;
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
			output.println(HTTP_STATE_OK);
			output.println(HTTP_CONTENT + ": " + HTTP_CONTENT_HTML + ";" + HTTP_CHARSET + ";");
			output.println(HTTP_LENGTH + ": "+ content.length());
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
				output.println(HTTP_STATE_OK);
				output.println(HTTP_CONTENT + ": " + HTTP_CONTENT_HTML + ";" + HTTP_CHARSET + ";");
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
				output.println(HTTP_LENGTH + ": " + len);
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
					    pos++; // SKIP ":"
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
				this.withHeader(HTTP_LENGTH + ": " + length);
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
				this.content = new CharacterBuffer().with(entry.toString());
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

	public SimpleKeyValueList<String, Object> parseForm() {
	    SimpleKeyValueList<String, Object> contentValueResult = new SimpleKeyValueList<String, Object>();
		contentValues = contentValueResult;
		if (HTTP_CONTENT_FORM.equals(this.contentType) && this.content instanceof CharacterBuffer) {
			CharacterBuffer buffer = new CharacterBuffer();
			CharacterBuffer source = (CharacterBuffer) this.content;
			char c;
			String key = null;
			for (int i = 0; i < source.length(); i++) {
				c = source.charAt(i);
				if (c == '=') {
					key = buffer.toString();
					buffer.clear();
					continue;
				}
				if (c == '&') {
					contentValues.put(key, buffer.toString());
					buffer.clear();
					continue;
				}
				buffer.with(c);
			}
			if (buffer.length() > 0) {
				contentValues.put(key, buffer.toString());
			}
		}
		return contentValueResult;
	}
	
	public String getContentValue(String key) {
		return ""+contentValues.get(key);
	}

	public HTTPRequest withHeader(String value) {
		if (value != null) {
			value = value.trim();
			if (value.length() > 0) {
				if (value.startsWith(HTTP_CONTENT)) {
					this.contentType = value.substring(HTTP_CONTENT.length() + 2);
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

	public BaseItem getContentElement() {
        return content;
    }
	public String getContent() {
	    if(content == null) {
	        return null;
	    }
		return content.toString();
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

	public HTTPRequest withContent(String... values) {
		if (content == null) {
			content = new CharacterBuffer();
		}
		if (values != null && content instanceof CharacterBuffer) {
			CharacterBuffer sb = (CharacterBuffer) content;
			for (String item : values) {
				if (item != null) {
					sb.append(item);
				}
			}
		}
		return this;
	}
	
	public HTTPRequest withContentForm(String splitter, Map<String, Object> values) {
	    this.contentValues = values;
	    this.contentType = HTTP_CONTENT_MULTIFORM+" boundary="+splitter;
	    return this;
	}
	
	public HTTPRequest withContent(BaseItem entity) {
	    content = entity;
		return this;
	}


	public boolean match(HTTPRequest routing) {
		this.matchOfRequestPath = 0;
		this.matchValid = false;
		this.matchVariables.clear();

		if (routing == null || part == null) {
			return false;
		}
		SimpleList<MapEntry> paths = routing.getPathParts();
		for (matchOfRequestPath = 0; matchOfRequestPath < this.part.size(); matchOfRequestPath++) {
			MapEntry item = this.part.get(matchOfRequestPath);
			String type = item.getKey();
			String currentPathpart = item.getValueString();
			if (HTTPRequest.STATIC.equals(type)) {
				if (paths.size() < matchOfRequestPath) {
					break;
				}
				/* Must be the Same */
				if (!currentPathpart.equalsIgnoreCase(paths.get(matchOfRequestPath).getValueString())) {
					break;
				}
			} else if (HTTPRequest.VARIABLE.equals(type)) {
				/* NEW ONE */
				this.matchVariables.put(paths.get(matchOfRequestPath).getValueString(), currentPathpart);
			}
		}
		matchValid = matchOfRequestPath == paths.size();
		return matchValid;
	}

	private SimpleList<MapEntry> getPathParts() {
        return part;
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

    public HTTPRequest withType(String value) {
        this.http_Type = value;
        return this;
    }

    public CharacterBuffer getMultiContent() {
        if(contentValues == null || !(contentType != null && contentType.startsWith(HTTP_CONTENT_MULTIFORM))) {
            return null;
        } 
        int pos = contentType.indexOf("boundary=");
        if(pos<1) {
        	return null;
        }
        String splitter = contentType.substring(pos+9);
        CharacterBuffer content = new CharacterBuffer();
        for(Iterator<Entry<String, Object>> iterator = contentValues.entrySet().iterator();iterator.hasNext();) {
            Entry<String, Object> item = iterator.next();
            Object element = item.getValue();
            if(element == null) {
                continue;
            }
            content.withLine("--"+splitter);
            if (element instanceof JsonArray || element instanceof JsonObject) {
                content.withLine(HTTP_DISPOSITION+" form-data; name=\"" + item.getKey() + "\"; filename=\"" + item.getKey() +".json\"");
                content.withLine(HTTP_CONTENT + ": " + HTTP_CONTENT_JSON);
                content.with(BaseItem.CRLF);
                content.withLine(element.toString());
            } else if (element instanceof BaseItem) {
                content.withLine(HTTP_DISPOSITION + " form-data; name=\"" + item.getKey() + "\"; filename=\"" + item.getKey()+".txt\"");
                content.with(BaseItem.CRLF);
                content.withLine(element.toString());
            } else {
                content.withLine(HTTP_DISPOSITION+" form-data; name=\""+item.getKey()+"\"");
                content.withLine(element.toString());
            }
        }
        content.withLine("--"+splitter+"--");
        return content;
    }

    public HTTPRequest withContent(String bodyType, Object[] params) {
        if(params == null || params.length % 2 == 1) {
            return this;
        }
        this.contentType = bodyType;
        this.contentValues = new SimpleKeyValueList<String, Object>();
        for(int i=0;i<params.length;i+=2) {
             this.contentValues.put(""+ params[i], params[i+1]);
        }
        return this;
    }
    public HTTPRequest withContent(String bodyType, Map<String, Object> values) {
    	this.contentType = bodyType;
    	this.contentValues = values;
    	return this;
    }
    
    public boolean isValidContentType() {
        return (HTTP_CONTENT_PLAIN.equalsIgnoreCase(contentType) || 
        		HTTP_CONTENT_JSON.equalsIgnoreCase(contentType) ||
        		HTTP_CONTENT_HEADER.equalsIgnoreCase(contentType));
    }

	public String getContentType() {
		return contentType;
	}

	public Map<String, Object> getContentValues() {
		return contentValues;
	}

	public HTTPRequest withContentType(String value) {
		this.contentType = value;
		return this;
	}
	
	@Override
	public String toString() {
	    if(this.url != null) {
	        return this.url;
	    }
	    return super.toString();
	}

	public HTTPRequest withTag(String value) {
	    this.tag = value;
	    return this;
	}
	
	public String getTag() {
        return tag;
    }
}
