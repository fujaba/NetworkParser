package de.uniks.networkparser.ext;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.Iterator;

import de.uniks.networkparser.Deep;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.http.HTTPRequest;
import de.uniks.networkparser.ext.http.LoginService;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.Server;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedList;
import de.uniks.networkparser.xml.XMLEntity;

public class RESTServiceTask implements Runnable, Server {
	private int port;
	public static final String PROPERTY_ERROR = "error";
	public static final String PROPERTY_ALLOW = "allow";
	private ServerSocket serverSocket;
	private IdMap map;
	private Object root;
	private SendableEntityCreator creator;
	private boolean routingExists;
	private Filter filter = Filter.regard(Deep.create(1));

	private Condition<Exception> errorListener;
	private Condition<SimpleEvent> allowListener;
	private Condition<SimpleEvent> loginController;
	public static final String JSON = "/json";
	public static final String XML = "/xml";
	private SimpleList<HTTPRequest> routing;

	public RESTServiceTask(int port, IdMap map, Object root) {
		super();
		this.port = port;
		this.map = map;
		this.root = root;
		if (map != null) {
			creator = map.getCreatorClass(root);
		}
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
			if (port == 0) {
				return;
			}
			serverSocket = new ServerSocket(this.port);
			while (serverSocket != null) {
				HTTPRequest clientSocket = HTTPRequest.create(serverSocket.accept());
				clientSocket.withExceptionListener(this.errorListener);

				clientSocket.readType();
				clientSocket.readPath();

				System.out.println(clientSocket.getHttp_Type() + ": " + clientSocket.getPath());

				HTTPRequest match = null;

				if (routing != null && routing.size() > 0) {
					/* Parsing Path */
					HTTPRequest defaultMatch = null;
					String path = clientSocket.getPath();

					SortedList<HTTPRequest> matches = new SortedList<HTTPRequest>(true);
					for (int i = 0; i < routing.size(); i++) {
						HTTPRequest key = routing.get(i);
						if ("*".equals(key.getPath())) {
							defaultMatch = key;
						}
						if (clientSocket.match(key)) {
							matches.add(key);
						}
					}
					if (matches.size() > 0) {
						HTTPRequest first = matches.first();
						/* *  */
						if ((routingExists && first.isValid()) || routingExists == false) {
							match = first;
						} else if (path.indexOf("/") < 1) {
							match = defaultMatch;
						}
					}
				}
				/* SO NEW MATCHES */
				SimpleEvent event = new SimpleEvent(clientSocket, clientSocket.getPath(), null, null);
				if (allowListener != null) {
					if (allowListener.update(event) == false) {
						clientSocket.writeHeader(HTTPRequest.HTTP_PERMISSION_DENIED);
						clientSocket.close();
						continue;
					}
				} else if (loginController != null) {
					if (loginController.update(event) == false) {
						clientSocket.close();
						continue;
					}
				}
				/* So Valid and Execute Match or default 
				   CHECK FOR NEXT VALID OR BEST */
				if (match != null) {
					match.update(clientSocket);
					clientSocket.close();
					continue;
				}
				String result = this.executeRequest(event);
				clientSocket.writeBody(result);
				clientSocket.close();
			}
		} catch (Exception e) {
			if (errorListener != null) {
				errorListener.update(e);
			}
		}
	}

	@Override
	public boolean close() {
		if (serverSocket == null || serverSocket.isClosed()) {
			return true;
		}
		try {
			serverSocket.close();
		} catch (Exception e) {
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
		if (socketRequest == null) {
			return null;
		}
		String type = socketRequest.getType();
		if (NodeProxyTCP.GET.equalsIgnoreCase(type) || "".equals(type)
				|| SendableEntityCreator.NEW.equalsIgnoreCase(type)) {
			return this.getExecute(socketRequest);
		}
		if (NodeProxyTCP.POST.equalsIgnoreCase(type)) {
			return this.postExecute(socketRequest);
		}
		if (NodeProxyTCP.PUT.equalsIgnoreCase(type)) {
			return this.putExecute(socketRequest);
		}
		if (NodeProxyTCP.PATCH.equalsIgnoreCase(type)) {
			return this.patchExecute(socketRequest);
		}
		if (NodeProxyTCP.DELETE.equalsIgnoreCase(type)) {
			return this.deleteExecute(socketRequest);
		}
		return null;
	}

	private Object getElement(CharacterBuffer request, CharacterBuffer path, CharacterBuffer listID,
			boolean lastElement) {
		int pos = 0;
		SendableEntityCreator creator = this.creator;

		if (request.startsWith(JSON, 0, false)) {
			pos = 6;
		} else if (request.startsWith(XML, 0, false)) {
			pos = 5;
		}
		Object element = root;
		Object last = root;
		CharacterBuffer oldPath = new CharacterBuffer();
		while (pos < request.length()) {
			if (request.charAt(pos) == '[') {
				listID.clear();
				pos++;
				while (pos < request.length() && request.charAt(pos) != ']') {
					listID.with(request.charAt(pos));
					pos++;
				}
				pos++;
			}

			if (request.charAt(pos) != '/' && pos < request.length()) {
				path.with(request.charAt(pos));
				pos++;
			}
			if (request.charAt(pos) == '/' || (pos == request.length() && path.length() > 0)) {
				last = element;
				oldPath.set(path.toString());
				element = creator.getValue(element, path.toString());
				if (element == null) {
					break;
				}
				/* Switch For List */
				if (element instanceof Collection<?>) {
					int temp;
					String id = listID.toString();
					try {
						temp = Integer.parseInt(id);
					} catch (NumberFormatException e) {
						temp = -1;
					}

					Collection<?> collection = (Collection<?>) element;
					if (temp < 0) {
						Object item = null;
						Iterator<?> i = collection.iterator();
						if (id.length() > 0) {
							element = null;
							while (i.hasNext()) {
								item = i.next();
								if (id.equals(map.getId(item, true))) {
									last = element;
									oldPath.set(path.toString());
									element = item;
									break;
								}
							}
						} else if (collection.size() == 1) {
							last = element;
							oldPath.set(path.toString());
							element = i.next();
						}
					} else {
						last = element;
						oldPath.set(path.toString());
						element = null;
						int collectionPos = 0;
						for (Iterator<?> i = collection.iterator(); i.hasNext();) {
							if (collectionPos == temp) {
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
		if (lastElement) {
			path.set(oldPath.toString());
			return last;
		}
		return element;
	}

	/* GET
	   Read 200 (OK)
	   404 (Not Found) if ID not found or invalid */
	private String getExecute(SimpleEvent socketRequest) {
		CharacterBuffer path = new CharacterBuffer();
		CharacterBuffer listID = new CharacterBuffer();
		CharacterBuffer request = new CharacterBuffer().with(socketRequest.getPropertyName());
		Object element = getElement(request, path, listID, false);
		if (element == root) {
			if (path.length() > 0) {
				element = null;
			} else if (listID.length() > 0) {
				element = map.getObject(listID.toString());
			}
		}
		boolean isXML = false;
		if (request.startsWith(JSON, 0, false)) {
		} else if (request.startsWith(XML, 0, false)) {
			isXML = true;
		}
		if (element != null) {
			if (isXML) {
				XMLEntity xml = map.toXMLEntity(element, filter);
				return xml.toString();
			}
			if (element instanceof Collection<?>) {
				JsonArray jsonArray = map.toJsonArray(element, filter);
				return jsonArray.toString();

			} else {
				JsonObject jsonObject = map.toJsonObject(element, filter);
				return jsonObject.toString();
			}
		}
		return HTTPRequest.HTTP__NOTFOUND;
	}

	/** DELETE
	 *  Delete 405 (Method Not Allowed)
	 *  200 (OK)
	 *  404 (Not Found), if ID not found or invalid.
	 * @param socketRequest The SocketRequest
	 * @return Response
	 */
	private String deleteExecute(SimpleEvent socketRequest) {
		CharacterBuffer path = new CharacterBuffer();
		CharacterBuffer listID = new CharacterBuffer();
		CharacterBuffer request = new CharacterBuffer().with(socketRequest.getPropertyName());
		Object element = getElement(request, path, listID, false);
		if (element != null) {
			map.removeObj(element, true);
			return HTTPRequest.HTTP_OK;
		}
		return HTTPRequest.HTTP__NOTFOUND;
	}

	/** POST
	 *  Create 200 (Created)
	 *  404 (Not Found)
	 *  409 (Conflict) if resource already exists..
  	 *  @param socketRequest The SocketRequest
	 *  @return Response
	 */
	private String postExecute(SimpleEvent socketRequest) {
		if (socketRequest == null) {
			return HTTPRequest.HTTP__NOTFOUND;
		}
		Object source = socketRequest.getSource();
		if (source instanceof HTTPRequest == false) {
			return HTTPRequest.HTTP__NOTFOUND;
		}
		HTTPRequest request = (HTTPRequest) source;

		CharacterBuffer path = new CharacterBuffer();
		CharacterBuffer listID = new CharacterBuffer();
		CharacterBuffer pathValue = new CharacterBuffer().with(request.getPath());
		Object element = getElement(pathValue, path, listID, true);

		/* First item SWITCH */
		String body = request.getContent();
		if (body.charAt(0) == JsonObject.START || body.charAt(0) == XMLEntity.START) {
			/* JsonObject or XMLEntity */
			Object child = map.decode(body);
			SendableEntityCreator creator = map.getCreatorClass(element);
			if (creator != null) {
				creator.setValue(element, pathValue.toString(), child, SendableEntityCreator.NEW);
			}
			return HTTPRequest.HTTP_OK;
		}
		/* PLAIN KEY VALUE */
		SimpleKeyValueList<String, String> child = new SimpleKeyValueList<String, String>().withKeyValueString(body,
				String.class);
		String className = child.getString(IdMap.CLASS);
		SendableEntityCreator childCreator = map.getCreator(className, false);
		Object childValue = childCreator.getSendableInstance(false);
		for (int i = 0; i < child.size(); i++) {
			String key = child.getKeyByIndex(i);
			if (IdMap.CLASS.equalsIgnoreCase(key)) {
				continue;
			}
			childCreator.setValue(childValue, child.getKeyByIndex(i), child.getValueByIndex(i),
					SendableEntityCreator.NEW);
		}
		creator.setValue(element, pathValue.toString(), childValue, SendableEntityCreator.NEW);
		return HTTPRequest.HTTP_OK;
	}

	/** PUT
	*   Update/Replace 405 (Method Not Allowed)
	*   200 (OK) or 204 (No Content)
	*   404 (Not Found), if ID not found or invalid.
	*   @param socketRequest The SocketRequest
	*   @return Response */
	private String putExecute(SimpleEvent socketRequest) {
		return HTTPRequest.HTTP__NOTFOUND;
	}

	/** PATCH Update/Modify
	* 405 (Method Not Allowed), unless you want to modify the collection itself.
	* 200 (OK) or 204 (No Content).
	* 404 (Not Found), if ID not found or invalid.
	* @param socketRequest The SocketRequest
	* @return Response */
	private String patchExecute(SimpleEvent socketRequest) {
		return HTTPRequest.HTTP__NOTFOUND;
	}

	public void stop() {
		if (serverSocket != null) {
			try {
				ServerSocket socket = serverSocket;
				serverSocket = null;
				socket.close();
			} catch (IOException e) {
				if (errorListener != null) {
					errorListener.update(e);
				}
			}
		}
	}

	public RESTServiceTask withLoginService(LoginService loginService) {
		this.loginController = loginService;
		return this;
	}

	public RESTServiceTask withRooting(String string, Condition<HTTPRequest> webContent) {
		if (this.routing == null) {
			this.routing = new SimpleList<HTTPRequest>();
		}

		HTTPRequest routing = HTTPRequest.createRouting(string);
		routing.withUpdateCondition(webContent);
		this.routing.add(routing);
		return this;
	}
}
