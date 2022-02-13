package de.uniks.networkparser.ext.http;

import java.util.Iterator;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.interfaces.SimpleUpdateListener;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class ConfigService implements Condition<HTTPRequest> {
	private HTTPRequest routing;
	private RESTServiceTask task;
	private Configuration configuration;
	
	public static final String ACTION_CLOSE="close";
	public static final String ACTION_OPEN="open";
	public static final String ACTION_SAVE="Speichern";
	public static final String ACTION_ABORT="Abbrechen";
	private SimpleUpdateListener listener;
	
	public ConfigService(Configuration configuration) {
		routing = HTTPRequest.createRouting("/"+ configuration.getTag());
		routing.withUpdateCondition(this);
		this.configuration = configuration;
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
		if(HTTPRequest.HTTP_TYPE_POST.equalsIgnoreCase(value.getHttp_Type())) {
			if(executeSettings(value)) {
				return true;
			}
		}
		if(HTTPRequest.HTTP_TYPE_GET.equalsIgnoreCase(value.getHttp_Type())) {
			return this.showDefault(value);
		} else if(HTTPRequest.HTTP_TYPE_POST.equalsIgnoreCase(value.getHttp_Type())) {
			String key = value.parse().getContentValue(HTMLEntity.ACTION);
			if(ACTION_CLOSE.equalsIgnoreCase(key)) {
				task.close();
				listener.update(new SimpleEvent(this, ACTION_CLOSE, value));
			}
		}
		return false;
	}

	private boolean showDefault(HTTPRequest value) {
		HTMLEntity entity = new HTMLEntity();
		entity.createTag("h1", "Konfiguration");
		
		entity.withActionButton("Allgemein", ACTION_OPEN, this.routing.getAbsolutePath("general"));
		
		if(configuration != null && configuration.getSettings() != null) {
			for(Iterator<SendableEntityCreatorTag> iterator = configuration.getSettings().iterator();iterator.hasNext();) {
				SendableEntityCreatorTag entry = iterator.next();
				entity.withActionButton(entry.getTag(), ACTION_OPEN, this.routing.getAbsolutePath(entry.getTag()));
			}
		}
		
		entity.withActionButton("Beenden", ACTION_CLOSE);
		if(this.task.getImpressumService() != null) {
		    XMLEntity footer = entity.createTag("div").with("class", "footer");
		    XMLEntity lnk = footer.createChild("a", "href", this.task.getImpressumService().getRouting().getAbsolutePath());
		    lnk.withValue("Impressum");
		}
		value.withContent(entity);
		if(listener != null) {
			listener.update(new SimpleEvent(this, HTTPRequest.HTTP_TYPE_GET, value));
		}
		value.write(entity);
		return true;
	}

	private boolean executeSettings(HTTPRequest value) {
		String path = this.routing.getUrl()+"/general";
		if(value.getUrl().equalsIgnoreCase(path)) {
			String key = value.parse().getContentValue(HTMLEntity.ACTION);
			if(key != null) {
				if(key.equalsIgnoreCase(ACTION_ABORT)) {
					return value.redirect(this.routing.getAbsolutePath());
				}
				if(key.equalsIgnoreCase(ACTION_SAVE)) {
					configuration.setValue(configuration, Configuration.PORT, value.getContentValue("port"), SendableEntityCreator.NEW);
					listener.update(new SimpleEvent(this, ACTION_SAVE, value));
					return value.redirect(this.routing.getAbsolutePath());
				}
			}
			HTMLEntity entity = new HTMLEntity();
			entity.createTag("h1", "Allgemein");
			XMLEntity formTag = entity.createTag("form").withKeyValue("method", "post")
			.withKeyValue("enctype", "application/json");
			
			formTag.withChild(entity.createInput("Port:", "port", configuration.getPort()));
			formTag.createChild("input", "type", "submit", "value", ACTION_SAVE, "name", HTMLEntity.ACTION);
			formTag.createChild("input", "type", "submit", "value", ACTION_ABORT, "name", HTMLEntity.ACTION);
			value.withContent(entity);
			if(listener != null) {
				listener.update(new SimpleEvent(this, HTTPRequest.HTTP_TYPE_GET, value));
			}
			value.write(entity);
			return true;
		}
		if(configuration != null && configuration.getSettings() != null) {
			for(SendableEntityCreatorTag creator : configuration.getSettings()) {
				path = this.routing.getUrl()+"/"+creator.getTag();
				if(value.getUrl().equalsIgnoreCase(path)) {
					String key = value.parse().getContentValue(HTMLEntity.ACTION);
					if(key != null) {
						if(key.equalsIgnoreCase(ACTION_ABORT)) {
							return value.redirect(this.routing.getAbsolutePath());
						}
						if(key.equalsIgnoreCase(ACTION_SAVE)) {
							for(String prop : creator.getProperties()) {
								creator.setValue(creator, prop, value.getContentValue(prop), SendableEntityCreator.NEW);
							}
							listener.update(new SimpleEvent(this, ACTION_SAVE, value));
							return value.redirect(this.routing.getAbsolutePath());
						}
					}
					HTMLEntity entity = new HTMLEntity();
					entity.createTag("h1", creator.getTag());
					XMLEntity formTag = entity.createTag("form").withKeyValue("method", "post").withKeyValue("enctype", "application/json");
					for(String prop : creator.getProperties()) {
						formTag.withChild(entity.createInput(prop+":", prop, creator.getValue(creator, prop)));	
					}
					formTag.createChild("input", "type", "submit", "value", ACTION_SAVE, "name", HTMLEntity.ACTION);
					formTag.createChild("input", "type", "submit", "value", ACTION_ABORT, "name", HTMLEntity.ACTION);
					value.withContent(entity);
					if(listener != null) {
						listener.update(new SimpleEvent(this, HTTPRequest.HTTP_TYPE_GET, value));
					}
					return true;
				}
			}			
		}
		return false;
	}

	public ConfigService withListener(SimpleUpdateListener updateListener) {
		this.listener = updateListener;
		return this;
	}
}
