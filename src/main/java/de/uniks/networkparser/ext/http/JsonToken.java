package de.uniks.networkparser.ext.http;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.ByteConverter64;
import de.uniks.networkparser.bytes.HMAC;
import de.uniks.networkparser.json.JsonObject;

/**
 * Tokener for Json Format.
 *
 * @author Stefan Lindel
 */
public class JsonToken {
	
	/** The Constant EXPIRATION_DAY. */
	public static final long EXPIRATION_DAY = 24 * 60 * 60;
	
	/** The Constant ALG. */
	public static final String ALG = "alg";
	
	/** The Constant HS256. */
	public static final String HS256 = "HS256";
	
	/** The Constant SUB. */
	public static final String SUB = "sub";
	
	/** The Constant IAT. */
	public static final String IAT = "iat";
	
	/** The Constant EXPIRATION. */
	public static final String EXPIRATION = "exp";
	private JsonObject header;
	private JsonObject body;
	private Long expiration;
	private String secret;

	/**
	 * With algorytm.
	 *
	 * @param value the value
	 * @return the json token
	 */
	public JsonToken withAlgorytm(String value) {
		if (header == null) {
			header = new JsonObject();
		}
		header.add(ALG, value);
		return this;
	}

	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public JsonObject getHeader() {
		if (this.header == null) {
			this.withAlgorytm(HS256);
		}
		return this.header;
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public JsonObject getBody() {
		if (this.body == null) {
			this.body = new JsonObject();
		}
		return this.body;
	}

	/**
	 * Gets the body clone.
	 *
	 * @return the body clone
	 */
	public JsonObject getBodyClone() {
		JsonObject jsonObject = new JsonObject();
		JsonObject ref = getBody();
		for (int i = 0; i < ref.size(); i++) {
			jsonObject.put(ref.getKeyByIndex(i), ref.getValueByIndex(i));
		}
		return jsonObject;
	}

	/**
	 * With subject.
	 *
	 * @param value the value
	 * @return the json token
	 */
	public JsonToken withSubject(String value) {
		getBody().put(SUB, value);
		return this;
	}

	/**
	 * With time.
	 *
	 * @param time the time
	 * @return the json token
	 */
	public JsonToken withTime(long time) {
		getBody().put(IAT, time);
		return this;
	}

	/**
	 * With expiration.
	 *
	 * @param time the time
	 * @return the json token
	 */
	public JsonToken withExpiration(Long time) {
		this.expiration = time;
		return this;
	}

	/**
	 * With secret.
	 *
	 * @param secret the secret
	 * @return the json token
	 */
	public JsonToken withSecret(String secret) {
		this.secret = secret;
		return this;
	}

	/**
	 * Gets the token.
	 *
	 * @return the token
	 */
	public String getToken() {
		if (this.secret == null) {
			return null;
		}
		JsonObject header = getHeader();
		ByteConverter64 converter = new ByteConverter64();
		String headerString = converter.toStaticString(header.toString(), false).toString();

		JsonObject body = getBodyClone();
		if (body.has(IAT) == false) {
			body.put(IAT, System.currentTimeMillis() / 1000);
		}
		if (body.has(EXPIRATION) == false && this.expiration != null) {
			body.put(EXPIRATION, body.getLong(IAT) + this.expiration);
		}
		String bodyString = converter.toStaticString(body.toString(), false).toString();

		HMAC hmac = new HMAC(this.secret);
		CharacterBuffer buffer = new CharacterBuffer().with(headerString).with('.').with(bodyString);
		byte[] checkSumBytes = hmac.update(buffer.toString()).digest();
		String checkSum = converter.toStaticString(checkSumBytes, false).toString();
		buffer.with('.').with(checkSum);
		return buffer.toString();
	}

	/**
	 * Gets the check sum.
	 *
	 * @return the check sum
	 */
	public String getCheckSum() {
		String token = getToken();
		if (token == null) {
			return null;
		}
		int pos = token.lastIndexOf('.');
		if (pos > 0) {
			return token.substring(pos + 1);
		}
		return null;
	}

	/**
	 * With key value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the json token
	 */
	public JsonToken withKeyValue(String key, String value) {
		if (this.body == null) {
			this.body = new JsonObject();
		}
		this.body.add(key, value);
		return this;
	}

	/**
	 * Creates the.
	 *
	 * @return the json token
	 */
	public JsonToken create() {
		JsonToken tokener = new JsonToken();
		tokener.withSecret(this.getSecret());
		tokener.withHeader(this.getHeader());
		tokener.withBody(this.getBodyClone());
		tokener.withExpiration(this.getExpiration());
		return tokener;
	}

	/**
	 * Creates the decoder.
	 *
	 * @return the json token
	 */
	public JsonToken createDecoder() {
		JsonToken tokener = new JsonToken();
		tokener.withSecret(this.getSecret());
		tokener.withHeader(this.getHeader());
		return tokener;
	}

	/**
	 * Gets the expiration.
	 *
	 * @return the expiration
	 */
	public Long getExpiration() {
		return this.expiration;
	}

	private JsonToken withBody(JsonObject value) {
		this.body = value;
		return this;
	}

	private JsonToken withHeader(JsonObject value) {
		this.header = value;
		return this;
	}

	private String getSecret() {
		return this.secret;
	}

	/**
	 * Validate.
	 *
	 * @param token the token
	 * @return true, if successful
	 */
	public boolean validate(String token) {
		if (token == null) {
			return false;
		}
		String header;
		String payLoad;
		String checkSum;

		int pos = token.indexOf('.');
		if (pos < 0) {
			return false;
		}
		header = token.substring(0, pos);
		int start = pos + 1;
		pos = token.indexOf('.', start);
		if (pos < 0) {
			return false;
		}
		payLoad = token.substring(start, pos);
		checkSum = token.substring(pos + 1);
		/* CHECK HEADER */
		JsonObject headerJson = new JsonObject().withValue(ByteConverter64.fromBase64String(header));
		if (headerJson.equals(this.header) == false) {
			return false;
		}
		/* CHECK HASHCODE */

		this.body = new JsonObject().withValue(ByteConverter64.fromBase64String(payLoad));
		String token2 = this.getCheckSum();
		if (checkSum.equals(token2) == false) {
			return false;
		}
		/* CHECK EXPIRATED */
		this.expiration = this.body.getLong(EXPIRATION);
		if (this.expiration < System.currentTimeMillis() / 1000) {
			return false;
		}
		return true;
	}
}
