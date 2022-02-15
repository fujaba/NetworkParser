package de.uniks.networkparser.ext.http;

import java.util.Collection;
import java.util.List;

import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class Swagger.
 *
 * @author Stefan
 */
public class Swagger implements SendableEntityCreator {
	
	/** The Constant PROPERTY_VERSION. */
	public static final String PROPERTY_VERSION = "swagger";
	
	/** The Constant PROPERTY_INFO_TITLE. */
	public static final String PROPERTY_INFO_TITLE = "info.title";
	
	/** The Constant PROPERTY_INFO_DESCRIPTION. */
	public static final String PROPERTY_INFO_DESCRIPTION = "info.description";
	
	/** The Constant PROPERTY_INFO_VERSION. */
	public static final String PROPERTY_INFO_VERSION = "info.version";
	
	/** The Constant PROPERTY_INFO_TERMOFSERVICE. */
	public static final String PROPERTY_INFO_TERMOFSERVICE = "info.termsOfService";
	
	/** The Constant PROPERTY_CONTACT_NAME. */
	public static final String PROPERTY_CONTACT_NAME = "info.contact.name";
	
	/** The Constant PROPERTY_CONTACT_URL. */
	public static final String PROPERTY_CONTACT_URL = "info.contact.url";
	
	/** The Constant PROPERTY_CONTACT_EMAIL. */
	public static final String PROPERTY_CONTACT_EMAIL = "info.contact.email";
	
	/** The Constant PROPERTY_LICENSE_NAME. */
	public static final String PROPERTY_LICENSE_NAME = "info.license.name";
	
	/** The Constant PROPERTY_LICENSE_URL. */
	public static final String PROPERTY_LICENSE_URL = "info.license.url";
	
	/** The Constant PROPERTY_HOST. */
	public static final String PROPERTY_HOST = "host";
	
	/** The Constant PROPERTY_BASEPATH. */
	public static final String PROPERTY_BASEPATH = "basePath";
	
	/** The Constant PROPERTY_SCHEMES. */
	public static final String PROPERTY_SCHEMES = "schemes";
	
	/** The Constant PROPERTY_PATHS. */
	public static final String PROPERTY_PATHS = "path";
	
	/** The Constant PROPERTIES_20. */
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
	
	/**
 * Gets the properties.
 *
 * @return the properties
 */
@Override
	public String[] getProperties() {
		return properties;
	}
	
	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Swagger();
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
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


	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
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

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * With version.
	 *
	 * @param version the version
	 * @return the swagger
	 */
	public Swagger withVersion(String version) {
		this.version = version;
		return this;
	}

	/**
	 * Gets the infotitle.
	 *
	 * @return the infotitle
	 */
	public String getInfotitle() {
		return infotitle;
	}

	/**
	 * With info title.
	 *
	 * @param infotitle the infotitle
	 * @return the swagger
	 */
	public Swagger withInfoTitle(String infotitle) {
		this.infotitle = infotitle;
		return this;
	}

	/**
	 * Gets the infodescription.
	 *
	 * @return the infodescription
	 */
	public String getInfodescription() {
		return infodescription;
	}

	/**
	 * With info description.
	 *
	 * @param infodescription the infodescription
	 * @return the swagger
	 */
	public Swagger withInfoDescription(String infodescription) {
		this.infodescription = infodescription;
		return this;
	}

	/**
	 * Gets the info version.
	 *
	 * @return the info version
	 */
	public String getInfoVersion() {
		return infoVersion;
	}

	/**
	 * With info version.
	 *
	 * @param infoVersion the info version
	 * @return the swagger
	 */
	public Swagger withInfoVersion(String infoVersion) {
		this.infoVersion = infoVersion;
		return this;
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * With host.
	 *
	 * @param host the host
	 * @return the swagger
	 */
	public Swagger withHost(String host) {
		this.host = host;
		return this;
	}

	/**
	 * Gets the base path.
	 *
	 * @return the base path
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * With base path.
	 *
	 * @param basePath the base path
	 * @return the swagger
	 */
	public Swagger withBasePath(String basePath) {
		this.basePath = basePath;
		return this;
	}

	/**
	 * Gets the schemes.
	 *
	 * @return the schemes
	 */
	public List<String> getSchemes() {
		return schemes;
	}

	/**
	 * Adds the to schemes.
	 *
	 * @param schemes the schemes
	 * @return the swagger
	 */
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
	
	/**
	 * With value.
	 *
	 * @param element the element
	 * @return the swagger
	 */
	public Swagger withValue(Entity element) {
		this.withVersion(element.getString(PROPERTY_VERSION));
		for(String prop : this.getProperties()) {
			this.setValue(this, prop, element.getValue(prop), null);
		}
		return this;
	}

	/**
	 * Gets the info terms of service.
	 *
	 * @return the info terms of service
	 */
	public String getInfoTermsOfService() {
		return infoTermsOfService;
	}

	/**
	 * With info terms of service.
	 *
	 * @param value the value
	 * @return the swagger
	 */
	public Swagger withInfoTermsOfService(String value) {
		this.infoTermsOfService = value;
		return this;
	}

	/**
	 * Gets the contact name.
	 *
	 * @return the contact name
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * With contact name.
	 *
	 * @param contactName the contact name
	 * @return the swagger
	 */
	public Swagger withContactName(String contactName) {
		this.contactName = contactName;
		return this;
	}

	/**
	 * Gets the contact URL.
	 *
	 * @return the contact URL
	 */
	public String getContactURL() {
		return contactURL;
	}

	/**
	 * With contact URL.
	 *
	 * @param contactURL the contact URL
	 * @return the swagger
	 */
	public Swagger withContactURL(String contactURL) {
		this.contactURL = contactURL;
		return this;
	}

	/**
	 * Gets the contact E mail.
	 *
	 * @return the contact E mail
	 */
	public String getContactEMail() {
		return contactEMail;
	}

	/**
	 * With contact E mail.
	 *
	 * @param contactEMail the contact E mail
	 * @return the swagger
	 */
	public Swagger withContactEMail(String contactEMail) {
		this.contactEMail = contactEMail;
		return this;
	}

	/**
	 * Gets the license name.
	 *
	 * @return the license name
	 */
	public String getLicenseName() {
		return licenseName;
	}

	/**
	 * With license name.
	 *
	 * @param licenseName the license name
	 * @return the swagger
	 */
	public Swagger withLicenseName(String licenseName) {
		this.licenseName = licenseName;
		return this;
	}

	/**
	 * Gets the license URL.
	 *
	 * @return the license URL
	 */
	public String getLicenseURL() {
		return licenseURL;
	}

	/**
	 * With license URL.
	 *
	 * @param licenseURL the license URL
	 * @return the swagger
	 */
	public Swagger withLicenseURL(String licenseURL) {
		this.licenseURL = licenseURL;
		return this;
	}
}

