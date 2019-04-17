package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.HMAC;
import de.uniks.networkparser.converter.ByteConverter64;
import de.uniks.networkparser.json.JsonObject;

public class JsonToken {
	public static final long EXPIRATION_DAY= 24*60*60;
	public static final String ALG="alg";
	public static final String HS256="HS256";
	public static final String SUB = "sub";
	public static final String IAT = "iat";
	public static final String EXP = "exp";
	private JsonObject header;
	private JsonObject body;
	private Long expiration;
	private String secret;
	
	public JsonToken withAlgorytm(String value) {
		if(header == null) {
			header = new JsonObject();
		}
		header.add(ALG, value);
		return this;
	}

	public JsonObject getHeader() {
		if(this.header == null) {
			this.withAlgorytm(HS256);
		}
		return this.header;
	}
	
	public JsonObject getBody() {
		if(this.body == null) {
			this.body = new JsonObject();
		}
		return this.body;
	}
	
	public JsonToken withSubject(String value) {
		getBody().put(SUB, value);
		return this;
	}
	
	public JsonToken withTime(long time) {
		getBody().put(IAT, time);
		return this;
	}
	
	public JsonToken withExpiration(long time) {
		this.expiration = time;
		return this;
	}
	
	public JsonToken withSecret(String secret) {
		this.secret = secret;
		return this;
	}
	
	public String getToken() {
		if(this.secret == null) {
			return null;
		}
		JsonObject header = getHeader();
		ByteConverter64 converter = new ByteConverter64();
		String headerString = converter.toStaticString(header.toString(), false).toString();
		
		JsonObject body = getBody();
		if(body.has(IAT) && body.has(EXP) == false && this.expiration != null) {
			body.put(EXP, body.getLong(IAT)+this.expiration);
		}
		String bodyString = converter.toStaticString(body.toString(), false).toString();
		
		HMAC hmac = new HMAC(this.secret);
		CharacterBuffer buffer = new CharacterBuffer().with(headerString).with('.').with(bodyString);
		byte[] checkSumBytes = hmac.update(buffer.toString()).digest();
		String checkSum = converter.toStaticString(checkSumBytes, false).toString();
		buffer.with('.').with(checkSum);
		return buffer.toString();
	}
	
	public JsonToken withKeyValue(String key, String value ) {
		if(this.body == null) {
			this.body = new JsonObject();
		}
		this.body.add(key, value);
		return this;
	}
}
