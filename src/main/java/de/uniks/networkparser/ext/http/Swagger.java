package de.uniks.networkparser.ext.http;

import java.util.Collection;
import java.util.List;

import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;

public class Swagger implements SendableEntityCreator {
	public static final String PROPERTY_VERSION = "swagger";
	public static final String PROPERTY_INFO_TITLE = "info.title";
	public static final String PROPERTY_INFO_DESCRIPTION = "info.description";
	public static final String PROPERTY_INFO_VERSION = "info.version";
	public static final String PROPERTY_INFO_TERMOFSERVICE = "info.termsOfService";
	public static final String PROPERTY_CONTACT_NAME = "info.contact.name";
	public static final String PROPERTY_CONTACT_URL = "info.contact.url";
	public static final String PROPERTY_CONTACT_EMAIL = "info.contact.email";
	public static final String PROPERTY_LICENSE_NAME = "info.license.name";
	public static final String PROPERTY_LICENSE_URL = "info.license.url";
	
	public static final String PROPERTY_HOST = "host";
	public static final String PROPERTY_BASEPATH = "basePath";
	public static final String PROPERTY_SCHEMES = "schemes";
	public static final String PROPERTY_PATHS = "path";
	public static final String[] PROPERTIES_20 = new String[] {PROPERTY_VERSION, 
			PROPERTY_INFO_TITLE, PROPERTY_INFO_DESCRIPTION, PROPERTY_INFO_VERSION, PROPERTY_INFO_TERMOFSERVICE,
			PROPERTY_HOST, PROPERTY_BASEPATH, PROPERTY_SCHEMES, 
			PROPERTY_PATHS, 
			PROPERTY_CONTACT_NAME, PROPERTY_CONTACT_URL, PROPERTY_CONTACT_EMAIL,
			PROPERTY_LICENSE_NAME, PROPERTY_LICENSE_URL};
	
	private String[] properties = PROPERTIES_20;
	private String version;
	private String infotitle;
	private String infodescription;
	private String infoVersion;
	private String infoTermsOfService;
	private String contactName;
	private String contactURL;
	private String licenseName;
	private String licenseURL;
	private String contactEMail;
	private String host;
	private String basePath;
	private List<String> schemes;
//	private List<Request> paths;
	
