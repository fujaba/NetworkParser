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
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.Server;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.XMLEntity;

public class RESTServiceTask implements Runnable, Server{
	private int port;
	public static final String ERROR404="HTTP 404";
	public static final String OK="HTTP 200";
	public static final String PROPERTY_ERROR="error";
	public static final String PROPERTY_ALLOW="allow";
	public static final String LENGTH = "Content-Length:";
	private ServerSocket serverSocket;
	private IdMap map;
	private Object root;
	private SendableEntityCreator creator;
	private Filter filter = Filter.regard(Deep.create(1));

	private Condition<Exception> errorListener;
	private Condition<SimpleEvent> allowListener;
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
	public RESTServiceTask withAllowListener(Condition<SimpleEvent> listener) {
		this.allowListener = listener;
		return this;
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(this.port);
			CharacterBuffer buffer = new CharacterBuffer();
			while(serverSocket != null) {
				try {
					Socket clientSocket = serverSocket.accept();

					buffer.clear();

					InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
					BufferedReader br = new BufferedReader(isr);
					int c;
					while ((c = br.read()) != -1) {
						if(c == ' ') {
							break;
						}
						buffer.with((char) c);
					}
					String type = buffer.toString();
					buffer.clear();
					while ((c = br.read()) != -1) {
						if(c == ' ') {
							break;
						}
						buffer.with((char) c);
					}
					if(buffer.charAt(0)=='/') {
						buffer.withStartPosition(1);
					}
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					SimpleEvent event=new SimpleEvent(clientSocket, buffer.toString(), br, out);
					event.withType(type);

					if(allowListener != null) {
						if(allowListener.update(event) == false) {
							out.write("HTTP 403");
							out.close();
							clientSocket.close();
							continue;
						}
					}
					String result = this.executeRequest(event);
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

	@Override
	public boolean close() {
		if( serverSocket == null ||  serverSocket.isClosed()) {
			return true;
		}
		try{
			serverSocket.close();
		}catch (Exception e) {
		}
		return true;
	}

	@Override
	public boolean isRun() {
		return serverSocket != null && serverSocket.isClosed() == false;
	}

	public String executeRequest(String request) {
		SimpleEvent event = new SimpleEvent(this, request, null, null);
		return executeRequest(event);
	}
	public String executeRequest(SimpleEvent socketRequest) {
		if(socketRequest == null) {
			return null;
		}
		String type = socketRequest.getType();
		if(NodeProxyTCP.GET.equalsIgnoreCase(type) || "".equals(type) || SendableEntityCreator.NEW.equalsIgnoreCase(type)) {
			return this.getExecute(socketRequest);
		}
		if(NodeProxyTCP.POST.equalsIgnoreCase(type)) {
			return this.postExecute(socketRequest);
		}
		if(NodeProxyTCP.PUT.equalsIgnoreCase(type)) {
			return this.putExecute(socketRequest);
		}
		if(NodeProxyTCP.PATCH.equalsIgnoreCase(type)) {
			return this.patchExecute(socketRequest);
		}
		if(NodeProxyTCP.DELETE.equalsIgnoreCase(type)) {
			return this.deleteExecute(socketRequest);
		}
		return null;
	}

	private Object getElement(CharacterBuffer request, CharacterBuffer path, CharacterBuffer listID, boolean lastElement) {
		int pos = 0;
		SendableEntityCreator creator = this.creator;

		if(request.startsWith(JSON, 0, false)) {
			pos =6;
		} else if(request.startsWith(XML, 0, false)) {
			pos = 5;
		}
		Object element = root;
		Object last = root;
		CharacterBuffer oldPath=new CharacterBuffer();
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
				last = element;
				oldPath.set(path.toString());
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
									last = element;
									oldPath.set(path.toString());
									element = item;
									break;
								}
							}
						}else if(collection.size() == 1) {
							last = element;
							oldPath.set(path.toString());
							element = i.next();
						}
					}else {
						last = element;
						oldPath.set(path.toString());
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
		if(lastElement) {
			path.set(oldPath.toString());
			return last;
		}
		return element;
	}


