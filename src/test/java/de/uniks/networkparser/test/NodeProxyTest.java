package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.xml.HTMLEntity;

public class NodeProxyTest {

	@Test
	public void UniversityOfMadness() {
		HTMLEntity answer = NodeProxyTCP.getHTTP("avocado.uniks.de", 33000, "user/info");
		System.out.println(answer);
	}

	@Test
	public void UniversityOfMadnessCreate() {
//		HTMLEntity answer = NodeProxyTCP.postHTTP("avocado.uniks.de", 33000, "user/create", NodeProxyTCP.BODY_JSON, "Username", "Eraser6");
		
		HTMLEntity login = NodeProxyTCP.postHTTP("avocado.uniks.de", 33000, "user/login", NodeProxyTCP.BODY_JSON, "Username", "Eraser", "Password", "crazy");
		System.out.println(login.getStatusCode()+": "+login.getStatusMessage());
		System.out.println("BODY: "+login.getBody().getValue());

		HTMLEntity answer = NodeProxyTCP.postHTTP(login, "api/games/create", NodeProxyTCP.HEADER_PLAIN, "name", "Springfield");
		System.out.println(answer.getStatusCode()+": "+answer.getStatusMessage());
		System.out.println("BODY: "+answer.getBody().getValue());
	}

}
