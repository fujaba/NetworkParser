package de.uniks.networkparser.ext.http;

import java.util.Iterator;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.interfaces.SimpleUpdateListener;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * The Class ConfigService.
 *
 * @author Stefan
 */
public class ConfigService implements Condition<HTTPRequest> {
	private HTTPRequest routing;
	private RESTServiceTask task;
	private Configuration configuration;
	
	/** The Constant ACTION_CLOSE. */
	public static final String ACTION_CLOSE="close";
	
	/** The Constant ACTION_OPEN. */
	public static final String ACTION_OPEN="open";
	
	/** The Constant ACTION_SAVE. */
	public static final String ACTION_SAVE="Speichern";
	
	/** The Constant ACTION_ABORT. */
	public static final String ACTION_ABORT="Abbrechen";
	private SimpleUpdateListener listener;
    private String tag = "Konfiguration";
	
	/**
	 * Instantiates a new config service.
	 *
	 * @param configuration the configuration
	 */
	public ConfigService(Configuration configuration) {
		routing = HTTPRequest.createRouting("/"+ configuration.getTag());
		routing.withTag(tag);
		routing.withUpdateCondition(this);
		this.configuration = configuration;
	}
	
	/**
	 * With tag.
	 *
	 * @param value the value
	 * @return the config service
	 */
	public ConfigService withTag(String value) {
	    this.tag = value;
	    return this;
	}
	
	/**
	 * Gets the routing.
	 *
	 * @return the routing
	 */
	public HTTPRequest getRouting() {
		return routing;
	}
	
	/**
	 * With task.
	 *
	 * @param task the task
	 * @return the config service
	 */
	public ConfigService withTask(RESTServiceTask task) {
		this.task = task;
		return this;
	}
	
	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
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
		
		XMLEntity headerTag = entity.createChild("div", "class", "header");
		XMLEntity backBtn = headerTag.createChild("a", "href", "/");
		backBtn.createChild("div", FileBuffer.readResource("back.svg", GraphUtil.class).toString());
		backBtn.createChild("div", "Zur&uuml;ck");
		
		entity.createChild("h1", tag);
		
		entity.withActionButton("Allgemein", ACTION_OPEN, this.routing.getAbsolutePath("general"));
		
		if(configuration != null && configuration.getSettings() != null) {
			for(Iterator<SendableEntityCreatorTag> iterator = configuration.getSettings().iterator();iterator.hasNext();) {
				SendableEntityCreatorTag entry = iterator.next();
				entity.withActionButton(entry.getTag(), ACTION_OPEN, this.routing.getAbsolutePath(entry.getTag()));
			}
		}
		
		entity.withActionButton("Beenden", ACTION_CLOSE);
		if(this.task.getImpressumService() != null) {
		    XMLEntity footer = entity.createChild("div").with("class", "footer");
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
			entity.createChild("h1", "Allgemein");
			XMLEntity formTag = entity.createChild("form").withKeyValue("method", "post")
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
					entity.createChild("h1", creator.getTag());
					XMLEntity formTag = entity.createChild("form").withKeyValue("method", "post").withKeyValue("enctype", "application/json");
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

	/**
	 * With listener.
	 *
	 * @param updateListener the update listener
	 * @return the config service
	 */
	public ConfigService withListener(SimpleUpdateListener updateListener) {
		this.listener = updateListener;
		return this;
	}
}
