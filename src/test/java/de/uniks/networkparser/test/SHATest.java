package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.ByteConverter64;
import de.uniks.networkparser.ext.http.JsonToken;
import de.uniks.networkparser.json.JsonObject;

public class SHATest {

	@Test
	public void testSHA2() {
		Long now = System.currentTimeMillis();
		int secondsNow = (int) (now / 1000);
		String secret = "1511d1ace88704f9bc8be50b5e6641d207e5308a1c468f9088f5ed4d66329abc";
		
		String payLoad = "eyJzdWIiOiJhZG1pbiIsImlhdCI6MTU1OTY2MzA5NCwiZXhwIjoxNTU5NzQ5NDk0fQ";
//		CharacterBuffer fromBase64String = ByteConverter64.fromBase64String(payLoad);
//		new JsonObject().withValue(fromBase64String);
		
//		int expiration = 24*60*60;

		JsonToken jsonToken = new JsonToken();
		jsonToken.withSubject("admin");
		jsonToken.withSecret(secret);
		jsonToken.withTime(secondsNow).withExpiration(JsonToken.EXPIRATION_DAY);
		
		String token = jsonToken.getToken();
		
		
		// COPY OF ELEMENTS
		JsonToken create = jsonToken.createDecoder();
		Assert.assertTrue(create.validate(token));
	}
}
