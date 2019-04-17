package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.ext.petaf.JsonToken;

public class SHATest {

	@Test
	public void testSHA2() {
		Long now = System.currentTimeMillis();
		now = 1555084115686l;
		int secondsNow = (int) (now/1000);
		String secret = "1511d1ace88704f9bc8be50b5e6641d207e5308a1c468f9088f5ed4d66329abc";
		
//		int expiration = 24*60*60;

	
		JsonToken jsonToken = new JsonToken();
		jsonToken.withSubject("admin");
		jsonToken.withSecret(secret);
		jsonToken.withTime(secondsNow).withExpiration(JsonToken.EXPIRATION_DAY);
		System.out.println("MY: ");
		System.out.println(jsonToken.getToken());
			}
}
