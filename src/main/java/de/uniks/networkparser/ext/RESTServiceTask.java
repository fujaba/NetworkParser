package de.uniks.networkparser.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

import de.uniks.networkparser.Deep;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.xml.XMLEntity;

public class RESTServiceTask implements Runnable {
	private int port;
	public static final String PROPERTY_ERROR="error";
	public static final String PROPERTY_ALLOW="allow";
	private ServerSocket serverSocket;
	private IdMap map;
	private Object root;
	private SendableEntityCreator creator;
	private Filter filter = Filter.regard(Deep.create(1));
	
	private Condition<Exception> errorListener;
	private Condition<Socket> allowListener;
	public static final String JSON="/json";
	public static final String XML="/xml";

	public RESTServiceTask(int port, IdMap map, Object root) {
		super();
		this.port = port;
		this.map = map;
		this.root = root;
		creator = map.getCreatorClass(root);
	}

	public RESTServiceTask withErrorListener(Condition<Exception> listener) {
		this.errorListener = listener;
		return this;
	}
	public RESTServiceTask withAllowListener(Condition<Socket> listener) {
		this.allowListener = listener;
		return this;
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(this.port);
			char[] buffer = new char[30];
			CharacterBuffer type = new CharacterBuffer();
			CharacterBuffer request = new CharacterBuffer();
			boolean isType;
			int pos;
			while(serverSocket != null) {
				try {
					Socket clientSocket = serverSocket.accept();
					BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					int read = br.read(buffer);
					isType = true;
					type.clear();
					request.clear();
					for(pos = 0;pos<read;pos++) {
						if(buffer[pos] == ' ') {
							if(isType) {
								isType = false;
							} else {
								break;
							}
						} else if(isType){
							type.with(buffer[pos]);
						} else {
							request.with(buffer[pos]);
						}
						if(pos == read) {
							read = br.read(buffer);
							pos = 0;
						}

					}
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					if(allowListener != null) {
						SocketRequest socketRequest = new SocketRequest();
						socketRequest.socket = clientSocket;
						socketRequest.type = type;
						socketRequest.requst = request;
						if(allowListener.equals(socketRequest) == false) {
							out.write("HTTP 403");
							out.close();
							clientSocket.close();
							continue;
						}
					}
					pos = 0;
					boolean isJSON = false;
					boolean isXML = false;
					if(request.startsWith(JSON, 0, false)) {
						pos =6;
						isJSON = true;
					} else if(request.startsWith(XML, 0, false)) {
						pos = 5;
						isXML = true;
					}
					CharacterBuffer path = new CharacterBuffer();
					SendableEntityCreator creator = this.creator;
					int listPos = -1;
					Object element = root;
					while(pos<request.length()) {
						if(request.charAt(pos) == '[') {
							listPos=0;
							pos++;
							while(pos<request.length() && request.charAt(pos)!=']') {
								listPos=listPos*10 + (request.charAt(pos) - 48);
								pos++;
							}
							pos++;
						}

						if(request.charAt(pos) != '/' && pos<request.length()) {
							path.with(request.charAt(pos));
							pos++;
						}
						if(request.charAt(pos) == '/' || (pos==request.length() && path.length()>0)) {
							element = creator.getValue(element, path.toString());
							if(element == null) {
								break;
							}
							// Switch For List
							// listPos
							if(listPos>=0 && element instanceof Collection<?>) {
								Collection<?> collection = (Collection<?>) element;
								Object[] array = collection.toArray();
								if(listPos <array.length) {
									element = array[listPos];
								}
							}
							creator = map.getCreatorClass(element);
							path.clear();
							listPos = -1;
							pos++;
						}
					}
					
					if(element != null) {
						if(isJSON) {
							if(element instanceof Collection<?>) {
								JsonArray jsonArray = map.toJsonArray(element, filter);	
								out.write(jsonArray.toString());
								
							} else {
								JsonObject jsonObject = map.toJsonObject(element, filter);	
								out.write(jsonObject.toString());
							}
						} else if(isXML) {
							XMLEntity xml = map.toXMLEntity(element, filter);
							out.write(xml.toString());
						}
					} else {
						 out.write("HTTP 400");
					}
					out.close();
					clientSocket.close();
				}catch (Exception e) {
					if(errorListener != null) {
						errorListener.update(e);
					}
				}
			}
		}catch (Exception e) {
			if(errorListener != null) {
				errorListener.update(e);
			}
		}
	}
	
	public void stop() {
		if(serverSocket!=null) {
			try {
				ServerSocket socket = serverSocket;
				serverSocket = null;
				socket.close();
			} catch (IOException e) {
				if(errorListener != null) {
					errorListener.update(e);
				}
			}
		}
	}
}
