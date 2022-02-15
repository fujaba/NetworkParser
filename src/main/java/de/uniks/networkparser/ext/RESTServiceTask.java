package de.uniks.networkparser.ext;

import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;

import de.uniks.networkparser.Deep;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.http.ConfigService;
import de.uniks.networkparser.ext.http.Configuration;
import de.uniks.networkparser.ext.http.HTTPRequest;
import de.uniks.networkparser.ext.http.ImpressumService;
import de.uniks.networkparser.ext.http.LoginService;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyModel;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SimpleUpdateListener;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * The Class RESTServiceTask.
 *
 * @author Stefan
 */
public class RESTServiceTask implements Condition<Socket> {
	
	/** The Constant PROPERTY_ERROR. */
	public static final String PROPERTY_ERROR = "error";
	
	/** The Constant PROPERTY_ALLOW. */
	public static final String PROPERTY_ALLOW = "allow";
	private SendableEntityCreator creator;
	private Space space;

	private Filter filter = Filter.regard(Deep.create(1));
	private SimpleUpdateListener allowListener;
	private SimpleUpdateListener loginController;
	private SimpleUpdateListener executeController;
	
	/** The Constant JSON. */
	public static final String JSON = "/json";
	
	/** The Constant XML. */
	public static final String XML = "/xml";
	private SimpleList<HTTPRequest> routing;
	private NodeProxyTCP proxy;
	private boolean routingExists;
	private ConfigService configService;
    private ImpressumService impressumService;
	
	/**
	 * Creates the server.
	 *
	 * @param config the config
	 * @param map the map
	 * @param root the root
	 * @return the REST service task
	 */
	public RESTServiceTask createServer(Configuration config, IdMap map, Object root) {
		this.proxy = NodeProxyTCP.createServer(config.getPort());
		if (map != null) {
			creator = map.getCreatorClass(root);
		}
		configService = new ConfigService(config);
		configService.withTask(this);
		withRouting(configService.getRouting());
		
		impressumService = new ImpressumService();
        withRouting(getImpressumService().getRouting());
		
		this.proxy.withRestService(this);
		
		space = new Space();
		space.withMap(map);
		space.with(new NodeProxyModel(root));
		return this;
	}
	
	/**
	 * Start.
	 */
	public void start() {
		if(space != null) {
			space.with(proxy);
		}
	}
	
	/**
	 * With proxy.
	 *
	 * @param proxy the proxy
	 * @return the REST service task
	 */
	public RESTServiceTask withProxy(NodeProxyTCP proxy) {
		this.proxy = proxy;
		this.space = proxy.getSpace();
		return this;
	}

	/**
	 * With allow listener.
	 *
	 * @param listener the listener
	 * @return the REST service task
	 */
	public RESTServiceTask withAllowListener(SimpleUpdateListener listener) {
		this.allowListener = listener;
		return this;
	}
	
	/**
	 * Gets the routings.
	 *
	 * @return the routings
	 */
	public SimpleList<HTTPRequest> getRoutings() {
		return routing;
	}
	
