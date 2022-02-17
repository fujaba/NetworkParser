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
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;

/**
 * The Class HTTPRequest.
 *
 * @author Stefan
 */
public class HTTPRequest implements Comparable<HTTPRequest> {
	
	/** The Constant HTTP_STATE_NOTFOUND. */
	public static final String HTTP_STATE_NOTFOUND = "HTTP 404";
	
	/** The Constant HTTP_STATE_OK. */
	public static final String HTTP_STATE_OK = "HTTP/1.1 200 OK";
	
	/** The Constant HTTP_STATE_REDIRECT. */
	public static final String HTTP_STATE_REDIRECT = "HTTP 302";
	
	/** The Constant HTTP_STATE_PERMISSION_DENIED. */
	public static final String HTTP_STATE_PERMISSION_DENIED = "HTTP 403";
	
	/** The Constant HTTP_CONTENT. */
	public static final String HTTP_CONTENT = "Content-Type";
	
	/** The Constant HTTP_LENGTH. */
	public static final String HTTP_LENGTH = "Content-Length";
	
	/** The Constant HTTP_REFRESH. */
	public static final String HTTP_REFRESH = "REFRESH";
	
	/** The Constant HTTP_CONTENT_HTML. */
	public static final String HTTP_CONTENT_HTML = "text/html";
	
	/** The Constant HTTP_CONTENT_PLAIN. */
	public static final String HTTP_CONTENT_PLAIN = "text/plain";
	
	/** The Constant HTTP_CONTENT_JSON. */
	public static final String HTTP_CONTENT_JSON = "application/json";
	
	/** The Constant HTTP_CONTENT_CSS. */
	public static final String HTTP_CONTENT_CSS = "text/css";
	
	/** The Constant HTTP_CONTENT_ICON. */
	public static final String HTTP_CONTENT_ICON = "image/x-icon";
	
	/** The Constant HTTP_CONTENT_FORM. */
	public static final String HTTP_CONTENT_FORM = "application/x-www-form-urlencoded";
	
	/** The Constant HTTP_CONTENT_MULTIFORM. */
	public static final String HTTP_CONTENT_MULTIFORM = "multipart/form-data;";
	
	/** The Constant HTTP_CHARSET. */
	public static final String HTTP_CHARSET = "charset=UTF-8";
	
	/** The Constant HTTP_DISPOSITION. */
	public static final String HTTP_DISPOSITION = "Content-Disposition:";
	
	/** The Constant HTTP_CONTENT_HEADER. */
	public static final String HTTP_CONTENT_HEADER = "plainHeader";

    /** The Constant HTTP_TYPE_POST. */
    public static final String HTTP_TYPE_POST = "POST";
    
    /** The Constant HTTP_TYPE_GET. */
    public static final String HTTP_TYPE_GET = "GET";
    
    /** The Constant HTTP_TYPE_PUT. */
    public static final String HTTP_TYPE_PUT = "PUT";
    
    /** The Constant HTTP_TYPE_PATCH. */
    public static final String HTTP_TYPE_PATCH = "PATCH";
    
    /** The Constant HTTP_TYPE_DELETE. */
    public static final String HTTP_TYPE_DELETE = "DELETE";
	
	/** The Constant BEARER. */
	public static final String BEARER = "Bearer";
	
	/** The Constant HTTP_AUTHENTIFICATION. */
	public static final String HTTP_AUTHENTIFICATION = "Authentification";
	
	/** The Constant STATIC. */
	public static final String STATIC = "S";
	
	/** The Constant VARIABLE. */
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
	private boolean parsing = false;
	private int matchOfRequestPath;

	/**
	 * Execute exeption.
	 *
	 * @param e the e
	 */
	public void executeExeption(Exception e) {
		if (errorListener != null) {
			errorListener.update(e);
		} else {
			e.printStackTrace();
		}
	}

	/**
	 * Instantiates a new HTTP request.
	 *
	 * @param params the params
	 */
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

	/**
	 * Gets the input.
	 *
	 * @return the input
	 */
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

	/**
	 * Read type.
	 *
	 * @return the string
	 */
	public String readType() {
		this.http_Type = readTo(' ');
		return http_Type;
	}

	/**
	 * Read to.
	 *
	 * @param splitStr the split str
	 * @return the string
	 */
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

	/**
	 * Gets the output.
	 *
	 * @return the output
	 */
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
	
	/**
	 * Write.
	 *
	 * @param value the value
	 * @return the HTTP request
	 */
	public HTTPRequest write(CharSequence value) {
		getOutput().append(value);
		return this;
	}
	
