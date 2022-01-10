package de.uniks.networkparser.ext.http;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SimpleUpdateListener;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class ConfigService implements Condition<HTTPRequest> {
	private HTTPRequest routing;
	private RESTServiceTask task;
	private Configuration configuration;
	
	public static final String ACTION_CLOSE="close";
	private SimpleUpdateListener listener;
	
	public ConfigService() {
		routing = HTTPRequest.createRouting("/config");
		routing.withUpdateCondition(this);
	}
	
	public HTTPRequest getRouting() {
		return routing;
	}
	
	public ConfigService withTask(RESTServiceTask task) {
		this.task = task;
		return this;
	}
	
	@Override
	public boolean update(HTTPRequest value) {
		if(task == null) {
			return false;
		}
		if(NodeProxyTCP.GET.equalsIgnoreCase(value.getHttp_Type())) {
			HTMLEntity entity = new HTMLEntity();
			entity.createTag("h1", "Konfiguration");
			
			XMLEntity formTag = entity.createTag("form").withKeyValue("action", routing.getPath()).withKeyValue("method", "post")
					.withKeyValue("enctype", "application/json");
			XMLEntity portinput = formTag.createChild("input", "name", "port");
			portinput.setValueItem("value", configuration.getPort());
			
			formTag.createChild("input", "type", "submit", "value", "Speichern");
			
			entity.withActionButton("Beenden", ACTION_CLOSE);

			value.withBufferResponse(entity);
			if(listener != null) {
				listener.update(new SimpleEvent(value, NodeProxyTCP.GET, value));
			}
			value.write(entity);
			return true;
		} else if(NodeProxyTCP.POST.equalsIgnoreCase(value.getHttp_Type())) {
			String key = value.parse().getContentValue(HTMLEntity.ACTION);
			if(ACTION_CLOSE.equalsIgnoreCase(key)) {
				task.close();
				listener.update(new SimpleEvent(value, HTMLEntity.ACTION, ACTION_CLOSE));
			}
		}
		return false;
	}

	public ConfigService withConfiguration(Configuration config) {
		this.configuration = config;
		return this;
	}

	public ConfigService withListener(SimpleUpdateListener updateListener) {
		this.listener = updateListener;
		return this;
	}
}