	/**
	 * Update.
	 *
	 * @param socket the socket
	 * @return true, if successful
	 */
	public boolean update(Socket socket) {
		HTTPRequest clientSocket = HTTPRequest.create(socket);
		clientSocket.readType();
		clientSocket.readPath();
		HTTPRequest match = null;
		String path = clientSocket.getUrl();
		if (routing != null && !routing.isEmpty()) {
			/* Parsing Path */
			HTTPRequest defaultMatch = null;
			SortedList<HTTPRequest> matches = new SortedList<HTTPRequest>(true);
			for (int i = 0; i < routing.size(); i++) {
				HTTPRequest key = routing.get(i);
				if ("*".equals(key.getUrl())) {
					defaultMatch = key;
				}
				if (clientSocket.match(key)) {
					matches.add(key);
				}
			}
			if (!matches.isEmpty()) {
				HTTPRequest first = matches.first();
				if ((routingExists && first.isValid()) || !routingExists ) {
					match = first;
				} else if (path.indexOf("/") < 1) {
					match = defaultMatch;
				}
			}
		}
		/* SO NEW MATCHES */
		SimpleEvent event = new SimpleEvent(this, clientSocket.getUrl(), clientSocket);
		if (allowListener != null) {
			if (!allowListener.update(event)) {
				clientSocket.writeHeader(HTTPRequest.HTTP_STATE_PERMISSION_DENIED);
				clientSocket.close();
				return false;
			}
		} else if (loginController != null) {
			if (!loginController.update(event)) {
				clientSocket.close();
				return false;
			}
		}
	    if(match == null && routing.size()>0 && (path == null || path.isEmpty())) {
            // ROOT AND DEFAULT PAGE
            HTMLEntity entity = new HTMLEntity();
            XMLEntity menueItem = entity.createChild("div", "class", "menue");
            for(HTTPRequest item : routing) {
                if(item.getTag() != null) {
                    entity.createChild("a", menueItem, "class", "button", "target", "_self", "href", item.getUrl(), item.getTag());
                }
            }
            clientSocket.withContent(entity);
        }
		if(this.executeController != null) {
			boolean success = executeController.update(event);
			if(success) {
				clientSocket.close();
				return success;
			}
		}
		/* So Valid and Execute Match or default CHECK FOR NEXT VALID OR BEST */
		if (match != null) {
			boolean success = match.update(clientSocket);
			clientSocket.close();
			return success;
		}
		String result = this.executeRequest(event);
		clientSocket.writeBody(result);
		clientSocket.close();
		return true;
	}

	/**
	 * Close.
	 *
	 * @return true, if successful
	 */
	public boolean close() {
		return this.proxy.close();
	}

	/**
	 * Checks if is run.
	 *
	 * @return true, if is run
	 */
	public boolean isRun() {
		return this.proxy.isRun();
	}

	/**
	 * Execute request.
	 *
	 * @param request the request
	 * @return the string
	 */
	public String executeRequest(String request) {
		SimpleEvent event = new SimpleEvent(this, request);
		return executeRequest(event);
	}

	/**
	 * Execute request.
	 *
	 * @param socketRequest the socket request
	 * @return the string
	 */
	public String executeRequest(SimpleEvent socketRequest) {
		if (socketRequest == null) {
			return null;
		}
		String type = socketRequest.getType();
		if (HTTPRequest.HTTP_TYPE_GET.equalsIgnoreCase(type) || "".equals(type)
				|| SendableEntityCreator.NEW.equalsIgnoreCase(type)) {
			return this.getExecute(socketRequest);
		}
		if (HTTPRequest.HTTP_TYPE_POST.equalsIgnoreCase(type)) {
			return this.postExecute(socketRequest);
		}
		if (HTTPRequest.HTTP_TYPE_PUT.equalsIgnoreCase(type)) {
			return this.putExecute(socketRequest);
		}
		if (HTTPRequest.HTTP_TYPE_PATCH.equalsIgnoreCase(type)) {
			return this.patchExecute(socketRequest);
		}
		if (HTTPRequest.HTTP_TYPE_DELETE.equalsIgnoreCase(type)) {
			return this.deleteExecute(socketRequest);
		}
		return null;
	}