	@Override
	public String[] getProperties() {
		return properties;
	}
	
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Swagger();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(!(entity instanceof Swagger) || attribute == null) {
			return null;
		}
		Swagger swagger = (Swagger) entity;
		if(PROPERTY_VERSION.equals(attribute)) {
			return swagger.getVersion();
		}
		if(PROPERTY_INFO_TITLE.equals(attribute)) {
			return swagger.getInfotitle();
		}
		if(PROPERTY_INFO_DESCRIPTION.equals(attribute)) {
			return swagger.getInfodescription();
		}
		if(PROPERTY_INFO_VERSION.equals(attribute)) {
			return swagger.getInfoVersion();
		}
		if(PROPERTY_HOST.equals(attribute)) {
			return swagger.getHost();
		}
		if(PROPERTY_BASEPATH.equals(attribute)) {
			return swagger.getBasePath();
		}
		if(PROPERTY_SCHEMES.equals(attribute)) {
			return swagger.getSchemes();
		}
		if(PROPERTY_INFO_TERMOFSERVICE.equals(attribute)) {
			return swagger.getInfoTermsOfService();
		}
		if(PROPERTY_LICENSE_NAME.equals(attribute)) {
			return swagger.getLicenseName();
		}
		if(PROPERTY_LICENSE_URL.equals(attribute)) {
			return swagger.getLicenseURL();
		}
		if(PROPERTY_CONTACT_NAME.equals(attribute)) {
			return swagger.getContactName();
		}
		if(PROPERTY_CONTACT_EMAIL.equals(attribute)) {
			return swagger.getContactEMail();
		}
		if(PROPERTY_CONTACT_URL.equals(attribute)) {
			return swagger.getContactURL();
		}
		return null;
	}


	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(!(entity instanceof Swagger) || attribute == null) {
			return false;
		}
		Swagger swagger = (Swagger) entity;
		if(PROPERTY_VERSION.equals(attribute)) {
			swagger.withVersion("" + value);
			return true;
		}
		if(PROPERTY_INFO_TITLE.equals(attribute)) {
			swagger.withInfoTitle("" + value);
			return true;
		}
		if(PROPERTY_INFO_DESCRIPTION.equals(attribute)) {
			swagger.withInfoDescription("" + value);
			return true;
		}
		if(PROPERTY_INFO_VERSION.equals(attribute)) {
			swagger.withInfoVersion("" + value);
			return true;
		}
		if(PROPERTY_INFO_TERMOFSERVICE.equals(attribute)) {
			swagger.withInfoTermsOfService(""+value);
			return true;
		}
		if(PROPERTY_HOST.equals(attribute)) {
			swagger.withHost("" + value);
			return true;
		}
		if(PROPERTY_BASEPATH.equals(attribute)) {
			swagger.withBasePath("" + value);
			return true;
		}
		if(PROPERTY_SCHEMES.equals(attribute)) {
			if(value instanceof Collection<?>) {
				for(Object schema : (Collection<?>) value) {
					if(schema instanceof String) {
						swagger.addToSchemes(""+schema);
					}
				}	
			}
			return true;
		}
		if(PROPERTY_CONTACT_NAME.equals(attribute)) {
			swagger.withContactName(""+value);
			return true;
		}
		if(PROPERTY_CONTACT_URL.equals(attribute)) {
			swagger.withContactURL(""+value);
			return true;
		}
		if(PROPERTY_CONTACT_EMAIL.equals(attribute)) {
			swagger.withContactEMail(""+value);
			return true;
		}
		if(PROPERTY_LICENSE_NAME.equals(attribute)) {
			swagger.withLicenseName(""+value);
			return true;
		}
		if(PROPERTY_LICENSE_URL.equals(attribute)) {
			swagger.withLicenseURL(""+value);
			return true;
		}
		if(PROPERTY_PATHS.equals(attribute)) {
			swagger.addToPath(null);
			return true;
		}
		return false;
	}

	private Swagger addToPath(Request value) {
		return this;
	}

	public String getVersion() {
		return version;
	}

	public Swagger withVersion(String version) {
		this.version = version;
		return this;
	}

	public String getInfotitle() {
		return infotitle;
	}

	public Swagger withInfoTitle(String infotitle) {
		this.infotitle = infotitle;
		return this;
	}

	public String getInfodescription() {
		return infodescription;
	}

	public Swagger withInfoDescription(String infodescription) {
		this.infodescription = infodescription;
		return this;
	}

	public String getInfoVersion() {
		return infoVersion;
	}

	public Swagger withInfoVersion(String infoVersion) {
		this.infoVersion = infoVersion;
		return this;
	}

	public String getHost() {
		return host;
	}

	public Swagger withHost(String host) {
		this.host = host;
		return this;
	}

	public String getBasePath() {
		return basePath;
	}

	public Swagger withBasePath(String basePath) {
		this.basePath = basePath;
		return this;
	}

	public List<String> getSchemes() {
		return schemes;
	}

	public Swagger addToSchemes(String... schemes) {
		if(schemes == null || schemes.length<1) {
			return this;
		}
		if(this.schemes == null) {
			this.schemes = new SimpleList<String>();
		}
		for(String value : schemes) {
			this.schemes.add(value);
		}
		return this;
	}
	
	public Swagger withValue(Entity element) {
		this.withVersion(element.getString(PROPERTY_VERSION));
		for(String prop : this.getProperties()) {
			this.setValue(this, prop, element.getChild(prop), null);
		}
		return this;
	}

	public String getInfoTermsOfService() {
		return infoTermsOfService;
	}

	public Swagger withInfoTermsOfService(String value) {
		this.infoTermsOfService = value;
		return this;
	}

	public String getContactName() {
		return contactName;
	}

	public Swagger withContactName(String contactName) {
		this.contactName = contactName;
		return this;
	}

	public String getContactURL() {
		return contactURL;
	}

	public Swagger withContactURL(String contactURL) {
		this.contactURL = contactURL;
		return this;
	}

	public String getContactEMail() {
		return contactEMail;
	}

	public Swagger withContactEMail(String contactEMail) {
		this.contactEMail = contactEMail;
		return this;
	}

	public String getLicenseName() {
		return licenseName;
	}

	public Swagger withLicenseName(String licenseName) {
		this.licenseName = licenseName;
		return this;
	}

	public String getLicenseURL() {
		return licenseURL;
	}

	public Swagger withLicenseURL(String licenseURL) {
		this.licenseURL = licenseURL;
		return this;
	}
}