	//GET
	// Read	200 (OK)
	// 404 (Not Found) if ID not found or invalid
	private String getExecute(SimpleEvent socketRequest) {
		CharacterBuffer path = new CharacterBuffer();
		CharacterBuffer listID = new CharacterBuffer();
		CharacterBuffer request = new CharacterBuffer().with(socketRequest.getPropertyName());
		Object element = getElement(request, path, listID, false);
		if(element == root) {
			if(path.length()>0) {
				element = null;
			} else if(listID.length()>0) {
				element = map.getObject(listID.toString());
			}
		}
		boolean isXML = false;
		if(request.startsWith(JSON, 0, false)) {
		} else if(request.startsWith(XML, 0, false)) {
			isXML = true;
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
		return ERROR404;
	}

	// DELETE
	// Delete 405 (Method Not Allowed)
	// 200 (OK)
	// 404 (Not Found), if ID not found or invalid.
	private String deleteExecute(SimpleEvent socketRequest) {
		CharacterBuffer path = new CharacterBuffer();
		CharacterBuffer listID = new CharacterBuffer();
		CharacterBuffer request = new CharacterBuffer().with(socketRequest.getPropertyName());
		Object element = getElement(request, path, listID, false);
		if(element != null) {
			map.removeObj(element, true);
			return OK;
		}
		return ERROR404;
	}

	//POST
	// Create 200 (Created)
	// 404 (Not Found)
	// 409 (Conflict) if resource already exists..
	private String postExecute(SimpleEvent socketRequest) {
		if(socketRequest == null) {
			return ERROR404;
		}
		try {
			Object source = socketRequest.getSource();
			if(source instanceof Socket == false) {
				return ERROR404;
			}
			BufferedReader br = (BufferedReader) socketRequest.getOldValue();
			String propertyName = socketRequest.getPropertyName();

			CharacterBuffer path = new CharacterBuffer();
			CharacterBuffer listID = new CharacterBuffer();
			CharacterBuffer request = new CharacterBuffer().with(socketRequest.getPropertyName());
			Object element = getElement(request, path, listID, true);

			// First item SWITCH
			CharacterBuffer buffer = new CharacterBuffer();
			int c = br.read();
			int pos=0;
			boolean found = false;

			while ((c = br.read()) != -1) {
				if(c == LENGTH.charAt(pos)) {
					pos++;
					if(pos==LENGTH.length()) {
						found = true;
						break;
					}
				} else {
					pos =0;
				}
				buffer.with((char)  c);
			}
			int length = 0;
			if(found) {
				while ((c = br.read()) != -1) {
					if(c==' ') {
						continue;
					}
					if(c >='0' && c<='9') {
						length = length*10 + c-'0';
					}else {
						break;
					}
				}
				char[] item = new char[length];
				while ((c = br.read()) != -1) {
					if(c != 13 && c != 10 && c != ' ') {
						break;
					}

				}
				item[0] = (char) c;
				br.read(item, 1, item.length - 1);
				CharacterBuffer entry = new CharacterBuffer();
				entry.with(item, 0 , item.length);

				if(entry.charAt(0) == JsonObject.START || entry.charAt(0) == XMLEntity.START) {
					// JsonObject or XMLEntity
					Object child = map.decode(entry);
					SendableEntityCreator creator = map.getCreatorClass(element);
					if(creator != null) {
						creator.setValue(element, propertyName, child, SendableEntityCreator.NEW);
					}
					return OK;
				} else {
					// PLAIN KEY VALUE
					SimpleKeyValueList<String, String> child = new SimpleKeyValueList<String, String>().withKeyValueString(entry.toString(), String.class);
					String className = child.getString(IdMap.CLASS);
					SendableEntityCreator childCreator = map.getCreator(className, false);
					Object childValue = childCreator.getSendableInstance(false);
					for(int i=0;i<child.size();i++) {
						String key = child.getKeyByIndex(i);
						if(IdMap.CLASS.equalsIgnoreCase(key)) {
							continue;
						}
						childCreator.setValue(childValue, child.getKeyByIndex(i), child.getValueByIndex(i), SendableEntityCreator.NEW);
					}
					creator.setValue(element, propertyName, childValue, SendableEntityCreator.NEW);
					return OK;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ERROR404;
	}

	//PUT
	// Update/Replace 405 (Method Not Allowed)
    // 200 (OK) or 204 (No Content)
	// 404 (Not Found), if ID not found or invalid.
	private String putExecute(SimpleEvent socketRequest) {
		return ERROR404;
	}

	//PATCH	Update/Modify
	// 405 (Method Not Allowed), unless you want to modify the collection itself.
	// 200 (OK) or 204 (No Content).
	// 404 (Not Found), if ID not found or invalid.
	private String patchExecute(SimpleEvent socketRequest) {
		return ERROR404;
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
