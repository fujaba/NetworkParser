package de.uniks.networkparser.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;

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
	private Condition<SocketRequest> allowListener;
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
	public RESTServiceTask withAllowListener(Condition<SocketRequest> listener) {
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
					int temp = br.read(buffer);
					isType = true;
					type.clear();
					request.clear();
					for(pos = 0;pos<temp;) {
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
						pos++;
						if(pos == temp) {
							temp = br.read(buffer);
							pos = 0;
						}

					}
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					if(allowListener != null) {
						SocketRequest socketRequest = new SocketRequest();
						socketRequest.socket = clientSocket;
						socketRequest.type = type;
						socketRequest.requst = request;
						if(allowListener.update(socketRequest) == false) {
							out.write("HTTP 403");
							out.close();
							clientSocket.close();
							continue;
						}
					}
					String result = this.executeRequest(request);
					out.write(result);
					out.close();
					clientSocket.close();
				}catch (Exception e) {
					if(errorListener != null) {
						errorListener.update(e);
					}else {
						e.printStackTrace();
					}
				}
			}
		}catch (Exception e) {
			if(errorListener != null) {
				errorListener.update(e);
			}
		}
	}
	public String executeRequest(String request) {
		return executeRequest(new CharacterBuffer().with(request));
	}
	public String executeRequest(CharacterBuffer request) {
		int pos = 0;
		boolean isXML = false;
		if(request.startsWith(JSON, 0, false)) {
			pos =6;
		} else if(request.startsWith(XML, 0, false)) {
			pos = 5;
			isXML = true;
		}
		CharacterBuffer path = new CharacterBuffer();
		SendableEntityCreator creator = this.creator;
		CharacterBuffer listID = new CharacterBuffer();
		Object element = root;
		while(pos<request.length()) {
			if(request.charAt(pos) == '[') {
				listID.clear();
				pos++;
				while(pos<request.length() && request.charAt(pos)!=']') {
					listID.with(request.charAt(pos));
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
				if(element instanceof Collection<?>) {
					int temp;
					String id = listID.toString();
					try {
						temp = Integer.parseInt(id);
					} catch (NumberFormatException e) {
						temp = -1;
					}
					
					Collection<?> collection = (Collection<?>) element;
					if(temp<0) {
						Object item = null;
						Iterator<?> i = collection.iterator();
						if(id.length()>0){
							element = null;
							while(i.hasNext()) {
								item = i.next();
								if(id.equals(map.getId(item, true))) {
									element = item;
									break;
								}
							}
						}else if(collection.size() == 1) {
							element = i.next();
						}
					}else {
						element = null;
						int collectionPos = 0;
						for(Iterator<?> i = collection.iterator();i.hasNext();) {
							if(collectionPos==temp) {
								element = i.next();
								break;
							} else {
								i.next();
								collectionPos++;
							}
						}
					}
				}
				creator = map.getCreatorClass(element);
				path.clear();
				listID.clear();
				pos++;
			}
		}
		if(element == root) {
			if(path.length()>0) {
				element = null;
			} else if(listID.length()>0) {
				element = map.getObject(listID.toString());
			}
		}
		if(element != null) {
			if(isXML) {
				XMLEntity xml = map.toXMLEntity(element, filter);
				return xml.toString();
			}
			if(element instanceof Collection<?>) {
				JsonArray jsonArray = map.toJsonArray(element, filter);	
				return jsonArray.toString();
				
			} else {
				JsonObject jsonObject = map.toJsonObject(element, filter);	
				return jsonObject.toString();
			}
		}
		return "HTTP 400";
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
