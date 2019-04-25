package de.uniks.networkparser.ext.petaf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;

public class HTTPRequest {
	public static final String HTTP__NOTFOUND = "HTTP 404";
	public static final String HTTP_OK = "HTTP/1.1 200 OK";
	public static final String HTTP_PERMISSION_DENIED = "HTTP 403";
	public static final String HTTP_CONTENT = "Content-Type:";
	public static final String HTTP_AUTHENTIFICATION = "Authentification";
	public static final String HTTP_REFRESH = "REFRESH";
	public static final String HTTP_CONTENT_HTML = "text/html";
	public static final String HTTP_CHARSET = "charset=UTF-8";
	public static final String HTTP_CONTENT_FORM = "application/x-www-form-urlencoded";
	public static final String HTTP_LENGTH = "Content-Length:";
	public static final String BEARER = "Bearer";

	private BufferedReader inputStream;
	private PrintWriter outputStream;
	private Condition<Exception> errorListener;
	private Socket socket;
	private String http_Type;
	private String path;
	private SimpleList<String> headers=new SimpleList<String>();
	private String content;
	private String contentType;
	
	public void executeExeption(Exception e) {
		if (errorListener != null) {
			errorListener.update(e);
		} else {
			e.printStackTrace();
		}

	}
	private HTTPRequest(Socket socket) {
		this.socket = socket;
	}
	
	public BufferedReader getInput() {
		if(socket != null && this.inputStream == null) {
			try {
				this.inputStream = new BufferedReader (new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				executeExeption(e);
			}
		}
		return inputStream;
	}
	
	public String readType() {
		this.http_Type= readTo(' ');
		return http_Type;
	}
	
	public String readTo(char splitStr) {
		int c;
		CharacterBuffer buffer = new CharacterBuffer();
		try {
			while ((c = getInput().read()) != -1) {
				if (c == ' ') {
					break;
				}
				buffer.with((char) c);
			}
		} catch (IOException e) {
			executeExeption(e);
		}
		return buffer.toString();
	}
	
	public PrintWriter getOutput() {
		if(this.socket != null && this.outputStream == null) {
			try {
				this.outputStream = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				executeExeption(e);
			}
		}
		return outputStream;
	}
	
	public boolean close() {
		if(outputStream != null) {
			outputStream.close();
		}
		if(this.socket != null) {
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

	public void readPath() {
		int c;
		CharacterBuffer buffer = new CharacterBuffer();
		BufferedReader input = getInput();
		if(input != null) {
			try {
				while ((c = input.read()) != -1) {
					if (c == ' ') {
						break;
					}
					buffer.with((char) c);
				}
			} catch (IOException e) {
				executeExeption(e);
			}
			if (buffer.charAt(0) == '/') {
				buffer.withStartPosition(1);
			}
		}
		this.path = buffer.toString();
	}
	
	public String getHttp_Type() {
		return http_Type;
	}
	public String getPath() {
		return path;
	}

	public boolean write(String value) {
		PrintWriter output = getOutput();
		if(output != null) {
			output.write(value);
			return true;
		}
		return false;
	}
	public boolean write(HTMLEntity entity) {
		String content = entity.toString();
		PrintWriter output = getOutput();
		if(output != null) {
			output.println(HTTP_OK);
			output.println(HTTP_CONTENT+" "+HTTP_CONTENT_HTML+";"+HTTP_CHARSET+";");
			output.println(HTTP_LENGTH + content.length());
			output.write(BaseItem.CRLF);
			output.print(content);
			output.flush();
			return true;
		}
		return false;
	}
	
	public boolean readHeader() {
		BufferedReader input = getInput();
		if(input == null) {
			return false;
		}
		int c, pos = 0;
		boolean isEnd = false;
		CharacterBuffer buffer= new CharacterBuffer();
		int length = 0;
		try {
			while ((c = input.read()) != -1) {
				if(c==10 && buffer.size()<1) {
					continue;
				}
				if (c == HTTP_LENGTH.charAt(pos)) {
					pos++;
					if (pos == HTTP_LENGTH.length()) {
						length= 1;
						break;
					}
				} else {
					pos = 0;
				}
				if(c == 13) {
					String value = buffer.toString();
					if(value.length()<1) {
						isEnd = true;
						break;
					}
					this.withHeader(value);
					buffer.clear();
				} else {
					buffer.with((char) c);
				}
			}
			if(length < 1) {
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
				this.withHeader(HTTP_LENGTH+" " + length);
				if(c == 13) {
					c = input.read();
				}
			}
			if(isEnd == false) {
				String line;
				do {
					line = input.readLine();
					this.withHeader(line);
				}while(line != null && line.trim().length()>0);
			}
			if(length>0)  {
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
	
	public SimpleKeyValueList<String, String> parseForm() {
		SimpleKeyValueList<String, String> value = new SimpleKeyValueList<String, String>();
		if(HTTP_CONTENT_FORM.equals(this.contentType) && this.content != null) {
			CharacterBuffer buffer=new CharacterBuffer();
			char c;
			String key = null;
			for(int i=0;i<this.content.length();i++) {
				c= this.content.charAt(i);
				if(c == '=') {
					key=buffer.toString();
					buffer.clear();
					continue;
				}
				if(c == '&') {
					value.add(key, buffer.toString());
					buffer.clear();
					continue;
				}
				buffer.with(c);
			}
			if(buffer.length()>0) {
				value.add(key, buffer.toString());
			}
		}
		return value;
	}
	
	public HTTPRequest withHeader(String value) {
		if(value != null) {
			value  = value.trim();
			if(value.length()>0) {
				if(value.startsWith(HTTP_CONTENT)) {
					this.contentType = value.substring(HTTP_CONTENT.length()+1);
				}
				this.headers.add(value);
			}
		}
		return this;
	}
	
	public String getContent() {
		return content;
	}
	public HTTPRequest withExceptionListener(Condition<Exception> value) {
		this.errorListener = value;
		return this;
	}
}