	private Object getElement(CharacterBuffer request, CharacterBuffer path, CharacterBuffer listID,
			boolean lastElement) {
		int pos = 0;
		SendableEntityCreator creator = this.creator;
		if(creator == null) {
		    return null;
		}

		if (request.startsWith(JSON, 0, false)) {
			pos = 6;
		} else if (request.startsWith(XML, 0, false)) {
			pos = 5;
		}
		Object element = space.getModelRoot();
		Object last = element;
		IdMap map = space.getMap();
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
		IdMap map = space.getMap();
		if (element == space.getModelRoot()) {
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
		return HTTPRequest.HTTP_STATE_NOTFOUND;
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
			space.getMap().removeObj(element, true);
			return HTTPRequest.HTTP_STATE_OK;
		}
		return HTTPRequest.HTTP_STATE_NOTFOUND;
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
			return HTTPRequest.HTTP_STATE_NOTFOUND;
		}
		Object source = socketRequest.getSource();
		if (source instanceof HTTPRequest == false) {
			return HTTPRequest.HTTP_STATE_NOTFOUND;
		}
		HTTPRequest request = (HTTPRequest) source;

		CharacterBuffer path = new CharacterBuffer();
		CharacterBuffer listID = new CharacterBuffer();
		CharacterBuffer pathValue = new CharacterBuffer().with(request.getUrl());
		Object element = getElement(pathValue, path, listID, true);

		/* First item SWITCH */
		String body = request.getContent();
		IdMap map = space.getMap();
		if (body.charAt(0) == JsonObject.START || body.charAt(0) == XMLEntity.START) {
			/* JsonObject or XMLEntity */
			Object child = map.decode(body);
			SendableEntityCreator creator = map.getCreatorClass(element);
			if (creator != null) {
				creator.setValue(element, pathValue.toString(), child, SendableEntityCreator.NEW);
			}
			return HTTPRequest.HTTP_STATE_OK;
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
		return HTTPRequest.HTTP_STATE_OK;
	}

	/** PUT
	*   Update/Replace 405 (Method Not Allowed)
	*   200 (OK) or 204 (No Content)
	*   404 (Not Found), if ID not found or invalid.
	*   @param socketRequest The SocketRequest
	*   @return Response */
	private String putExecute(SimpleEvent socketRequest) {
		return HTTPRequest.HTTP_STATE_NOTFOUND;
	}

	/** PATCH Update/Modify
	* 405 (Method Not Allowed), unless you want to modify the collection itself.
	* 200 (OK) or 204 (No Content).
	* 404 (Not Found), if ID not found or invalid.
	* @param socketRequest The SocketRequest
	* @return Response */
	private String patchExecute(SimpleEvent socketRequest) {
		return HTTPRequest.HTTP_STATE_NOTFOUND;
	}

	/**
	 * Stop.
	 */
	public void stop() {
		proxy.stop();
	}

	/**
	 * With login service.
	 *
	 * @param loginService the login service
	 * @return the REST service task
	 */
	public RESTServiceTask withLoginService(LoginService loginService) {
		this.loginController = loginService;
		return this;
	}
	
	/**
	 * With routing.
	 *
	 * @param routing the routing
	 * @return the REST service task
	 */
	public RESTServiceTask withRouting(HTTPRequest routing) {
		if (this.routing == null) {
			this.routing = new SimpleList<HTTPRequest>();
		}
		this.routing.add(routing);
		return this;
	}

	/**
	 * With rooting.
	 *
	 * @param string the string
	 * @param webContent the web content
	 * @return the REST service task
	 */
	public RESTServiceTask withRooting(String string, Condition<HTTPRequest> webContent) {
		HTTPRequest route = HTTPRequest.createRouting(string);
		route.withUpdateCondition(webContent);
		this.withRouting(route);
		return this;
	}

	/**
	 * Gets the configuration service.
	 *
	 * @return the configuration service
	 */
	public ConfigService getConfigurationService() {
		return this.configService;
	}

	/**
	 * With execute listener.
	 *
	 * @param executeController the execute controller
	 * @return the REST service task
	 */
	public RESTServiceTask withExecuteListener(SimpleUpdateListener executeController) {
		this.executeController = executeController;
		return this;
	}

    /**
     * Gets the impressum service.
     *
     * @return the impressum service
     */
    public ImpressumService getImpressumService() {
        return impressumService;
    }
}