	/**
	 * Redirect.
	 *
	 * @param url the url
	 * @return true, if successful
	 */
	public boolean redirect(String url) {
		PrintWriter writer = getOutput();
		writer.println(HTTP_STATE_REDIRECT);
		writer.println("Location: "+url);
		writer.flush();
		return true;
	}
	
	/**
	 * Write HTTP response.
	 *
	 * @param response the response
	 * @param param the param
	 * @return true, if successful
	 */
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
	
	
	
	/**
	 * Close.
	 *
	 * @return true, if successful
	 */
	public boolean close() {
		if (!this.writeBody && this.content != null) {
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

	/**
	 * Creates the.
	 *
	 * @param socket the socket
	 * @return the HTTP request
	 */
	public static HTTPRequest create(Socket socket) {
		return new HTTPRequest(socket);
	}
	
	/**
	 * Gets the content HTML.
	 *
	 * @return the content HTML
	 */
	public HTMLEntity getContentHTML() {
		if(content instanceof HTMLEntity) {
			return (HTMLEntity) content;
		}
		return null;
	}

	/**
	 * Creates the routing.
	 *
	 * @param value the value
	 * @return the HTTP request
	 */
	public static HTTPRequest createRouting(String value) {
		HTTPRequest httpRequest = new HTTPRequest();
		if (value != null) {
			StringReader stringReader = new StringReader(value);
			httpRequest.parsingPath(stringReader, "*");
		}
		return httpRequest;
	}

	/**
	 * Read path.
	 */
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
				if (c == '?' && !isVariable) {
					isVariable = true;
				}
				if (c == '&' && isVariable) {
					part.withStartPosition(1);
					String value = part.toString();
					this.part.add(MapEntry.create(VARIABLE, value));
					part.clear();
					continue;
				}
				if (c == '/' && !isFirst) {
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

	/**
	 * Gets the http type.
	 *
	 * @return the http type
	 */
	public String getHttp_Type() {
		return http_Type;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Gets the absolute path.
	 *
	 * @param sub the sub
	 * @return the absolute path
	 */
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
	
	/**
	 * With URL.
	 *
	 * @param value the value
	 * @return the HTTP request
	 */
	public HTTPRequest withURL(String value) {
		this.url = value;
		return this;
	}

	/**
	 * Write.
	 *
	 * @param entity the entity
	 * @return true, if successful
	 */
	public boolean write(HTMLEntity entity) {
		if (entity == null) {
			return false;
		}
		String content = entity.toString();
		PrintWriter output = getOutput();
		if (output != null && !this.writeBody) {
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

	/**
	 * Write header.
	 *
	 * @param header the header
	 * @return true, if successful
	 */
	public boolean writeHeader(String... header) {
		PrintWriter output = getOutput();
		if (output != null) {
			if (!this.writeHeader) {
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

	/**
	 * Write body.
	 *
	 * @param body the body
	 * @return true, if successful
	 */
	public boolean writeBody(String... body) {
		PrintWriter output = getOutput();
		if (output != null) {
			if (!this.writeHeader) {
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

	/**
	 * Read header.
	 *
	 * @return true, if successful
	 */
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
					    input.read();  // SKIP ":"
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
					input.read();
				}
			}
			if (!isEnd) {
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

	/**
	 * Checks if is write body.
	 *
	 * @return true, if is write body
	 */
	public boolean isWriteBody() {
		return writeBody;
	}

	/**
	 * Checks if is write header.
	 *
	 * @return true, if is write header
	 */
	public boolean isWriteHeader() {
		return writeHeader;
	}

	/**
	 * Parses the form.
	 *
	 * @return the simple key value list
	 */
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
	
	/**
	 * Gets the content value.
	 *
	 * @param key the key
	 * @return the content value
	 */
	public String getContentValue(String key) {
	    if(contentValues == null) {
	        return null;
	    }
	    Object value = contentValues.get(key);
	    if(value instanceof String) {
	        return (String)value;
	    }
		return null;
	}

	/**
	 * With header.
	 *
	 * @param value the value
	 * @return the HTTP request
	 */
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

	/**
	 * Gets the header.
	 *
	 * @param filter the filter
	 * @return the header
	 */
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

	/**
	 * Gets the content element.
	 *
	 * @return the content element
	 */
	public BaseItem getContentElement() {
        return content;
    }
	
	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent() {
	    if(content == null) {
	        return null;
	    }
		return content.toString();
	}
	
	/**
	 * Parses the.
	 *
	 * @return the HTTP request
	 */
	public HTTPRequest parse() {
	    if(!parsing) {
    		readHeader();
    		parseForm();
    		parsing = true;
	    }
		return this;
	}

	/**
	 * With exception listener.
	 *
	 * @param value the value
	 * @return the HTTP request
	 */
	public HTTPRequest withExceptionListener(Condition<Exception> value) {
		this.errorListener = value;
		return this;
	}

	/**
	 * Write cookie.
	 *
	 * @param key the key
	 * @param value the value
	 * @param expriration the expriration
	 * @return true, if successful
	 */
	public boolean writeCookie(String key, String value, int expriration) {
		PrintWriter output = getOutput();
		if (output != null) {
			output.println("Set-Cookie: " + key + "=" + value + "; Max-Age=" + expriration);
			return true;
		}
		return false;
	}

	/**
	 * Gets the match of request path.
	 *
	 * @return the match of request path
	 */
	public int getMatchOfRequestPath() {
		return matchOfRequestPath;
	}

	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	public boolean isValid() {
		return matchValid;
	}

	/**
	 * Compare to.
	 *
	 * @param o the o
	 * @return the int
	 */
	@Override
	public int compareTo(HTTPRequest o) {
		if (o == null) {
			return 1;
		}
		if (isValid() && !o.isValid()) {
			return 1;
		}
		if (!isValid() && o.isValid()) {
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

	/**
	 * With content.
	 *
	 * @param values the values
	 * @return the HTTP request
	 */
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
	
	/**
	 * With content form.
	 *
	 * @param splitter the splitter
	 * @param values the values
	 * @return the HTTP request
	 */
	public HTTPRequest withContentForm(String splitter, Map<String, Object> values) {
	    this.contentValues = values;
	    this.contentType = HTTP_CONTENT_MULTIFORM+" boundary="+splitter;
	    return this;
	}
	
	/**
	 * With content.
	 *
	 * @param entity the entity
	 * @return the HTTP request
	 */
	public HTTPRequest withContent(BaseItem entity) {
	    content = entity;
		return this;
	}


	/**
	 * Match.
	 *
	 * @param routing the routing
	 * @return true, if successful
	 */
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
				MapEntry match = paths.get(matchOfRequestPath);
				if(match == null) {
				    break;
				}
				/* Must be the Same */
				if (!currentPathpart.equalsIgnoreCase(match.getValueString())) {
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

    /**
     * With update condition.
     *
     * @param condition the condition
     * @return the HTTP request
     */
    public HTTPRequest withUpdateCondition(Condition<HTTPRequest> condition) {
		this.updateCondition = condition;
		return this;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean update(HTTPRequest value) {
		if (updateCondition != null) {
			return updateCondition.update(value);
		}
		return false;
	}

    /**
     * With type.
     *
     * @param value the value
     * @return the HTTP request
     */
    public HTTPRequest withType(String value) {
        this.http_Type = value;
        return this;
    }

    /**
     * Gets the multi content.
     *
     * @return the multi content
     */
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

    /**
     * With content.
     *
     * @param bodyType the body type
     * @param params the params
     * @return the HTTP request
     */
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
    
    /**
     * With content.
     *
     * @param bodyType the body type
     * @param values the values
     * @return the HTTP request
     */
    public HTTPRequest withContent(String bodyType, Map<String, Object> values) {
    	this.contentType = bodyType;
    	this.contentValues = values;
    	return this;
    }
    
    /**
     * Checks if is valid content type.
     *
     * @return true, if is valid content type
     */
    public boolean isValidContentType() {
        return (HTTP_CONTENT_PLAIN.equalsIgnoreCase(contentType) || 
        		HTTP_CONTENT_JSON.equalsIgnoreCase(contentType) ||
        		HTTP_CONTENT_HEADER.equalsIgnoreCase(contentType));
    }

	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Gets the content values.
	 *
	 * @return the content values
	 */
	public Map<String, Object> getContentValues() {
		return contentValues;
	}

	/**
	 * With content type.
	 *
	 * @param value the value
	 * @return the HTTP request
	 */
	public HTTPRequest withContentType(String value) {
		this.contentType = value;
		return this;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
	    if(this.url != null) {
	        return this.url;
	    }
	    return super.toString();
	}

	/**
	 * With tag.
	 *
	 * @param value the value
	 * @return the HTTP request
	 */
	public HTTPRequest withTag(String value) {
	    this.tag = value;
	    return this;
	}
	
	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	public String getTag() {
        return tag;
    }
}
